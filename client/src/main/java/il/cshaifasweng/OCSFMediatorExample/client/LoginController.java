package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    // Static instance
    private static LoginController instance;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label emailLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button loginButton;

    // Constructor
    public LoginController() {
        instance = this;  // Initialize the static instance when the controller is created
    }

    public static LoginController getInstance() {
        return instance;  // Provide access to the instance
    }

    @FXML
    void login(ActionEvent event) throws IOException {
        String email = emailField.getText().trim().toLowerCase();  // Trim whitespace
        String password = passwordField.getText().trim();

        // Check if fields are empty
        if (email.isEmpty() && password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must fill all fields!.");
            return;

        }
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email cannot be empty.");
            return;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password cannot be empty.");
            return;
        }

        // Validate email format (a simple check for "@" and ".")
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format.");
            return;
        }

        // Send login request to the server
        try {
            SimpleClient.getClient().sendLoginRequest(email, password);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "An error occurred. Please try again.");
        }



    }

    // A simple email validation method
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    // Show Alert dialog
    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional, set header text to null for simplicity
        alert.setContentText(message);
        alert.showAndWait();
    }

}
