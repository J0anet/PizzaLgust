package application;
	
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	String first_name = null;
    boolean is_admin = false;
    String last_name = null;
    String msg = null;
    String token = null;
    String user_name = null;
    String user_type = null;
    
    private List<User> userList = new ArrayList<>();
    User selectedUser = null;
    
    int responseCode = 0;
    
    @FXML
    VBox loginScreen;
    
    @FXML
    VBox sessionProfile;
    
    @FXML
    VBox userListScreen;
    
    @FXML
    VBox registerUserScreen;
    
	@FXML
	TextField usernameField;
	
	@FXML
	TextField passwordField;
	
	@FXML
	TextField nameField;
	
	@FXML
	TextField lastnameField;
	
	@FXML
	Label errorLabel;
	
	@FXML
	Label userLabel;
	
	@FXML
	Label isAdminText;
	
	@FXML
	Label adminOptions;
	
	@FXML
	Button showUsers;
	
	@FXML
	ListView userListView;
	
	@FXML
	Button exitUserList;
	
	@FXML
	Button modifyButton;
	
	@FXML
	Button deleteUserButton;
	
	@FXML
	TextField usernameNew;
	
	@FXML
	TextField lastNameNew;
	
	@FXML
	TextField emailNew;
	
	@FXML
	TextField passwordNew;
	
	@FXML
	TextField nameNew;
	
	@FXML
	Button toRegisterScreenButton;
	
	@FXML
	Button logoutButton;
	
	@FXML
	Button createUserButton;
	
	/**
	 * Aquesta classe executa l'entorn gràfic del programa
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	@Override
	public void start(Stage primaryStage) {
		
		try {
			
			
			Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("PizzaLgust");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception errorStart) {
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Aquest mètode controla el login al clicar al botó de login
	 * @author Raul Luque Craciun
	 * @param e ActionEvent
	 */
	public void handleLogin(ActionEvent e) {
		try {
            URL url = new URL("http://localhost:5002/pizzalgust/login");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"email\": \""+usernameField.getText()+"\", \"password\": \""+passwordField.getText()+"\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Login Code: " + responseCode);
            
            if (responseCode == 410) {
            	errorLabel.setText("Email inexistent o incorrecte");
            	usernameField.setText("");
            	passwordField.setText("");
            } else if (responseCode == 401) {
            	errorLabel.setText("Contrasenya incorrecta");
            	passwordField.setText("");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                String json = response.toString();
                String[] pairs = json.replaceAll("[{}\"]", "").split(",");
                
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "first_name":
                            first_name = value;
                            break;
                        case "is_admin":
                            is_admin = Boolean.parseBoolean(value);
                            break;
                        case "last_name":
                            last_name = value;
                            break;
                        case "msg":
                            msg = value;
                            break;
                        case "token":
                            token = value;
                            break;
                        case "user_name":
                            user_name = value;
                            break;
                        case "user_type":
                        	user_type = value;
                        default:
                            break;
                    }
                }

                System.out.println("First Name: " + first_name);
                System.out.println("Last Name: " + last_name);
                System.out.println("User Name: " + user_name);
                System.out.println("Is Admin: " + is_admin);
                System.out.println("Message: " + msg);
                System.out.println("Token: " + token);
                System.out.println("User Type: " + user_type);
                
                userLabel.setText(user_name);
                nameField.setText(first_name);
                lastnameField.setText(last_name);
                
                if (is_admin) {
                	isAdminText.setVisible(true);
                	adminOptions.setVisible(true);
                	showUsers.setVisible(true);
                } else {
                	isAdminText.setVisible(false);
                	adminOptions.setVisible(false);
                	showUsers.setVisible(false);
                	
                }
                
                passwordField.setText("");
                
                loginScreen.setVisible(false);
                sessionProfile.setVisible(true);
            }
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (Exception errorHandleLogin) {
        }
	}
	
	/**
	 * Aquest mètode controla el logout al clicar el botó de logout
	 * @author Raul Luque Craciun
	 * @param e ActionEvent
	 */
	public void handleLogout(ActionEvent e) {
		try {
			URL url = new URL("http://localhost:5002/pizzalgust/logout");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"token\": \""+token+"\"}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Logout Code: " + responseCode);
            
            loginScreen.setVisible(true);
            sessionProfile.setVisible(false);
            
            responseCode = connection.getResponseCode();
            connection.disconnect();
		} catch(Exception errorHandleLogout) {
		}
	}
	
	/**
	 * Aquest mètode mostra la llista d'usuaris
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void userList(ActionEvent e) {
		userList.clear();
		userListMethod();
	}
	
	/**
	 * Aquest és el mètode que conté la lògica per mostrar tots els usuaris
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void userListMethod() {
		try {
			userList.clear();
			URL url = new URL("http://localhost:5002/pizzalgust/get-all-users");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"token\": \""+token+"\"}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    String jsonResponse = response.toString();
                    // Parse JSON manually
                    String usersString = jsonResponse.substring(jsonResponse.indexOf("[{"), jsonResponse.lastIndexOf("}]") + 1);
                    String[] userStrings = usersString.split("\\},\\{");

                    for (String userString : userStrings) {
                        userString = userString.replace("{", "").replace("}", "");
                        String[] fields = userString.split(",");
                        User user = new User();
                        for (String field : fields) {
                            String[] keyValue = field.split(":");
                            String key = keyValue[0].trim().replace("\"", "");
                            String value = keyValue[1].trim().replace("\"", "");
                            switch (key) {
                                case "_id":
                                    user.setId(value);
                                    break;
                                case "user_name":
                                    user.setUserName(value);
                                    break;
                                case "email":
                                    user.setEmail(value);
                                    break;
                                case "first_name":
                                    user.setFirstName(value);
                                    break;
                                case "last_name":
                                    user.setLastName(value);
                                    break;
                                case "is_admin":
                                    user.setAdmin(Boolean.parseBoolean(value));
                                    break;
                                case "user_type":
                                    user.setUserType(value);
                                    break;
                            }
                        }
                        userList.add(user);
                    }
                }
                userListScreen.setVisible(true);
                sessionProfile.setVisible(false);
                
            } else {
                System.out.println("Error fetching users. Response code: " + responseCode);
            }
            responseCode = connection.getResponseCode();
            connection.disconnect();
            
            userListView.getItems().clear();
            for (User user : userList) {
                userListView.getItems().addAll("ID: "+user.getId()+" | Username:"+user.getUserName());
            }
            userListView.getSelectionModel().select(userList.size() - 1);
		} catch(Exception errorUserList) {
			
		}
	}
	
	/**
	 * Aquesta mètode s'utilitza per tornar al llistat d'usuaris o al perfil d'usuari, depenent de la pantalla actual
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void handleExitUserList() {
		if (exitUserList.getText().equalsIgnoreCase(("Escollir un altre usuari"))) {
			exitUserList.setText("Tornar enrere");
			deleteUserButton.setVisible(false);
			userListView.setDisable(false);
			userListView.getItems().clear();
			for (User user : userList) {
				userListView.getItems().addAll("ID: "+user.getId()+" | Username: "+user.getUserName());
            }
			userListView.getSelectionModel().select(userList.size() - 1);
		} else {
			
			userListScreen.setVisible(false);
	        sessionProfile.setVisible(true);
	        
		}
		
	}
	
	/**
	 * Aquest mètode controla la lògica per actualitzar la informació de l'usuari
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void handleUpdate(ActionEvent e) {
		if (modifyButton.getText().equalsIgnoreCase("Modificar")) {
			modifyButton.setText("Aplicar canvis");
			nameField.setEditable(true);
			lastnameField.setEditable(true);
		} else {
			modifyButton.setText("Modificar");
			try {
				URL url = new URL("http://localhost:5002/pizzalgust/update-user");

	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	            connection.setRequestMethod("PUT");

	            connection.setRequestProperty("Content-Type", "application/json");

	            connection.setDoOutput(true);

	            String jsonInputString = "{\"token\": \""+token+"\", \"first_name\": \""+nameField.getText()+"\", \"last_name\": \""+lastnameField.getText()+"\"}";
	            
	            try (OutputStream os = connection.getOutputStream()) {
	                byte[] input = jsonInputString.getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }
	            
	            nameField.setEditable(false);
				lastnameField.setEditable(false);
				
				responseCode = connection.getResponseCode();
				connection.disconnect();
			} catch (Exception errorHandleUpdate) {
				
			}
		}
		
	}
	
	/**
	 * Aquest mètode controla quan un usuari s'ha clicat per mostrar la seva informació
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void userClicked() {
		selectedUser = userList.get(userListView.getSelectionModel().getSelectedIndex());
		userListView.getItems().clear();
		userListView.setDisable(true);
		deleteUserButton.setVisible(true);
		exitUserList.setText("Escollir un altre usuari");
		userListView.getItems().addAll("ID: "+selectedUser.getId());
		userListView.getItems().addAll("Nom d'usuari: "+selectedUser.getUserName());
		userListView.getItems().addAll("Email: "+selectedUser.getEmail());
		userListView.getItems().addAll("Nom: "+selectedUser.getFirstName());
		userListView.getItems().addAll("Cognom: "+selectedUser.getLastName());
		userListView.getItems().addAll("Es administrador?: "+selectedUser.isAdmin());
		userListView.getItems().addAll("Tipus d'usuari: "+selectedUser.getUserType());
	}
	
	/**
	 * Aquest mètode conté la lògica per esborrar usuaris seleccionats a la llista
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void deleteUser(ActionEvent e) {
		
		try {
			
			URL url = new URL("http://localhost:5002/pizzalgust/delete-user");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("DELETE");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"token\": \""+token+"\", \"user_id\": \""+userListView.getItems().get(0).toString().substring(4)+"\"}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            userListScreen.setVisible(false);
            userListView.setDisable(false);
            sessionProfile.setVisible(true);
    		deleteUserButton.setVisible(false);
    		exitUserList.setText("Tornar enrere");
		
    		responseCode = connection.getResponseCode();
    		connection.disconnect();
		} catch(Exception errorDeleteUser) {
			
		}
	}
	
	/**
	 * Aquest mètode conté la lògica per crear un usuari
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void handleCreateUser() {
		try {
			URL url = new URL("http://localhost:5002/pizzalgust/create-user");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            String jsonInputString = "{\"user_name\": \""+usernameNew.getText()+"\", \"email\": \""+emailNew.getText()+"\", \"first_name\": \""+nameNew.getText()+"\", \"last_name\": \""+lastNameNew.getText()+"\", \"password\": \""+passwordNew.getText()+"\"}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            if(connection.getResponseCode() == 200) {
            	errorLabel.setText("Usuari creat correctament");
            	registerUserScreen.setVisible(false);
            	loginScreen.setVisible(true);
            	usernameNew.clear();
            	emailNew.clear();
            	nameNew.clear();
            	lastNameNew.clear();
            	passwordNew.clear();
            }
            responseCode = connection.getResponseCode();
            connection.disconnect();
		} catch (Exception errorHandleCreateUser) {
			
		}
	}
	
	/**
	 * Aquest mètode fa que el botó de Registre d'usuari executi aquella pantalla
	 * @author Raul Luque Craciun
	 * @param primaryStage L'escena que s'executarà
	 */
	public void toRegisterScreen() {
		loginScreen.setVisible(false);
		registerUserScreen.setVisible(true);
	}
}
