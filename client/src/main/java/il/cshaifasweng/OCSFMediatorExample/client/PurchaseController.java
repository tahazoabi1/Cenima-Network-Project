/**
 * Sample Skeleton for 'purchase-page.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class PurchaseController {
    private Movie selectedMovie = null;
    private static PurchaseController instance;

    public PurchaseController() {
        instance = this;  // Initialize the instance in the constructor
    }

    public static PurchaseController getInstance() {
        return instance;
    }

    @FXML
    void purchaseATicket(MouseEvent event) {

    }
    public void setMovie(Movie movie) {
        this.selectedMovie = movie;
    }
}
