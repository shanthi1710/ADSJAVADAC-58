package firstHibernate.firstHibernate;

import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       Configuration hibernateConfiguration = null;
       SessionFactory hibernateFactory = null;
       Session hibernateSession = null;
       
       try {
    	   hibernateConfiguration = new Configuration();
    	   hibernateConfiguration.configure("hibernate.cfg.xml");
    	   hibernateFactory = hibernateConfiguration.buildSessionFactory();
    	   hibernateSession = hibernateFactory.openSession();
    	   
    	   try (Scanner scanner = new Scanner(System.in)){
    		   System.out.println("Enter the user name");
    		   String userName = scanner.next();
    		   System.out.println("Enter the user username");
    		   String password = scanner.next();
    		   System.out.println("Enter the user password");
    		   String email = scanner.next();
    		   
    		   Users objUser = new Users(userName,password,email);
    		   
    		   hibernateSession.beginTransaction();
    		   hibernateSession.persist(objUser);
    		   
    		   hibernateSession.getTransaction().commit();
    		   
    		   System.out.println("User registered!");
    	   }catch(Exception e) {
    		   e.printStackTrace();
    	   }
       }finally {
    	   if(hibernateSession != null)
    		   hibernateSession.close();
    	   if(hibernateFactory!= null)
    		   hibernateFactory.close();
       }
    }
}
