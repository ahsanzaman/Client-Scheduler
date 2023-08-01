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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import utilities.AlertMessage;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller class for the add appointment UI.
 */

public class AddAppointmentController implements Initializable {

    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField locationTextField;
    public TextField typeTextField;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox startTimeComboBox;
    public ComboBox endTimeComboBox;
    public ComboBox customerIdComboBox;
    public ComboBox contactNameComboBox;
    public ObservableList<Contact> allContacts;

    /**
     * Fills combo boxes in UI with appropriate data retrieved from the db and also populates the time option with list
     * of times 15 minutes apart in the "hh:mm a" format.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // connect to db
            JDBC.openConnection();

            // populate comboboxes
            // get contact names as string list
            allContacts = ContactDao.getAllContacts();
            ObservableList<String> contactNames = FXCollections.observableArrayList();
            for(Contact contact: allContacts) {
                contactNames.add(contact.getContactName());
            }
            // set contact names into combobox
            contactNameComboBox.setItems(contactNames);

            // get customerId list
            ObservableList<Customer> allCustomers = CustomerDao.getAllCustomers();
            ObservableList<Long> allCustomerId = FXCollections.observableArrayList();
            for(Customer customer: allCustomers) {
                allCustomerId.add(customer.getCustomerId());
            }
            customerIdComboBox.setItems(allCustomerId);

            // setting start and end times
            LocalTime time = LocalTime.MIN;
            LocalTime endTime = LocalTime.MAX.minusMinutes(15);
            ObservableList<String> times = FXCollections.observableArrayList();
            while(time.isBefore(endTime)) {
                times.add(time.format(DateTimeFormatter.ofPattern("hh:mm a")));
                time = time.plusMinutes(15);
            }
            startTimeComboBox.setItems(times);
            endTimeComboBox.setItems(times);

            // disconnect from db
            JDBC.closeConnection();



        } catch(Exception e) {
            System.out.println("Exception caught while initializing addAppointment view: "+e.getMessage());
        }
    }

    /**
     * This is invoked when the submit button is clicked in the UI. This method pulls the data from the UI, checks for appointment availability and
     * basic logic before submitting the data through the DAO layer.
     * @param actionEvent
     */
    public void addAppointment(ActionEvent actionEvent) {
        // retrieve data from fields and submit through AppointmentDao to db
        System.out.println("Submit clicked.");
        try {
            // retrieve data from UI
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
            String type = typeTextField.getText();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime startTime = LocalTime.parse((String)startTimeComboBox.getSelectionModel().getSelectedItem(), format);
            LocalTime endTime = LocalTime.parse((String)endTimeComboBox.getSelectionModel().getSelectedItem(), format);
            long customerId = (long) customerIdComboBox.getSelectionModel().getSelectedItem();
            long userId = LoginController.userLoggedIn.getUserId();
            String contactName = (String) contactNameComboBox.getSelectionModel().getSelectedItem();

            long contactId=-1;
            for(Contact contact:allContacts) {
                if(contact.getContactName().equalsIgnoreCase(contactName))
                    contactId=contact.getContactId();
            }

            // logic checks
            // appointment times must be between 8 am to 10 pm
            // appointment times must not coincide with other appointments
            // fields should not be empty
            String errorMessage = "";
            boolean logicChecks = true;

            // converting to localdatetime for adding to appointment
            LocalDateTime start = startDate.atTime(startTime);
            LocalDateTime end = endDate.atTime(endTime);

            ZonedDateTime startEST = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endEST = end.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime startTest1 = LocalTime.of(8, 0, 0).atDate(startDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime startTest2 = LocalTime.of(22, 0, 0).atDate(startDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endTest1 = LocalTime.of(8, 0, 0).atDate(endDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endTest2 = LocalTime.of(22, 0, 0).atDate(endDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));

            System.out.println("start: "+startEST);
            System.out.println("startTest1: "+startTest1);
            System.out.println("startTest2: "+startTest2);
            System.out.println("end: "+endEST);
            System.out.println("endTest1: "+endTest1);
            System.out.println("endTest2: "+endTest2);

            // logic check to make sure appointment times are within working business hours in EST
            if(startEST.isBefore(startTest1) || startEST.isAfter(startTest2))
                logicChecks = false;

            if(endEST.isBefore(endTest1) || endEST.isAfter(endTest2))
                logicChecks = false;

            // when logic checks have failed for business hours
            if(!logicChecks)
                errorMessage = "Incorrect time";

            // ending appointment time cannot be before the start time, so this would fail logic check.
            if(end.isBefore(start)) {
                errorMessage = "Before start";
                logicChecks = false;
            }

            // check if appointment time coincides with start or end date/time
            // connect to db
            JDBC.openConnection();
            if(!AppointmentDao.checkAppointmentAvailability(start.atZone(ZoneOffset.UTC), end.atZone(ZoneOffset.UTC), -1)) {
                errorMessage = "Overlap";
                logicChecks = false;
            }

            if(title==null || description==null || location==null || type==null) {
                errorMessage = "No options";
                logicChecks = false;
            }

            // when all logic checks have passed, pass the appointment data into the dao layer.
            if(logicChecks) {
                // add appointment
                Appointment appointment = new Appointment(-1, title, description, location, type, start.atZone(ZoneId.systemDefault()), end.atZone(ZoneId.systemDefault()), customerId, userId, contactId);
                AppointmentDao.addAppointment(appointment);

                // switch view to main appointments
                Parent parent = FXMLLoader.load(getClass().getResource("/view/AppointmentsMain.fxml"));
                Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

                Scene scene = new Scene(parent);
                stage.setTitle("Appointments");
                stage.setScene(scene);
                stage.show();
            } else {
                // display the message from the logic checks above.
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, AlertMessage.processMessage(errorMessage));
                alert.show();
            }

            // close connection
            JDBC.closeConnection();
        } catch(Exception e) {
            System.out.println("addAppointment exception caught: "+e.getMessage());
        }
    }

    /**
     * Change back to AppointmentsMain FXML UI
     * @param actionEvent
     */
    public void cancelClicked(ActionEvent actionEvent) {
        System.out.println("Cancel clicked. Exiting Add appointments scene.");
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
}
