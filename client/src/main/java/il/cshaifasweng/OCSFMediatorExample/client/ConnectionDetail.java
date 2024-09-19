package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionDetail {

    @FXML
    private Button connectToServerBtn;

    @FXML
    private TextField hostAddress;

    @FXML
    private TextField portNum;

    @FXML
    private Label emptyAddressLabel;

    @FXML
    private Label emptyPortLabel;

    @FXML
    private Label invalidAddressLabel;

    @FXML
    private Label invalidPortLabel;

    @FXML
    void connectToServer(ActionEvent event) throws IOException {
        boolean valid = true;

        // Validate host address
        if (hostAddress.getText().isEmpty()) {
            emptyAddressLabel.setVisible(true);
            valid = false;
        } else {
            emptyAddressLabel.setVisible(false);
            if (!isValidHost(hostAddress.getText())) {
                invalidAddressLabel.setVisible(true);
                valid = false;
            } else {
                invalidAddressLabel.setVisible(false);
            }
        }

        // Validate port number
        if (portNum.getText().isEmpty()) {
            emptyPortLabel.setVisible(true);
            valid = false;
        } else {
            emptyPortLabel.setVisible(false);
            if (!isValidPort(portNum.getText())) {
                invalidPortLabel.setVisible(true);
                valid = false;
            } else {
                invalidPortLabel.setVisible(false);
            }
        }

        if (valid) {
            String host = hostAddress.getText();
            int port = Integer.parseInt(portNum.getText());

            SimpleClient.initializeClient(host, port);
            SimpleClient.getClient().openConnection();

            App.loadScene("start");
        }
    }

    private boolean isValidHost(String host) {
        try {
            InetAddress.getByName(host);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isValidPort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
