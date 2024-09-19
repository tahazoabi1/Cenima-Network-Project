package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RegisterController {

    // Static instance for singleton pattern
    private static RegisterController instance;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label errorMessage;

    public RegisterController() {
        instance = this;  // Initialize the instance in the constructor
    }

    // Method to get the singleton instance
    public static RegisterController getInstance() {
        return instance;
    }

    @FXML
    public void registerUser(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields must be filled in!");
            return;
        }

        // Simple email validation
        if (!email.contains("@") || !email.contains(".")) {
            showError("Invalid email address.");
            return;
        }

        // Check if the email already exists in the database
        SimpleClient.getClient().sendCheckEmailRequest(email);
    }

    public void handleEmailCheckResponse(boolean emailExists) {
        if (emailExists) {
            showError("Email already exists. Please choose a different one.");
        } else {
            // If email doesn't exist, proceed with registration
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            User user = new User(name, email, password);
            try {
                SimpleClient.getClient().sendRegistrationRequest(user); // Assuming server handles DB operations
                showSuccess("Registration Successful!", "You have successfully registered.");
            } catch (IOException e) {
                e.printStackTrace();
                showError("An error occurred during registration. Please try again.");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
