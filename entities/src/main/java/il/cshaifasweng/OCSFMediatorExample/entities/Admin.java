package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")  // Create a separate table for Admins // Link primary key with the 'users' table
public class Admin extends User {

    public Admin() {
        super();
        setAdmin(true);  // Ensure the 'isAdmin' field is set to true
    }


    public Admin(String name, String email, String password) {
        super(name, email, password);
        this.setAdmin(true);
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

    public void addAdmin(Admin admin) {
    }
}
