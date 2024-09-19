package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class ConnectToDataBase {
    private static Session session;

    private static SessionFactory sessionFactory;

    // Singleton pattern to ensure only one session factory instance
    private static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            // Add ALL of your entities here. You can also try adding a whole package.
            configuration.addAnnotatedClass(Movie.class);
            configuration.addAnnotatedClass(User.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }


    static List<Movie> getAllMovies() throws Exception {
        Session session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root);

            List<Movie> data = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return data;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback transaction if an exception occurs
            }
            e.printStackTrace();
            throw e; // rethrow the exception after logging
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static void updateShowtime(String title, LocalTime newShowtime) throws Exception {
        System.out.println("Update function reached...");
        SessionFactory sessionFactory = getSessionFactory();
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            // Using CriteriaBuilder to fetch the movie with the specified title
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root).where(builder.equal(root.get("title"), title));

            List<Movie> movies = session.createQuery(query).getResultList();

            if (movies.isEmpty()) {
                System.out.println("Movie with title \"" + title + "\" not found.");
                session.getTransaction().rollback();
                return;
            }

            Movie temp = movies.get(0);
            temp.setShowTime(newShowtime);
            session.update(temp);  // Use update instead of save
            session.getTransaction().commit();

            System.out.println("Updated showtime for movie: " + title);
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.out.println("Error updating showtime: " + e.getMessage());
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static Session initializeDatabase() throws IOException {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.clear();
            session.getTransaction().commit();
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return null;
    }

    public static void saveUser(User user) throws Exception {
        Session session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            user.setEmail(user.getEmail().toLowerCase());
            session.save(user);  // Save user to the database
            session.getTransaction().commit();
            System.out.println("User saved to database: " + user.getName());
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();  // Rollback transaction if there is an error
            }
            throw e;  // Rethrow the exception to handle it higher up
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


//    public static User getUserByEmailAndPassword(String email, String password) throws Exception {
//        Session session = null;
//        try {
//            session = getSessionFactory().openSession();
//            session.beginTransaction();
//
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<User> query = builder.createQuery(User.class);
//            Root<User> root = query.from(User.class);
//
//            // Check email and password
//            query.select(root).where(
//                    builder.and(
//                            builder.equal(root.get("email"), email),
//                            builder.equal(root.get("password"), password)
//                    )
//            );
//
//            List<User> users = session.createQuery(query).getResultList();
//            session.getTransaction().commit();
//
//            if (!users.isEmpty()) {
//                User user = users.get(0);
//                System.out.println("Found user in database: " + user.getEmail() + " with password: " + user.getPassword()); // Add this line to debug
//                return user;  // Return the first matching user
//            }
//            System.out.println("No user found with email: " + email + " and password: " + password); // Add this line to debug
//            return null;  // No user found
//        } catch (Exception e) {
//            if (session != null && session.getTransaction().isActive()) {
//                session.getTransaction().rollback();
//            }
//            throw e;  // Rethrow exception
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//
//    }


    public static User getUserByEmail(String email) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(builder.equal(root.get("email"), email));

            List<User> users = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            if (!users.isEmpty()) {
                return users.get(0);  // Return the user if found
            }
            return null;  // No user found
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw e;  // Rethrow exception
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static String getPasswordByEmail(String email) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<String> query = builder.createQuery(String.class);
            Root<User> root = query.from(User.class);

            // We are only selecting the password field
            query.select(root.get("password")).where(builder.equal(root.get("email"), email));

            List<String> passwords = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            if (!passwords.isEmpty()) {
                return passwords.get(0);  // Return the password if found
            }
            return null;  // No password found (user with the email doesn't exist)
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw e;  // Rethrow exception
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static void updateLoggedInStatus(String email, boolean status) throws Exception {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the user with the given email
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("email"), email));

            List<User> users = session.createQuery(query).getResultList();

            if (users.isEmpty()) {
                System.out.println("No user found with email: " + email);
                session.getTransaction().rollback();
                return;  // No user found, exit the method
            }

            // Update the loggedIn status of the found user
            User user = users.get(0);
            user.setLoggedIn(status);  // Assuming 'loggedIn' is a boolean field in your User class
            session.update(user);

            // Commit the transaction
            session.getTransaction().commit();

            System.out.println("Updated loggedIn status for user: " + user.getEmail() + " to: " + status);

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();  // Rollback on exception
            }
            throw e;  // Re-throw the exception to handle it at a higher level
        } finally {
            if (session != null) {
                session.close();  // Close the session to prevent leaks
            }
        }
    }


}






