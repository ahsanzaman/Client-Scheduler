package controller;

import dao.ContactDao;
import dao.CustomerDao;
import dao.helper.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Contact;
import model.Customer;
import utilities.AlertMessage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for Customers Main FXML UI page.
 */
public class CustomersMainController implements Initializable {

    @FXML
    public TableView customersTable;
    @FXML
    public TableColumn customerIDCol;
    @FXML
    public TableColumn customerNameCol;
    @FXML
    public TableColumn addressCol;
    @FXML
    public TableColumn postalCodeCol;
    @FXML
    public TableColumn phoneCol;
    @FXML
    public TableColumn divisionNameCol;
    @FXML
    public Button addCustomer;
    public static Customer selectedCustomer;

    /**
     * Initializes Customers Main page UI by populating customers data into table.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // open connection to db
            JDBC.openConnection();

            // get all customers using the DAO layer.
            ObservableList<Customer> customers = CustomerDao.getAllCustomers();

            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            divisionNameCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            customersTable.setItems(customers);

            // close connection to db
            JDBC.closeConnection();

        } catch(Exception e) {
            System.out.println("Exception caught initializing Customers view: "+e.getMessage());
        }
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
     * Switches view to MainMenu page.
     * @param actionEvent
     */
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

    /**
     * Switches view to add customer UI.
     * @param actionEvent
     */
    public void addNewCustomer(ActionEvent actionEvent) {
        System.out.println("Opening Add Customer");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Add New Customer");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("addNewCustomer Exception caught: "+e.getMessage());
        }
    }

    /**
     * Saves the selected customer and switches view to updatecustomer UI.
     * @param actionEvent
     */
    public void updateCustomer(ActionEvent actionEvent) {
        System.out.println("Updating customer");
        try {
            // setting selected customer to update for the UpdateCustomer view
            selectedCustomer = (Customer)customersTable.getSelectionModel().getSelectedItem();
            System.out.println("Selected customer: "+selectedCustomer.getCustomerName());
            System.out.println("Division ID: "+selectedCustomer.getDivisionId());

            // switching to update customer view
            Parent parent = FXMLLoader.load(getClass().getResource("/view/UpdateCustomer.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Update Customer");
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            System.out.println("updateCustomer Exception caught: "+e.getMessage());
        }
    }

    /**
     * Saves selected customer and submits a request through DAO layer to delete customer.
     * @param actionEvent
     */
    public void deleteCustomer(ActionEvent actionEvent) {
        System.out.println("Deleting customer.");
        try {
            // getting the customer to delete
            selectedCustomer = (Customer)customersTable.getSelectionModel().getSelectedItem();
            if(selectedCustomer!=null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You are about to delete this customer and associated appointments. Continue?", ButtonType.OK, ButtonType.CANCEL);
                Optional<ButtonType> result = alert.showAndWait();

                    if(result.get() == ButtonType.OK) {
                        System.out.println("Deleting customer: " + selectedCustomer.getCustomerName());

                        // opening connection
                        JDBC.openConnection();

                        // sending delete query
                        CustomerDao.deleteCustomer(selectedCustomer.getCustomerId());

                        // updating view
                        ObservableList<Customer> customers = CustomerDao.getAllCustomers();

                        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
                        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
                        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
                        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
                        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
                        divisionNameCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

                        customersTable.setItems(customers);

                        // closing connection
                        JDBC.closeConnection();
                    } else {
                        System.out.println("Delete cancelled.");
                    }
            } else {
                System.out.println("No options chosen.");
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, AlertMessage.processMessage("No options"));
                alert.show();
            }

        } catch(Exception e) {
            System.out.println("deleteCustomer Exception thrown: "+e.getMessage());
        }
    }
}
