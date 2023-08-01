package controller;

import com.sun.scenario.effect.Offset;
import dao.UserDao;
import dao.helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import utilities.Logger;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for Login FXML UI.
 */
public class LoginController implements Initializable {
    @FXML
    public Label usernameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public Button loginBtn;
    @FXML
    public TextField usernameFieldLogin;
    @FXML
    public PasswordField passwordFieldLogin;
    @FXML
    public Label errorLabel;
    @FXML
    public Label timezoneLabel;
    public static User userLoggedIn;

    /**
     * This method will get user login credentials from the UI and match it with user login in db.
     * It also uses the Logger class to record login activity into a text file and displays an error
     * on every incorrect login.
     * @param actionEvent
     */
    public void authenticateUser(ActionEvent actionEvent) {
        String username = usernameFieldLogin.getText();
        String password = passwordFieldLogin.getText();
        ResourceBundle rb = ResourceBundle.getBundle("locale/Nat", Locale.getDefault());

        // testing auth
        if (!username.isEmpty() && !password.isEmpty()) {
            System.out.println("Authenticating.");
            try {
                JDBC.openConnection();
                User loginUser = UserDao.getUser(username, password);
                // if credentials are a match the id will be a number more than 0
                if(loginUser.getUserId()!=-1) {
                    System.out.println("Authenticated");
                    Logger.log("Success! Login by "+loginUser.getUserName()+" at "+ LocalDateTime.now(ZoneOffset.UTC));
                    // case of successful authentication
                    userLoggedIn = loginUser;

                    Parent parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                    Stage stage = (Stage)(((Node)actionEvent.getSource()).getScene().getWindow());

                    Scene scene = new Scene(parent);
                    stage.setTitle("Client Scheduler Main Menu");
                    stage.setScene(scene);
                    stage.show();
                }
                else {
                    // unsuccessful login attempt
                    // logging the attempt and displaying the error on label
                    System.out.println("Unsuccessful login.");
                    Logger.log("Failed! Login attempt: "+username+" at "+ LocalDateTime.now(ZoneOffset.UTC));
                    errorLabel.setText(rb.getString("invalid_login")+"!");
                    errorLabel.setVisible(true);
                }
                JDBC.closeConnection();
            } catch (Exception e) {
                System.out.println("Exception caught: " + e.getMessage());
            }
        }
        else {
            // case where a field is empty
            System.out.println("Unable to authenticate.");
            errorLabel.setText(rb.getString("empty_field")+"!");
            errorLabel.setVisible(true);
            System.out.println("Username entered: "+username);
            System.out.println("Password entered: "+password);

        }
    }


    /**
     * Initializes Login UI along with default locale set for the computer.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ResourceBundle rb = ResourceBundle.getBundle("locale/Nat", Locale.getDefault());

        // setting all labels with respect to default language
        usernameLabel.setText(rb.getString("username_label")+":");
        passwordLabel.setText(rb.getString("password_label")+":");
        loginBtn.setText(rb.getString("login_button"));

        timezoneLabel.setText(rb.getString("location_label")+": "+ ZoneId.systemDefault());
    }
}
