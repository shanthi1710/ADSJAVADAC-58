package assignment.assignment;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        Configuration hibernateConfiguration = null;
        SessionFactory hibernateFactory = null;
        Session hibernateSession = null;
        
        try {
            hibernateConfiguration = new Configuration();
            hibernateConfiguration.configure("hibernate.cfg.xml");
            hibernateFactory = hibernateConfiguration.buildSessionFactory();
            hibernateSession = hibernateFactory.openSession();
            
            Scanner scanner = new Scanner(System.in);
            int choice;
            
            do {
                System.out.println("\n===== User Management Menu =====");
                System.out.println("1. Add User (persist)");
                System.out.println("2. Find User by Username (get)");
                System.out.println("3. Load User by Username (load)");
                System.out.println("4. Update User");
                System.out.println("5. Delete User (remove)");
                System.out.println("6. Merge User (detached to persistent)");
                System.out.println("7. List All Users (read)");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");
                
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        persistUser(hibernateSession, scanner);
                        break;
                    case 2:
                        findUser(hibernateSession, scanner);
                        break;
                    case 3:
                        break;
                    case 4:
                        updateUser(hibernateSession, scanner);
                        break;
                    case 5:
                        removeUser(hibernateSession, scanner);
                        break;
                    case 6:
                        mergeUser(hibernateSession, scanner);
                        break;
                    case 7:
                        listAllUsers(hibernateSession);
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 8);
            
            scanner.close();
            
        } finally {
            if (hibernateSession != null) {
                hibernateSession.close();
            }
            if (hibernateFactory != null) {
                hibernateFactory.close();
            }
        }
    }
    
    private static void persistUser(Session session, Scanner scanner) {
        System.out.println("\n--- Add New User ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        Users newUser = new Users(username, password, email);
        
        session.beginTransaction();
        session.persist(newUser);
        session.getTransaction().commit();
        
        System.out.println("User added successfully!");
    }
    
    private static void findUser(Session session, Scanner scanner) {
        System.out.println("\n--- Find User (get) ---");
        System.out.print("Enter username to find: ");
        String username = scanner.nextLine();
        
        Users user = session.get(Users.class, username);
        
        if (user != null) {
            System.out.println("User found:");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Email: " + user.getEmail());
        } else {
            System.out.println("User not found with username: " + username);
        }
    }
    
    
    private static void updateUser(Session session, Scanner scanner) {
        System.out.println("\n--- Update User ---");
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine();
        
        Users user = session.get(Users.class, username);
        
        if (user != null) {
            System.out.println("Current details:");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Email: " + user.getEmail());
            
            System.out.print("Enter new password (leave blank to keep current): ");
            String password = scanner.nextLine();
            
            System.out.print("Enter new email (leave blank to keep current): ");
            String email = scanner.nextLine();
            
            session.beginTransaction();
            
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            if (!email.isEmpty()) {
                user.setEmail(email);
            }
            
            session.merge(user);
            session.getTransaction().commit();
            
            System.out.println("User updated successfully!");
        } else {
            System.out.println("User not found with username: " + username);
        }
    }
    
    private static void removeUser(Session session, Scanner scanner) {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine();
        
        Users user = session.get(Users.class, username);
        
        if (user != null) {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("User not found with username: " + username);
        }
    }
    
    private static void mergeUser(Session session, Scanner scanner) {
        System.out.println("\n--- Merge User (detached to persistent) ---");
        System.out.print("Enter username to merge: ");
        String username = scanner.nextLine();
        
        // First get the user to demonstrate merge
        Users detachedUser = session.get(Users.class, username);
        
        if (detachedUser != null) {
            System.out.println("Current details:");
            System.out.println("Username: " + detachedUser.getUsername());
            System.out.println("Password: " + detachedUser.getPassword());
            System.out.println("Email: " + detachedUser.getEmail());
            
            // Detach the user
            session.evict(detachedUser);
            System.out.println("User detached from session.");
            
            // Make changes to detached object
            System.out.print("Enter new password (leave blank to keep current): ");
            String password = scanner.nextLine();
            
            System.out.print("Enter new email (leave blank to keep current): ");
            String email = scanner.nextLine();
            
            if (!password.isEmpty()) {
                detachedUser.setPassword(password);
            }
            if (!email.isEmpty()) {
                detachedUser.setEmail(email);
            }
            
            // Merge the changes back
            session.beginTransaction();
            Users mergedUser = (Users) session.merge(detachedUser);
            session.getTransaction().commit();
            
            System.out.println("User merged successfully!");
            System.out.println("Merged details:");
            System.out.println("Username: " + mergedUser.getUsername());
            System.out.println("Password: " + mergedUser.getPassword());
            System.out.println("Email: " + mergedUser.getEmail());
        } else {
            System.out.println("User not found with username: " + username);
        }
    }
    
    private static void listAllUsers(Session session) {
        System.out.println("\n--- List All Users ---");
        
        List<Users> users = session.createQuery("from Users", Users.class).list();
        
        if (users.isEmpty()) {
            System.out.println("No users found in database.");
        } else {
            System.out.println("Total users: " + users.size());
            for (Users user : users) {
                System.out.println("Username: " + user.getUsername() + 
                                 ", Password: " + user.getPassword() + 
                                 ", Email: " + user.getEmail());
            }
        }
    }
}