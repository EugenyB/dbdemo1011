package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private Connection connection;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        connection = createConnection();
        int m;
        while ((m=menu())!=0) {
            switch (m) {
                case 1: showAll(); break;
                case 2: addPerson(); break;
                case 3: deletePerson(); break;
                case 4: findByAge(); break;
            }
        }
    }

    private void findByAge() {
        System.out.println("Find people older than age");
        Scanner in = new Scanner(System.in);
        System.out.print("Age:");
        int ageToFind = in.nextInt();
        try (PreparedStatement statement = connection.prepareStatement("select * from person where age > ? order by id")) {
            statement.setInt(1, ageToFind);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println(id + " " + name + " " + age);
            }
            System.out.println("Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePerson() {
        System.out.println("Delete person");
        Scanner in = new Scanner(System.in);
        System.out.print("id:");
        int id = in.nextInt();
        try (PreparedStatement statement = connection.prepareStatement("delete from person where id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPerson() {
        System.out.println("Add person");
        Scanner in = new Scanner(System.in);
        System.out.print("name:");
        String name = in.nextLine();
        System.out.print("age :");
        int age = in.nextInt();
        try (PreparedStatement statement = connection.prepareStatement("insert into person (name, age) values (?, ?)")) {
//            statement.setInt(1, 3);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.executeUpdate();
            System.out.println("Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAll() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from person");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println(id + " " + name + " " + age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int menu() {
        System.out.println("------------------");
        System.out.println("1. Read All");
        System.out.println("2. Add Person");
        System.out.println("3. Delete Person");
        System.out.println("4. Find By Age");
        System.out.println("0. Exit");
        System.out.println("------------------");
        return new Scanner(System.in).nextInt();
    }

    private Connection createConnection() {
        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("connection.props"))) {
            props.load(reader);
            connection = DriverManager.getConnection(props.getProperty("url"), props);
            return connection;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
