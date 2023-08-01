package controller;

import dao.CountryDao;
import dao.CustomerDao;
import dao.DivisionDao;
import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;
import utilities.AlertMessage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is the controller class for the Add Customer UI.
 */
public class AddCustomerController implements Initializable {
    @FXML
    public TextField customerNameField;
    @FXML
    public TextField addressField;
    @FXML
    public TextField postalCodeField;
    @FXML
    public TextField phoneField;
    @FXML
    public Button saveBtn;
    @FXML
    public ComboBox stateComboBox;
    @FXML
    public ComboBox countryComboBox;

    /**
     * Initialize the Add Customer UI by populating state and country comboboxes.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            // open connection
            JDBC.openConnection();

            // get data from database
            ObservableList<Country> countries = CountryDao.getAllCountries();
            ObservableList<Division> divisions = DivisionDao.getAllDivisions();

            // get countries names of all countries in a string list
            ObservableList<String> countryNames = FXCollections.observableArrayList();

            // lambda expression #1
            countries.forEach(country -> countryNames.add(country.getCountry()));

            // get all division names in a string list
            ObservableList<String> divisionNames = FXCollections.observableArrayList();
            // lamda expression #1 / same type
            divisions.forEach(division -> divisionNames.add(division.getDivision()));

            // populating combo boxes with retrieved data
            countryComboBox.setItems(countryNames);
            stateComboBox.setItems(divisionNames);

            // close connection
            JDBC.closeConnection();

        } catch(Exception e) {
            System.out.println("Exception thrown while initializing addCustomer view: "+e.getMessage());
        }

    }

    /**
     * Changes view to CustomersMain page.
     * @param actionEvent
     */
    public void openCustomersMain(ActionEvent actionEvent) {
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
            System.out.println("openCustomersMain Exception caught: "+e.getMessage());
        }
    }

    /**
     * This is when the submit button has been clicked. Performs basic logic check and then submits data through dao.
     * @param actionEvent
     */
    public void addCustomer(ActionEvent actionEvent) {
        System.out.println("Adding customer");
        try {

            // retrieving customer info from UI
            String customerName = customerNameField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String postalCode = postalCodeField.getText();
            String stateName = stateComboBox.getSelectionModel().getSelectedItem().toString();

            // checking for blank values before submission
            if(customerName==null || phone==null || address==null || postalCode==null || stateName==null) {
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, "No options");
                alert.show();
            } else {
                // connecting to db
                JDBC.openConnection();

                // find divisionId from stateName
                long divisionId = DivisionDao.getDivisionIdByName(stateName);

                // creating and passing new customer object to db
                Customer newCustomer = new Customer(-1, customerName, address, postalCode, phone, divisionId, stateName);
                CustomerDao.addCustomer(newCustomer);

                // closing connection
                JDBC.closeConnection();

                // moving scene back to customers main screen
                Parent parent = FXMLLoader.load(getClass().getResource("/view/CustomersMain.fxml"));
                Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

                Scene scene = new Scene(parent);
                stage.setTitle("Customers");
                stage.setScene(scene);
                stage.show();
            }
        } catch(Exception e) {
            System.out.println("addCustomer Exception caught: "+e.getMessage());
        }
    }

    /**
     * When a different country is selected in UI, change the divisions according to the country chosen.
     * @param actionEvent
     */
    public void updateDivisions(ActionEvent actionEvent) {
        System.out.println("Updating divisions dropdown.");
        try {
            // opening connection to db
            JDBC.openConnection();

            // finding out what country was selected in UI
            String countryNameSelected = countryComboBox.getSelectionModel().getSelectedItem().toString();
            System.out.println("Country selected: "+countryNameSelected);

            // query db for country that has been selected to retrieve countryId
            Country countrySelected = CountryDao.getCountryByName(countryNameSelected);

            // querying db for divisions corresponding with countryId
            ObservableList<String> divisions = DivisionDao.getDivisionsForCountry(countrySelected.getCountryId());

            // setting state dropdown to list corresponding with country chosen
            stateComboBox.setItems(divisions);

            // closing connection to db
            JDBC.closeConnection();
        } catch(Exception e) {
            System.out.println("updateDivisions Exception caught: "+e.getMessage());
        }
    }
}
