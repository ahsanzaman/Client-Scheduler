package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is the controller class for the MainMenu UI.
 */

public class MainMenuController implements Initializable {
    @FXML
    public Button mainMenuLogoutBtn;
    @FXML
    public Button mainMenuAppointmentsBtn;

    /**
     * Nothing requires to be initialized in the controller layer as it is all statically defined in the FXML UI.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Logs user out and switches view to Login page.
     * @param actionEvent
     */
    public void logoutUser(ActionEvent actionEvent) {
        System.out.println("User logging out.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Client Scheduler Login");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("logoutUser Exception caught: "+e.getMessage());
        }
    }

    /**
     * Switches view to Appointments main page.
     * @param actionEvent
     */
    public void openAppointments(ActionEvent actionEvent) {
        System.out.println("Opening appointments.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/AppointmentsMain.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Appointments");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("openAppointments Exception caught: "+e.getMessage());
        }
    }

    /**
     * Switches view to customers main page.
     * @param actionEvent
     */
    public void openCustomers(ActionEvent actionEvent) {
        System.out.println("Opening customers.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/CustomersMain.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Customers");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("openCustomers Exception caught: "+e.getMessage());
        }
    }

    /**
     * Switches view to reports main page.
     * @param actionEvent
     */
    public void openReports(ActionEvent actionEvent) {
        System.out.println("Opening reports.");
        try {
            // loading view for reports
            Parent parent = FXMLLoader.load(getClass().getResource("/view/Reports.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Contacts");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("openReports Exception caught: "+e.getMessage());
        }
    }
}
