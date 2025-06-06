package com.cdac.acts;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TableCreator {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pavan@1710";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found");
            e.printStackTrace();
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\nDatabase Table Manager");
            System.out.println("1. Create New Table");
            System.out.println("2. Display Columns of a Table");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  

            switch (choice) {
                case 1:
                    createTable();
                    break;
                case 2:
                    displayTableColumns();
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void createTable() {
        System.out.println("\nCreate New Table");
        System.out.print("Enter table name: ");
        String tableName = scanner.nextLine();

        List<Column> columns = new ArrayList<>();
        String primaryKey = null;

        boolean editing = true;
        while (editing) {
            System.out.println("\nTable: " + tableName);
            System.out.println("Current Columns:");
            if (columns.isEmpty()) {
                System.out.println("(No columns defined yet)");
            } else {
                for (int i = 0; i < columns.size(); i++) {
                    Column col = columns.get(i);
                    System.out.printf("%d. %s %s%s%n", 
                        i + 1, col.name, col.type, 
                        (col.name.equals(primaryKey) ? " (PRIMARY KEY)" : ""));
                }
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add Column");
            System.out.println("2. Set Primary Key");
            System.out.println("3. Save Table");
            System.out.println("4. Cancel");
            System.out.print("Select option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); 
            switch (option) {
                case 1:
                    addColumn(columns);
                    break;
                case 2:
                    primaryKey = setPrimaryKey(columns);
                    break;
                case 3:
                    saveTable(tableName, columns, primaryKey);
                    editing = false;
                    break;
                case 4:
                    System.out.println("Table creation cancelled.");
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addColumn(List<Column> columns) {
        System.out.print("\nEnter column name: ");
        String columnName = scanner.nextLine();

        System.out.println("\nSelect data type:");
        System.out.println("1. VARCHAR (String)");
        System.out.println("2. INT (Integer)");
        System.out.println("3. FLOAT (Decimal)");
        System.out.print("Enter choice: ");
        
        int typeChoice = scanner.nextInt();
        scanner.nextLine();  
        
        String dataType;
        switch (typeChoice) {
            case 1:
                System.out.print("Enter length for VARCHAR (e.g., 50): ");
                int length = scanner.nextInt();
                scanner.nextLine();  
                dataType = "VARCHAR(" + length + ")";
                break;
            case 2:
                dataType = "INT";
                break;
            case 3:
                dataType = "FLOAT";
                break;
            default:
                System.out.println("Invalid choice. Using VARCHAR(50) by default.");
                dataType = "VARCHAR(50)";
        }

        columns.add(new Column(columnName, dataType));
        System.out.println("Column added successfully.");
    }

    private static String setPrimaryKey(List<Column> columns) {
        if (columns.isEmpty()) {
            System.out.println("No columns available to set as primary key.");
            return null;
        }

        System.out.println("\nSelect primary key column:");
        for (int i = 0; i < columns.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, columns.get(i).name);
        }

        System.out.print("Enter column number: ");
        int colNum = scanner.nextInt();
        scanner.nextLine();  
        if (colNum < 1 || colNum > columns.size()) {
            System.out.println("Invalid column number.");
            return null;
        }

        String pk = columns.get(colNum - 1).name;
        System.out.println(pk + " set as primary key.");
        return pk;
    }

    private static void saveTable(String tableName, List<Column> columns, String primaryKey) {
        if (columns.isEmpty()) {
            System.out.println("Cannot save table with no columns.");
            return;
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
    
        for (Column col : columns) {
            sql.append(col.name).append(" ").append(col.type).append(", ");
        }
    
        if (primaryKey != null) {
            sql.append("PRIMARY KEY (").append(primaryKey).append(")");
        } else {
             
            sql.setLength(sql.length() - 2);
        }
        
        sql.append(")");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            
            statement.executeUpdate(sql.toString());
            System.out.println("Table '" + tableName + "' created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            if (e.getMessage().contains("already exists")) {
                System.out.println("A table with this name already exists.");
            }
        }
    }

    private static void displayTableColumns() {
        System.out.println("\nDisplay Table Columns");
        System.out.print("Enter table name: ");
        String tableName = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                System.out.println("\nColumns in table '" + tableName + "':");
                
                boolean hasColumns = false;
                while (columns.next()) {
                    hasColumns = true;
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println("- " + columnName + " (" + columnType + ")");
                }
                
                if (!hasColumns) {
                    System.out.println("No columns found or table doesn't exist.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving table information: " + e.getMessage());
        }
    }

    private static class Column {
        String name;
        String type;

        Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
