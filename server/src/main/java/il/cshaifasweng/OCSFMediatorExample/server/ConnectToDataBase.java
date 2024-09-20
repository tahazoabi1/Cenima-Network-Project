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
    private static SessionFactory sessionFactory;

    // Ensures only one session factory is instantiated.
    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Movie.class).addAnnotatedClass(User.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
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
            CriteriaQuery<User> query = getUserByEmailQuery(session, email);
            List<User> users = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email", e);
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
                user.setLoggedIn(status);
                session.update(user);
                session.getTransaction().commit();
                System.out.println("Login status updated for: " + email);
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
}
