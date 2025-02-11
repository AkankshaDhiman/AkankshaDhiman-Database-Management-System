package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.sql.*;
import java.util.*;

class Mysql {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/akshi";
        String user = "root";
        String password = "Akshi2004@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully!");

            Statement stmt = con.createStatement();

            //Table created in Mysql database

            String sql = "CREATE TABLE IF NOT EXISTS emp (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    " name VARCHAR(50)," +
                    " age INT NOT NULL)";
            stmt.executeUpdate(sql);
            System.out.println("Table checked/created successfully!");

            // Insert the data

            for (int i = 1; i <= 50; i++) {
                String name = "Employee" + i;
                int age = 20 + i;
                String insertQuery = "INSERT INTO emp (name, age) VALUES (?, ?)";
                PreparedStatement pstmt = con.prepareStatement(insertQuery);
                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.executeUpdate();
                pstmt.close();
            }

            System.out.println("Data inserted successfully!");


            //This part of code display the inserted table data...

            sql = "SELECT * FROM emp";
            ResultSet rs1 = stmt.executeQuery(sql);

            // Process the results
            System.out.printf("%-10s%-20s%-10s\n", "ID", "Name", "Age");
            while (rs1.next()) {
                int id = rs1.getInt("id");
                String name = rs1.getString("name");
                int age = rs1.getInt("age");
                System.out.printf("%-10d%-20s%-10d\n", id, name, age);
            }
            rs1.close();


            //Ask the user to choose the file format....

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter in which format you want the data (XML/JSON): ");
            String data = scanner.nextLine();

            // Take user input for ID
            System.out.print("Enter Employee ID to fetch details: ");
            int empId = scanner.nextInt();
            scanner.nextLine();
            // Fetch Data by matching the id in the table name emp...

            String fetchQuery = "SELECT * FROM emp WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(fetchQuery);
            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();

            // Convert to JSON using Jackson
            //if the user enter the id and the id will match in the database then if  will execute ohterwise else will execute...
            if (rs.next()) {
                Map<String, Object> employee = new HashMap<>();
                employee.put("id", rs.getInt("id"));
                employee.put("name", rs.getString("name"));
                employee.put("age", rs.getInt("age"));

                // Convert the data into json and xml...
                ObjectMapper objectMapper = new ObjectMapper();
                XmlMapper xmlMapper = new XmlMapper();
                String xml = xmlMapper.writeValueAsString(employee);
                String jsonOutput = objectMapper.writeValueAsString(employee);
                if (data.equalsIgnoreCase("JSON")) {   //Check the condition if user enter JSON then it will execute
                    System.out.println("Employee Details (JSON Format):\n" + jsonOutput);  //Check the condition if user enter XML then it will execute
                } else if (data.equalsIgnoreCase("XML")) {
                    System.out.println("Employee Details (XML Format):\n" + xml);
                } else {
                    System.out.println("Invalid format. Please enter XML or JSON.");
                }
            } else {
                System.out.println("No employee found with ID: " + empId);
            }

            rs.close();
            pstmt.close();
            stmt.close();
            con.close();
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}