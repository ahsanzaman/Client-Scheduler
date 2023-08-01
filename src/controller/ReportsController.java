package controller;

import dao.AppointmentDao;
import dao.ContactDao;
import dao.CustomerDao;
import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import model.*;

/**
 * This class acts as a controller for the Reports UI. It displays a table for appointments for a contact which can be
 * selected from a dropdown, a table for report by type and by division.
 */

public class ReportsController implements Initializable {

    public TableColumn appointmentIDCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn locationCol;
    public TableColumn typeCol;
    public TableColumn startCol;
    public TableColumn endCol;
    public TableColumn customerIDCol;
    public TableColumn userIDCol;
    public TableColumn contactNameCol;
    public TableView appointmentsTable;
    public ComboBox contactComboBox;
    public ObservableList<Contact> allContacts;
    public TableView byTypeReportTable;
    public TableColumn monthCol;
    public TableColumn byTypeCol;
    public TableColumn totalCol;
    public TableView divisionReportTable;
    public TableColumn divisionNameCol;
    public TableColumn totalCustomersCol;

    /**
     * Initializes reports UI by:
     * 1. Populating contacts in dropdown
     * 2. Populating Report by Type and Month into table
     * 3. Populating Report by Division into table
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            System.out.println("Initializing reports for contact.");

            // opening jdbc connection and getting all appointments
            JDBC.openConnection();

            // getting all contacts to populate combobox
            allContacts = ContactDao.getAllContacts();
            ObservableList<String> contactNames = FXCollections.observableArrayList();

            // lamba # 2
            allContacts.forEach(contact->contactNames.add(contact.getContactName()));

            // set contact names into combobox
            contactComboBox.setItems(contactNames);

            // getting report by type and populating table in UI
            ObservableList<ReportByType> reportByTypes = AppointmentDao.getReportByType();
            monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
            byTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
            byTypeReportTable.setItems(reportByTypes);

            // getting divisions report and populating table in UI
            ObservableList<ReportByDivision> reportByDivisions = CustomerDao.getReportByDivision();
            divisionNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            totalCustomersCol.setCellValueFactory(new PropertyValueFactory<>("total"));
            divisionReportTable.setItems(reportByDivisions);

            // closing db connection
            JDBC.closeConnection();

        } catch (Exception e) {
            System.out.println("Exception caught while initializing Reports view: "+e.getMessage());
        }
    }

    /**
     * Changes view to Main menu page.
     * @param actionEvent
     */
    public void toMainMenu(ActionEvent actionEvent) {
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
     * Logs user out and changes view to the login page.
     * @param actionEvent
     */
    public void logout(ActionEvent actionEvent) {
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
     * This method is toggled when a value is changed for contact names dropdown.
     * The view is updated according to the option chosen.
     * @param actionEvent
     */
    public void changeView(ActionEvent actionEvent) {
        try {
            System.out.println("Initializing reports for contact.");

            // opening jdbc connection and getting all appointments
            JDBC.openConnection();

            ObservableList<Contact> allContacts = ContactDao.getAllContacts();
            String contactName = (String) contactComboBox.getSelectionModel().getSelectedItem();

            Contact searchedContact = new Contact(-1, "", "");
            for(Contact contact:allContacts) {
                if(contact.getContactName().equalsIgnoreCase(contactName))
                    searchedContact=contact;
            }

            ObservableList<Appointment> appointments = AppointmentDao.getAppointmentsByContact(searchedContact);

            // setting appointments values to the table
            appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            startCol.setCellValueFactory(new PropertyValueFactory<>("startStr"));
            endCol.setCellValueFactory(new PropertyValueFactory<>("endStr"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
            contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));

            appointmentsTable.setItems(appointments);

            // closing db connection
            JDBC.closeConnection();

        } catch (Exception e) {
            System.out.println("Exception caught while changing reports view: "+e.getMessage());
        }
    }
}
