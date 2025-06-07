package com.cdac.acts;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UserManagementSystem {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc";  
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "Pavan@1710";
	private static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found");
			e.printStackTrace();
			return;
		}
		createUserTable();
		boolean running = true;
		while(running) {
			display();
			System.out.println("Enter your choice: ");  
			
			int choice = sc.nextInt();
			sc.nextLine();
			
			switch(choice) {
				case 1:{
					registerUser();
					break;
				}
				case 2:{
					listUsersByCity();
					break;
				}
				case 3:{
					updatePassword();
					break;
				}
				case 4:{
					displayUserInfo();
					break;
				}
				case 5:{
					running = false;
					System.out.println("Exiting...");  
					sc.close();  
					System.exit(0);
					break;
				}
				default:{
					System.out.println("Invalid choice. Please try again.");  
				}
			}
		}
	}
	private static void display() {
		System.out.println("\nUser Management System");
        System.out.println("1. Register a User");
        System.out.println("2. List All Users by City");
        System.out.println("3. Update Password of a User");
        System.out.println("4. Display user information based on User Name");
        System.out.println("5. Exit");
	}
	private static void createUserTable() {
		String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
				+ "username VARCHAR(50) PRIMARY KEY, "
				+ "name VARCHAR(100) NOT NULL, "
				+ "email VARCHAR(100) UNIQUE NOT NULL, " 
				+ "city VARCHAR(50), "
				+ "password VARCHAR(100) NOT NULL)";  
		try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			Statement statement = connection.createStatement()
			){
			statement.execute(createTableSQL);
			System.out.println("Table 'users' ready for operations.");
		}catch(SQLException e) {
			System.out.print("Error creating table: "+e.getMessage());
			e.printStackTrace();
		}
	}
	private static void registerUser() {
		System.out.println("\n Register a New user");
		
		System.out.println("Enter Username: ");
		String username = sc.nextLine();
		
		System.out.print("Enter Name: ");
        String name = sc.nextLine();
        
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        
        System.out.print("Enter City: ");
        String city = sc.nextLine();
        
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        
        String sql = "INSERT INTO users (username, name, email, city, password) VALUES (?, ?, ?, ?, ?)";
        
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        	PreparedStatement statement = connection.prepareStatement(sql)
        	){
        	if(userExists(connection, username)) {
        		System.out.println("Username already exists. Please choose a different one.");  
        		return;
        	}
        	statement.setString(1, username);
        	statement.setString(2, name);
        	statement.setString(3, email);
        	statement.setString(4, city);
        	statement.setString(5, password);
        	
        	int rowsInserted = statement.executeUpdate();
        	if(rowsInserted > 0) {
        		System.out.println("User registered successfully!");
        	}
        }catch(SQLException e) {
        	System.out.println("Error registering user: "+e.getMessage());
        	if(e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {  
        		System.out.println("Email already exists. Please use a different email address.");
        	}
        }
	}
	private static void listUsersByCity() {
		 System.out.println("\nList Users by City");
		 System.out.println("Enter City: ");
		 String city = sc.nextLine();
		 String sql = "select username,name,email from users where city = ?";
		 
		 try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	             PreparedStatement statement = connection.prepareStatement(sql)) {
			 
			 statement.setString(1, city);
			 
			 try(ResultSet resultSet = statement.executeQuery()){
				 System.out.println("\nUsers in " + city + ":");
	                System.out.printf("%-20s %-20s %-30s%n", "Username", "Name", "Email");
	                System.out.println("------------------------------------------------------------");
	                
	                boolean found = false;
	                while(resultSet.next()) {
	                	found=true;
	                	String username = resultSet.getString("username");
	                    String name = resultSet.getString("name");
	                    String email = resultSet.getString("email");
	                    System.out.printf("%-20s %-20s %-30s%n", username, name, email);
	                }
	                if (!found) {
	                    System.out.println("No users found in " + city);
	                }
			 }
		 }catch(SQLException e) {
			 System.err.println("Error listing users: " + e.getMessage());
		 }
	}
	private static void updatePassword() {
System.out.println("\nUpdate User Password");
        
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        
        System.out.print("Enter New Password: ");
        String newPassword = sc.nextLine();

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            // Check if user exists
            if (!userExists(connection, username)) {
                System.out.println("User not found.");
                return;
            }
            
            statement.setString(1, newPassword);
            statement.setString(2, username);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Failed to update password.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
	}
	private static void displayUserInfo() {
System.out.println("\nDisplay User Information");
        
        System.out.print("Enter Username: ");
        String username = sc.nextLine();

        String sql = "SELECT username, name, email, city FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("\nUser Details:");
                    System.out.println("Username: " + resultSet.getString("username"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Email: " + resultSet.getString("email"));
                    System.out.println("City: " + resultSet.getString("city"));
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user information: " + e.getMessage());
        }
	}
	
	private static boolean userExists(Connection connection, String username) throws SQLException {
		String sql = "SELECT username FROM users WHERE username = ?";
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, username);
			try(ResultSet resultSet = statement.executeQuery()){
				return resultSet.next();
			}
		}
	}
}
