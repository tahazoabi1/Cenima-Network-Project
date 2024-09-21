package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Admin;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.time.LocalTime;
import java.util.List;

public class SimpleServer extends AbstractServer {

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) throws Exception {
		if (msg instanceof User) {
			handleUserRegistration((User) msg, client);
		} else if (msg instanceof String) {
			handleStringMessage((String) msg, client);
		}
	}


	private void handleStringMessage(String message, ConnectionToClient client) throws Exception {
		System.out.println("Message received: " + message);

		if (message.startsWith("LOGIN_REQUEST")) {
			handleLoginRequest(message, client);
		} else if (message.startsWith("CHECK_EMAIL_REQUEST")) {
			handleEmailCheckRequest(message, client);
		} else if (message.startsWith("LOGOUT_REQUEST")) {
			handleLogoutRequest(message, client);
		} else if (message.startsWith("get me movies")) {
			handleMovieRequest(client);
		} else if (message.startsWith("Update time")) {
			handleUpdateTimeRequest(message, client);
		}
	}

	private void handleUserRegistration(User user, ConnectionToClient client) throws Exception {
		try {
			// Check if the user being registered is an Admin
			if (user instanceof Admin) {
				ConnectToDataBase.saveUser(user);  // Save the Admin to the database
				client.sendToClient("Registration successful!" + user.getEmail() + " [Admin]");
				System.out.println("Admin registered: " + user.getName() + " " + user.getEmail());
			} else {
				ConnectToDataBase.saveUser(user);  // Save the regular User to the database
				client.sendToClient("Registration successful!" + user.getEmail());
				System.out.println("User registered: " + user.getName() + " " + user.getEmail());
			}

			// Update the loggedIn status
			ConnectToDataBase.updateLoggedInStatus(user.getEmail(), true);
			user.setLoggedIn(true);
		} catch (Exception e) {
			System.out.println("Error registering user: " + user.getEmail() + " " + e.getMessage());
			client.sendToClient("Registration failed: " + e.getMessage());
			e.printStackTrace();
		}
	}


	private void handleLoginRequest(String message, ConnectionToClient client) throws Exception {
		String[] parts = message.split("\\$");
		String email = parts[1].toLowerCase();
		String password = parts[2];

		User user = ConnectToDataBase.getUserByEmail(email);
		if (user == null) {
			client.sendToClient("Login failed: Unregistered email.");
		} else if (!user.getPassword().equals(password)) {
			client.sendToClient("Login failed: Incorrect password.");
		} else {
			// Check if the user is an Admin
			String responseMessage = "Login successful!" + email;
			if (user instanceof Admin) {
				responseMessage += " [Admin]";
			}
			client.sendToClient(responseMessage);
			ConnectToDataBase.updateLoggedInStatus(email, true);
			user.setLoggedIn(true);
			System.out.println("Logged in: " + email + (user instanceof Admin ? " [Admin]" : ""));
		}
	}

	private void handleEmailCheckRequest(String message, ConnectionToClient client) throws Exception {
		String email = message.split("\\$")[1];
		boolean emailExists = ConnectToDataBase.getUserByEmail(email) != null;
		client.sendToClient(emailExists ? "EMAIL_EXISTS" : "EMAIL_NOT_EXISTS");
	}

	private void handleLogoutRequest(String message, ConnectionToClient client) throws Exception {
		String[] parts = message.split("\\$");
		String email = parts[1].toLowerCase().split(" ")[0];  // Split on space and take only the email part

		User user = ConnectToDataBase.getUserByEmail(email);  // Query only with the clean email
		if (user != null) {
			System.out.println("Attempting to log out: " + email);
			System.out.println("User type: " + (user instanceof Admin ? "Admin" : "User")); // Log the type

			ConnectToDataBase.updateLoggedInStatus(email, false);  // Log out the user
			user.setLoggedIn(false);

			client.sendToClient("Logout successful");
			System.out.println("Logged out: " + email);
		} else {
			System.out.println("User not found for logout: " + email);
		}
	}


	private void handleMovieRequest(ConnectionToClient client) throws Exception {
		List<Movie> movies = ConnectToDataBase.getAllMovies();
		client.sendToClient(movies);
	}

	private void handleUpdateTimeRequest(String message, ConnectionToClient client) throws Exception {
		String[] parts = message.split("@");
		LocalTime time = LocalTime.parse(parts[1]);
		ConnectToDataBase.updateShowtime(parts[2], time);
		List<Movie> movies = ConnectToDataBase.getAllMovies();
		client.sendToClient(movies);
	}
}
