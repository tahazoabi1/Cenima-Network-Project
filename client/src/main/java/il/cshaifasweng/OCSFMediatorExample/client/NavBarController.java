package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class NavBarController {

    @FXML private Button btnLogin, btnRegister, btnLogout, btnHome, btnListMovies;

    @FXML
    private HBox hBox;

    @FXML
    public void initialize() {
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        boolean isLoggedIn = SimpleClient.getClient().isLoggedIn();
        btnLogin.setVisible(!isLoggedIn);
        btnRegister.setVisible(!isLoggedIn);
        btnLogout.setVisible(isLoggedIn);
    }

    @FXML
    public void navigateToHome() throws IOException {
        App.setRoot("start");
        updateButtonVisibility();
    }

    @FXML
    public void navigateToListMovies() throws IOException {
        App.setRoot("list-movies");
        updateButtonVisibility();
    }

    @FXML
    public void navigateToLogin() throws IOException {
        App.setRoot("login");
        updateButtonVisibility();
    }

    @FXML
    public void navigateToRegister() throws IOException {
        App.setRoot("register");
        updateButtonVisibility();
    }

    @FXML
    public void logout() throws IOException {
        String email = SimpleClient.getClient().getEmail();
        if (email != null) {
            SimpleClient.getClient().sendLogoutRequest(email);
            App.setRoot("start");
            updateButtonVisibility();  // Ensure the logout button visibility is updated
        }
    }
}
