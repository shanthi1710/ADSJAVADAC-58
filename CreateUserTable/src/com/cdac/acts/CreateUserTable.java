package com.cdac.acts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserTable {
    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found");
            e.printStackTrace();
            return;
        }
       
        String url = "jdbc:mysql://localhost:3306/jdbc";
		String user ="root";
		String password = "Pavan@1710";
        
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                              "username VARCHAR(50) PRIMARY KEY, " +
                              "name VARCHAR(100) NOT NULL, " +
                              "email VARCHAR(100) UNIQUE NOT NULL, " +
                              "city varchar(50), "+
                              "password VARCHAR(100) NOT NULL)";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            
            statement.execute(createTableSQL);
            System.out.println("Table 'users' created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}