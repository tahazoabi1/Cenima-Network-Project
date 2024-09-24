package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Admin;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalTime;
import java.util.List;

public class ConnectToDataBase {
    private static SessionFactory sessionFactory;

    // Ensures only one session factory is instantiated.
    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Movie.class).addAnnotatedClass(User.class).addAnnotatedClass(Admin.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    public static void initializeDatabase() throws HibernateException {
        Session session = null;
        Transaction transaction = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Example: Clear specific tables or refresh schema
            // session.createQuery("DELETE FROM Movie").executeUpdate();
            // session.createQuery("DELETE FROM User").executeUpdate();
        Admin admin = new Admin("admin", "admin@admin.com", "admin");
        session.save(admin);
            transaction.commit();  // Commit changes to make sure they are applied
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();  // Ensure rollback on error
            throw new HibernateException("Failed to initialize database", e);
        } finally {
            if (session != null && session.isOpen()) session.close();  // Ensure session is closed
        }
    }



    // Fetch all movies from the database
    public static List<Movie> getAllMovies() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            query.from(Movie.class);
            List<Movie> movies = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return movies;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all movies", e);
        }
    }

    // Update movie showtime in the database
    public static void updateShowtime(String title, LocalTime newShowtime) throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            Movie movie = findMovieByTitle(session, title);
            if (movie != null) {
                movie.setShowTime(newShowtime);
                session.update(movie);
                session.getTransaction().commit();
                System.out.println("Updated showtime for movie: " + title);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update showtime", e);
        }
    }

    // Save a user to the database
    public static void saveUser(User user) throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            user.setEmail(user.getEmail().toLowerCase());
            session.save(user);
            session.getTransaction().commit();
            System.out.println("User saved: " + user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    // Fetch a user by email
    public static User getUserByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("email"), email));

            List<User> users = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            if (!users.isEmpty()) {
                User user = users.get(0);
                if (user instanceof Admin) {
                    return (Admin) user;  // Return as Admin if it's an Admin
                }
                return user;  // Return as a regular User
            }
            return null;
        }
    }

    // Fetch password by email
    public static String getPasswordByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<String> query = builder.createQuery(String.class);
            Root<User> root = query.from(User.class);
            query.select(root.get("password")).where(builder.equal(root.get("email"), email));
            List<String> passwords = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return passwords.isEmpty() ? null : passwords.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find password by email", e);
        }
    }

    // Update login status for a user
    public static void updateLoggedInStatus(String email, boolean status) throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = getUserByEmail(email);

            if (user != null) {
                System.out.println("Updating login status for: " + email + " (type: " + (user instanceof Admin ? "Admin" : "User") + ")");
                user.setLoggedIn(status);
                session.update(user);  // This should work for both Admin and User
                session.getTransaction().commit();
                System.out.println("Login status updated for: " + email);
            } else {
                System.out.println("No user found for login status update: " + email);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update login status", e);
        }
    }


    // Helper method to get user by email query
    private static CriteriaQuery<User> getUserByEmailQuery(Session session, String email) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(builder.equal(root.get("email"), email));
        return query;
    }

    // Helper method to find movie by title
    private static Movie findMovieByTitle(Session session, String title) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
        Root<Movie> root = query.from(Movie.class);
        query.select(root).where(builder.equal(root.get("title"), title));
        List<Movie> movies = session.createQuery(query).getResultList();
        return movies.isEmpty() ? null : movies.get(0);
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

}



