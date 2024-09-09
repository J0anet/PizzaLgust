package application;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends ApplicationTest {
    
    private Main mainApp;
    
    @Override
    public void start(Stage stage) {
        mainApp = new Main();
        mainApp.start(stage);
    }
    
    @Test
    public void testHandleLogin() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        VBox sessionProfile = find("#sessionProfile");
        assertTrue(sessionProfile.isVisible());
        Label userLabel = find("#userLabel");
        assertEquals("bwayne", userLabel.getText());
    }
    
    @Test
    public void testHandleLogout() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#logoutButton");
        VBox loginScreen = find("#loginScreen");
        assertTrue(loginScreen.isVisible());
    }
    
    @Test
    public void testUserList() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#showUsers");
        VBox userListScreen = find("#userListScreen");
        assertTrue(userListScreen.isVisible());
        ListView<?> userListView = find("#userListView");
        assertTrue(userListView.getItems().size() > 0);
    }
    
    @Test
    public void testToRegisterScreen() {
        clickOn("#toRegisterScreenButton");
        VBox registerUserScreen = find("#registerUserScreen");
        assertTrue(registerUserScreen.isVisible());
    }
    
    @Test
    public void test1HandleCreateUser() {
        clickOn("#toRegisterScreenButton");
        TextField usernameNew = find("#usernameNew");
        clickOn(usernameNew).write("newuser@example.com");
        TextField emailNew = find("#emailNew");
        clickOn(emailNew).write("newuser@example.com");
        TextField nameNew = find("#nameNew");
        clickOn(nameNew).write("New");
        TextField lastNameNew = find("#lastNameNew");
        clickOn(lastNameNew).write("User");
        TextField passwordNew = find("#passwordNew");
        clickOn(passwordNew).write("password");
        clickOn("#createUserButton");
        VBox loginScreen = find("#loginScreen");
        assertTrue(loginScreen.isVisible());
    }
    
    @Test
    public void testHandleUpdate() {
        clickOn("#usernameField").write("newuser@example.com");
        clickOn("#passwordField").write("password");
        clickOn("#loginButton");
        clickOn("#modifyButton");
        TextField nameField = find("#nameField");
        clickOn(nameField).write("New Name");
        TextField lastnameField = find("#lastnameField");
        clickOn(lastnameField).write("New Last Name");
        clickOn("#modifyButton");
        assertFalse(nameField.isEditable());
        assertFalse(lastnameField.isEditable());
    }
    
    @Test
    public void testUserClicked() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#showUsers");
        ListView<?> userListView = find("#userListView");
        clickOn(userListView);
        clickOn("#deleteUserButton");
        Button exitUserList = find("#exitUserList");
        assertEquals("Tornar enrere", exitUserList.getText());
    }
    
    @Test
    public void testDeleteUser() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#showUsers");
        ListView<?> userListView = find("#userListView");
        clickOn(userListView);
        clickOn("#deleteUserButton");
        Button exitUserList = find("#exitUserList");
        assertEquals("Tornar enrere", exitUserList.getText());
    }
    
    
    
    @Test
    public void testHandleExitUserList() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#showUsers");
        clickOn("#exitUserList");
        VBox sessionProfile = find("#sessionProfile");
        assertTrue(sessionProfile.isVisible());
    }
    
    @Test
    public void testHandleExitUserListBack() {
        clickOn("#usernameField").write("bwayne@gotham.com");
        clickOn("#passwordField").write("batman");
        clickOn("#loginButton");
        clickOn("#showUsers");
        clickOn("#exitUserList");
        clickOn("#exitUserList");
        VBox userListScreen = find("#userListScreen");
        assertFalse(userListScreen.isVisible());
    }
    
    // Helper method to find UI elements
    private <T extends javafx.scene.Node> T find(final String query) {
        return lookup(query).query();
    }
}
