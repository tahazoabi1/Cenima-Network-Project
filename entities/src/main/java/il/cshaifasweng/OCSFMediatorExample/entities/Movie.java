package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "show_time")
    private LocalTime showTime;

    @Column(name = "genre")
    private String genre;

    @Column(name = "duration")
    private int duration; // in minutes

    @Column(name = "rating")
    private float rating;

    @Column(name = "director")
    private String director;

    @Column(name = "description")
    private String description;

    @ElementCollection
    @Column(name = "actors")
    private List<String> actors;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url")
    private String posterURL;

    // Default constructor
    public Movie() {
    }

    public Movie(String title, LocalTime showTime, String genre, int duration, float rating, String director, String description, List<String> actors, LocalDate releaseDate, String posterURL) {
        this.title = title;
        this.showTime = showTime;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.description = description;
        this.actors = actors;
        this.releaseDate = releaseDate;
        this.posterURL = posterURL;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }


//    @Override
//    public String toString() {
//        return String.format("Movie ID: %d\nTitle: %s\nRelease Date: %s\nGenre: %s\nDuration: %d minutes\nRating: %.1f\nDirector: %s\nDescription: %s\nActors: %s",
//                this.id, this.title, this.showTime, this.genre, this.duration, this.rating, this.director, this.description, String.join(", ", this.actors));
//    }

}