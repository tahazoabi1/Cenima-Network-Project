package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EditMoviesController {

    @FXML
    private AnchorPane navBarContainer; // Placeholder for the nav-bar

    @FXML
    private ListView<Movie> moviesList;

    @FXML
    private TextField newShowTime;

    @FXML
    private Button updateShowTimeBTN;

    @FXML
    private Label updatedShowTimeLBL;


    private Movie movie;

    private static EditMoviesController instance;

    public static List<Movie> movies = new ArrayList<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Constructor
    public EditMoviesController() {
        instance = this;  // Assign the instance when the controller is created
    }

    // Provide access to the instance
    public static EditMoviesController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() throws IOException {
        // Load the navigation bar
        loadNavBar();

        SimpleClient.getClient().sendToServer("get me movies");  // Fetch the movies from the server

        // Add listener to set the selected movie when clicked
        moviesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.movie = newValue;  // Set the selected movie
            }
        });

        updateMoviesListView();
    }

    // Method to load the nav bar dynamically
    private void loadNavBar() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("nav-bar.fxml"));
        Pane navBar = loader.load();
        navBarContainer.getChildren().add(navBar);  // Add the navigation bar to the container
    }

    public void updateMoviesListView() {
        if (movies.isEmpty()) {
            moviesList.getItems().clear();  // Clear the list in case it's already populated
            return;
        }

        // Custom ListCell to load FXML for each movie item
        moviesList.setCellFactory(param -> new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);

                if (empty || movie == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Load the custom FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("movie_item.fxml"));
                        HBox hBox = loader.load();

                        // Access the FXML components
                        ImageView posterImage = (ImageView) hBox.lookup("#posterImage");
                        Label titleLabel = (Label) hBox.lookup("#titleLabel");
                        Label descriptionLabel = (Label) hBox.lookup("#descriptionLabel");
                        Label showTimeLabel = (Label) hBox.lookup("#showTimeLabel");

                        // Set the data for each movie
                        posterImage.setImage(new Image("file:" + movie.getPosterURL()));
                        titleLabel.setText(movie.getTitle());
                        descriptionLabel.setText(movie.getDescription());
                        showTimeLabel.setText("Showtime: " + movie.getShowTime().format(TIME_FORMATTER));

                        // Set the graphic for the cell
                        setGraphic(hBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setText("Error loading movie data");
                    }
                }
            }
        });

        // Add all movies to the ListView
        moviesList.getItems().setAll(movies);
    }

    private LocalTime parseTime(String timeString) {
        try {
            return LocalTime.parse(timeString, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @FXML
    void updateShowTime(ActionEvent event) throws IOException {
        // Check if a movie is currently selected
        if (moviesList.getSelectionModel().getSelectedItem() == null) {
            showCompletionMessage("Error", "Please select a movie to update");
            return;
        }

        if (newShowTime.getText().isEmpty()) {
            showCompletionMessage("Error", "Please enter time to update");
        } else {
            LocalTime newShowTimeParsed = parseTime(newShowTime.getText());
            if (newShowTimeParsed == null) {
                showCompletionMessage("Error", "Invalid time format, please enter time in HH:mm format");
                newShowTime.clear();
            } else {
                movie = moviesList.getSelectionModel().getSelectedItem();  // Get the selected movie

                updatedShowTimeLBL.setText("The show time of the movie \"" + movie.getTitle() + "\" has been updated from " +
                        movie.getShowTime().format(TIME_FORMATTER) + " to " + newShowTimeParsed.format(TIME_FORMATTER));

                showCompletionMessage("A show time has been updated", updatedShowTimeLBL.getText());

                // Update the movie's showtime and send the updated time to the server
                movie.setShowTime(newShowTimeParsed);
                SimpleClient.getClient().sendToServer("Update time @" + newShowTime.getText() + "@" + movie.getTitle());

                // Clear the selection after updating
                moviesList.getSelectionModel().clearSelection();
                this.movie = null;  // Reset the current movie
            }
        }
    }

    private void showCompletionMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void selectMovie(ActionEvent event) throws IOException {
        Movie selectedMovie = moviesList.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showCompletionMessage("Error", "Please select a movie first.");
            return;
        }
        else{
            App.setRoot("purchase-page");
            PurchaseController.getInstance().setMovie(selectedMovie);
        }
    }

}