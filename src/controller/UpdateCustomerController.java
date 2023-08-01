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
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;
import utilities.AlertMessage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the UpdateCustomer FXML UI.
 */

public class UpdateCustomerController implements Initializable {
    @FXML
    public TextField customerIdField;
    @FXML
    public TextField customerNameField;
    @FXML
    public TextField addressField;
    @FXML
    public TextField postalCodeField;
    @FXML
    public TextField phoneField;
    @FXML
    public ComboBox countryComboBox;
    @FXML
    public ComboBox stateComboBox;

    /**
     * Initializes all drop downs by getting the data from the db and datafields by retrieving the Customer information
     * from main controller.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize drop downs first
            // open connection
            JDBC.openConnection();

            // get data from database
            ObservableList<Country> countries = CountryDao.getAllCountries();
            ObservableList<Division> divisions = DivisionDao.getAllDivisions();

            // get countries names of all countries in a string list
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            for(Country eachCountry: countries) {
                countryNames.add(eachCountry.getCountry());
            }

            // get all division names in a string list
            ObservableList<String> divisionNames = FXCollections.observableArrayList();
            for(Division division: divisions) {
                divisionNames.add(division.getDivision());
            }

            // populating combo boxes with retrieved data
            countryComboBox.setItems(countryNames);
            stateComboBox.setItems(divisionNames);

            // close connection
            JDBC.closeConnection();

            // get selected customer from main controller
            Customer selectedCustomer = CustomersMainController.selectedCustomer;

            // set selected customer values in fields
            customerIdField.setText(Long.toString(selectedCustomer.getCustomerId()));
            customerNameField.setText(selectedCustomer.getCustomerName());
            addressField.setText(selectedCustomer.getAddress());
            postalCodeField.setText(selectedCustomer.getPostalCode());
            phoneField.setText(selectedCustomer.getPhone());

            // get country name corresponding to division
            long divisionId = selectedCustomer.getDivisionId();
            long countryId = -1;
            for(Division division: divisions) {
                if(division.getDivisionId()==divisionId) {
                        countryId = division.getCountryId();
                        break;
                }
            }
            String countryName="";
            for(Country country:countries) {
                if(country.getCountryId()==countryId) {
                    countryName = country.getCountry();
                    break;
                }
            }

            // setting retrieved values for comboboxes
            countryComboBox.setValue(countryName);
            this.updateDivisions(new ActionEvent());
            stateComboBox.setValue(selectedCustomer.getDivisionName());

        } catch(Exception e) {
            System.out.println("Exception caught while initializing UpdateCustomer View: "+e.getMessage());
        }
    }

    /**
     * This method is for when the submit button is clicked in the UI. It only pushes the update once all the logical checks
     * have passed successfully.
     * @param actionEvent
     */
    public void updateCustomer(ActionEvent actionEvent) {
        System.out.println("Updating customer");
        try {

            // retrieving customer info from UI
            long customerId = Long.parseLong(customerIdField.getText());
            String customerName = customerNameField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String postalCode = postalCodeField.getText();
            //String countryName = countryComboBox.getSelectionModel().getSelectedItem().toString();
            String stateName = stateComboBox.getSelectionModel().getSelectedItem().toString();

            // checking for blank values
            if(customerName==null || phone==null || address==null || postalCode==null || stateName==null) {
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, "No options");
                alert.show();
            } else {
                // connecting to db
                JDBC.openConnection();

                // find divisionId from stateName
                long divisionId = DivisionDao.getDivisionIdByName(stateName);

                // creating and passing new customer object to db
                Customer newCustomer = new Customer(customerId, customerName, address, postalCode, phone, divisionId, stateName);
                CustomerDao.updateCustomer(newCustomer);

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
            System.out.println("updateCustomer Exception caught: "+e.getMessage());
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
     * Updates divisions based on the info chosen in UI.
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
