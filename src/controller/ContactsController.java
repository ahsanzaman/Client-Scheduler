package controller;

import dao.ContactDao;
import dao.helper.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Contact;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for contacts view which is not implemented into the MainMenu options yet.
 */

public class ContactsController implements Initializable {

    @FXML
    public TableView contactsTable;
    @FXML
    public TableColumn contactIDCol;
    @FXML
    public TableColumn contactNameCol;
    @FXML
    public TableColumn emailCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            JDBC.openConnection();
            ObservableList<Contact> contacts = ContactDao.getAllContacts();

            contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

            contactsTable.setItems(contacts);
            JDBC.closeConnection();

        } catch(Exception e) {
            System.out.println("Exception caught initializing Contacts view: "+e.getMessage());
        }
    }

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

    public void openMainMenu(ActionEvent actionEvent) {
        System.out.println("Opening Main Menu.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Main Menu");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("openMainMenu Exception caught: "+e.getMessage());
        }
    }
}
