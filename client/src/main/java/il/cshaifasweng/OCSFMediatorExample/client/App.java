package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class App extends Application {

    private static Scene scene;
    private SimpleClient client;

    // Cache to store scenes and avoid re-initialization
    private static Map<String, Parent> sceneCache = new HashMap<>();


    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("connection-detail"), 640, 480);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        EventBus.getDefault().register(this);

        // Handle window close request
        stage.setOnCloseRequest(event -> {
            event.consume();  // Consume the event, so we control when the window actually closes
            handleWindowClose(stage);
        });
    }

    private void handleWindowClose(Stage stage) {
        // Check if the user is logged in
        if (SimpleClient.getClient().isLoggedIn()) {
            try {
                // Send logout request to the server
                String email = SimpleClient.getClient().getCurrentUser().getEmail();
                if (email != null) {
                    SimpleClient.getClient().sendLogoutRequest(email);
                }

                // Close the application after logout
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Close the application directly if not logged in
            stage.close();
        }
    }



    public static <T> T getController(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        loader.load();  // Load the FXML file
        return loader.getController();  // Return the associated controller
    }






    public static void loadScene(String fxml) throws IOException {
        Parent root;
        if (sceneCache.containsKey(fxml)) {
            root = sceneCache.get(fxml);  // Retrieve cached scene
        } else {
            root = loadFXML(fxml);
            sceneCache.put(fxml, root);  // Cache the scene after loading
        }
        scene.setRoot(root);
    }




    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void loadSceneWithLoading(String fxml, Runnable serverTask) throws IOException {
        // First, show the loading scene
        Parent loadingRoot = loadFXML("loading");
        scene.setRoot(loadingRoot);

        // Run the server task in a separate thread to prevent blocking the UI thread
        new Thread(() -> {
            // Simulate the server operation
            try {
                // Perform the actual server task (e.g., fetch movies)
                serverTask.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // After the server task is complete, switch to the desired scene
            Platform.runLater(() -> {
                try {
                    loadScene(fxml);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }




    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
		super.stop();
	}

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});

    }

	public static void main(String[] args) {
        launch();
    }

}