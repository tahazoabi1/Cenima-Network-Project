package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.Entity;

@Entity
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    // Additional methods for admin-specific actions
    public void deleteMovie(Movie movie) {
        // Admin can delete a movie from the database
    }

    public void addMovie(Movie movie) {
        // Admin can add a movie to the database
    }

    public void updateMovie(Movie movie) {
        // Admin can update a movie in the database
    }
}
