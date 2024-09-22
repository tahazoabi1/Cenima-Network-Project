package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class StartController {
    @FXML
    public Button registerBtn, loginBtn;
    @FXML
    private HBox navBarContainer;
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() throws IOException {
        loadNavBar();
        checkLoginStatus();
    }


    private void loadNavBar() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("nav-bar.fxml"));
        Pane navBar = loader.load();
        navBarContainer.getChildren().add(navBar);
    }

    public void checkLoginStatus() {
        boolean isLoggedIn = SimpleClient.getClient().isLoggedIn();
        loginBtn.setVisible(!isLoggedIn);
        registerBtn.setVisible(!isLoggedIn);
    }

    @FXML
    public void proceedToLogin(ActionEvent event) throws IOException {
        App.setRoot("login");
    }

    @FXML
    public void proceedToRegister(ActionEvent event) throws IOException {
        App.setRoot("register");
    }
}
