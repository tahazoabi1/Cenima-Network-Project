package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Admin;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import java.io.IOException;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private User currentUser;  // Store the currently logged-in user (could be Admin or User)


	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof String) {
			String message = (String) msg;
			Platform.runLater(() -> handleStringMessage(message));
		} else if (msg instanceof List) {
			// Assuming it's a list of Movie objects
			List<Movie> movieList = (List<Movie>) msg;
			Platform.runLater(() -> handleMovieList(movieList));
		} else if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}

	}

	private void handleStringMessage(String message) {
		if (message.startsWith("Registration successful!")) {
			handleRegistrationSuccess(message);
		} else if (message.startsWith("Registration failed")) {
			handleRegistrationFailure(message);
		} else if (message.startsWith("Login successful!")) {
			handleLoginSuccess(message);
		} else if (message.startsWith("Login failed")) {
			handleLoginFailure(message);
		} else if (message.startsWith("Logout successful")) {
			handleLogoutSuccess();
		}  else if (message.startsWith("EMAIL_EXISTS")) {
			RegisterController.getInstance().handleEmailCheckResponse(true);
		} else if (message.startsWith("EMAIL_NOT_EXISTS")) {
			RegisterController.getInstance().handleEmailCheckResponse(false);
		}
	}



	private void handleRegistrationSuccess(String message) {
		String email = message.split("!")[1].trim();

		// Check if the user is an Admin (server will append "[Admin]" in the message)
		if (message.contains("[Admin]")) {
			this.currentUser = new Admin();
		} else {
			this.currentUser = new User();
		}
		this.currentUser.setEmail(email);
		this.currentUser.setLoggedIn(true);

		try {
			App.setRoot("edit-movies");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private void handleRegistrationFailure(String message) {
		String errorMessage = message.split(":")[1].trim();  // Get the reason for registration failure
		Platform.runLater(() -> {
			// Assuming you're using an Alert to show the error message in your UI
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Registration Failed");
			alert.setHeaderText("Registration failed");
			alert.setContentText("Error: " + errorMessage);
			alert.showAndWait();
		});
	}


	private void handleLoginSuccess(String message) {
		String email = message.split("!")[1].trim();

		// Check if the user is an Admin (server will append "[Admin]" in the message)
		if (message.contains("[Admin]")) {
			this.currentUser = new Admin();
		} else {
			this.currentUser = new User();
		}
		this.currentUser.setEmail(email);
		this.currentUser.setLoggedIn(true);

		try {
			App.setRoot("edit-movies");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private void handleLoginFailure(String message) {
		// Extract the reason for login failure from the message
		String errorMessage = message.split(":")[1].trim();
		Platform.runLater(() -> {
			// Assuming you want to use an Alert to show the error message
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Login Failed");
			alert.setHeaderText("Login Error");
			alert.setContentText("Login failed: " + errorMessage);
			alert.showAndWait();
		});
	}


	private void handleLogoutSuccess() {
		System.out.println("Logout successful for: " + currentUser.getEmail());

		// Reset current user details
		currentUser = null;

		Platform.runLater(() -> {
			try {
				// Set the root to the start page
				App.setRoot("start");

				// Ensure the button visibility is updated after logout
				NavBarController navController = App.getController("nav-bar"); // Assuming this gets the NavBarController
				navController.updateButtonVisibility();

				StartController startController = App.getController("start");  // Assuming this gets the StartController
				startController.checkLoginStatus();  // This ensures buttons in the start page are updated

				System.out.println("UI updated after logout.");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}


	private void handleMovieList(List<Movie> movieList) {
		EditMoviesController.movies.clear();
		EditMoviesController.movies.addAll(movieList);
		EditMoviesController.getInstance().updateMoviesListView();
	}

	public boolean isAdmin() {
		return currentUser instanceof Admin;
	}

	public boolean isLoggedIn() {
		return currentUser != null && currentUser.getLoggedIn();
	}

	public User getCurrentUser() {
		return currentUser;
	}


	// Method to send registration request
	public void sendRegistrationRequest(User user) throws IOException {
		sendToServer(user);
	}

	// Method to send login request
	public void sendLoginRequest(String email, String password) throws IOException {
		String loginRequest = "LOGIN_REQUEST$" + email + "$" + password;
		sendToServer(loginRequest);
	}

	public void sendLogoutRequest(String email) throws IOException {
		String logoutRequest = "LOGOUT_REQUEST$" + email;
		sendToServer(logoutRequest);
	}

	public static void initializeClient(String host, int port) {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			throw new IllegalStateException("Client not initialized. Call initializeClient() first.");
		}
		return client;
	}

	public void sendCheckEmailRequest(String email) throws IOException {
		String checkEmailRequest = "CHECK_EMAIL_REQUEST$" + email;
		sendToServer(checkEmailRequest);
	}


}
