package il.cshaifasweng.OCSFMediatorExample.server;

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
		// Handle registration (User object)
		if (msg instanceof User) {
			User user = (User) msg;
			try {
				ConnectToDataBase.saveUser(user);  // Save user to the database
				client.sendToClient("Registration successful!" + user.getEmail());  // Send success message back to client
				System.out.println("User registered: " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
				ConnectToDataBase.updateLoggedInStatus(user.getEmail(),true);
				user.setLoggedIn(true);
			} catch (Exception e) {
				System.out.println("Error registering user: " + user.getEmail() + e.getMessage());
				client.sendToClient("Registration failed: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// Handle login request (message is a string with credentials)
		else if (msg instanceof String) {
			String msgString = (String) msg;
			System.out.println("Message received: " + msgString);



			if (msgString.startsWith("LOGIN_REQUEST")) {
				String[] parts = msgString.split("\\$");
				String email = parts[1].toLowerCase();
				String password = parts[2];

				User user = ConnectToDataBase.getUserByEmail(email);
				// Check login credentials
				if (user == null) {
					client.sendToClient("Login failed: Unregistered email.");
				} else if (!(ConnectToDataBase.getPasswordByEmail(user.getEmail()).equals(password))) {
					client.sendToClient("Login failed: Incorrect password.");
				} else {
					client.sendToClient("Login successful!" + email);
					ConnectToDataBase.updateLoggedInStatus(user.getEmail(), true);
					user.setLoggedIn(true);
					System.out.println("Logged in: " + email);

				}
			} else if (msgString.startsWith("CHECK_EMAIL_REQUEST")) {
				String email = msgString.split("\\$")[1];
				boolean emailExists = ConnectToDataBase.getUserByEmail(email) != null;

				if (emailExists) {
					client.sendToClient("EMAIL_EXISTS");
				} else {
					client.sendToClient("EMAIL_NOT_EXISTS");
				}
				
			}  else if (msgString.startsWith("LOGOUT_REQUEST")){
				System.out.println("Inside Logout Request");
				System.out.println(msgString);
				String[] parts = msgString.split("\\$");
				String email = parts[1].toLowerCase();
				User user = ConnectToDataBase.getUserByEmail(email);
				if (user != null) {
					ConnectToDataBase.updateLoggedInStatus(email, false);
					user.setLoggedIn(false);
					client.sendToClient("Logout successful");
					System.out.println("Logged out: " + email);
				}

			}

			// Handle other requests like getting movies or updating showtimes...
			else if (msgString.startsWith("get me movies")) {
				List<Movie> movies = ConnectToDataBase.getAllMovies();
				client.sendToClient(movies);
			} else if (msgString.startsWith("Update time")) {
				String[] parts = msgString.split("@");
				LocalTime time = LocalTime.parse(parts[1]);
				ConnectToDataBase.updateShowtime(parts[2], time);
				List<Movie> movies = ConnectToDataBase.getAllMovies();
				client.sendToClient(movies);
			}
		}
	}


}
