package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class NavBarController {

    @FXML private Button btnLogin, btnRegister, btnLogout, btnHome, btnEditMovies;

    @FXML
    private HBox hBox;

    @FXML
    public void initialize() {
        updateButtonVisibility();
    }

    public void updateButtonVisibility() {
        boolean isLoggedIn = SimpleClient.getClient().isLoggedIn();
        boolean isAdmin = SimpleClient.getClient().isAdmin();
        btnLogin.setVisible(!isLoggedIn);
        btnRegister.setVisible(!isLoggedIn);
        btnLogout.setVisible(isLoggedIn);
        btnEditMovies.setVisible(isAdmin);
    }

    @FXML
    public void navigateToHome() throws IOException {
        App.setRoot("start");
        updateButtonVisibility();

    }

    @FXML
    public void navigateToEditMovies() throws IOException {
        App.setRoot("edit-movies");
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
        String email = SimpleClient.getClient().getCurrentUser().getEmail();
        if (email != null) {
            SimpleClient.getClient().sendLogoutRequest(email);  // SimpleClient will handle redirection and button updates
        }
    }

}
