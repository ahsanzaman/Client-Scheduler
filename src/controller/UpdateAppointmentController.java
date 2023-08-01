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
 * This controller is for UpdateAppointment FXML UI.
 */

public class UpdateAppointmentController implements Initializable {
    public TextField appointmentIDTextField;
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
     * Initializes UI dropdowns with data from the db, and pulls the Appointment info from the AppointmentMain page to
     * display it to user and allow changes.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // populate regular appointments info
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

            // get data from main appointments screen
            // populate data into fields
            Appointment appointment = AppointmentsMainController.selectedAppointment;
            appointmentIDTextField.setText(Long.toString(appointment.getAppointmentId()));
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            typeTextField.setText(appointment.getType());
            startDatePicker.setValue(appointment.getStartDate());
            endDatePicker.setValue(appointment.getEndDate());
            startTimeComboBox.setValue(appointment.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")).toString());
            endTimeComboBox.setValue(appointment.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")).toString());
            customerIdComboBox.setValue(Long.toString(appointment.getCustomerId()));
            contactNameComboBox.setValue(appointment.getContactName());
        } catch(Exception e) {
            System.out.println("Exception thrown while initializing updateAppointment view: "+e.getMessage());
        }
    }

    /**
     * Changes view to AppointmentsMain page.
     * @param actionEvent
     */
    public void cancelClicked(ActionEvent actionEvent) {
        System.out.println("Opening appointments main view.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/AppointmentsMain.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Appointments");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("cancelClicked Exception caught: "+e.getMessage());
        }
    }

    /**
     * This method is for when the submit button is clicked. It performs the logical checks and then submits the update
     * request through the DAO layer.
     * @param actionEvent
     */
    public void updateAppointment(ActionEvent actionEvent) {
        System.out.println("Appointment update function started.");
        try {
            // retrieve appointment info from UI
            long appointmentId = Long.parseLong(appointmentIDTextField.getText());
            System.out.println("Appointment ID: "+appointmentId);
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
            String type = typeTextField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            // since UI has the datetime in hh:mm a format the option chosen will need to be parsed accordingly.
            DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime startTime = LocalTime.parse((String) startTimeComboBox.getSelectionModel().getSelectedItem(), format);
            LocalTime endTime = LocalTime.parse((String) endTimeComboBox.getSelectionModel().getSelectedItem(), format);
            long customerId = Long.parseLong((String) customerIdComboBox.getSelectionModel().getSelectedItem());
            long userId = LoginController.userLoggedIn.getUserId();
            String contactName = (String) contactNameComboBox.getSelectionModel().getSelectedItem();

            long contactId = -1;
            for (Contact contact : allContacts) {
                if (contact.getContactName().equalsIgnoreCase(contactName))
                    contactId = contact.getContactId();
            }


            // logic checks same as addAppointments
            // appointment times must be between 8 am to 10 pm
            // appointment times must not coincide with other appointments
            // fields should not be empty
            String errorMessage = "";
            boolean logicChecks = true;

            // converting to localdatetime for adding to appointment
            LocalDateTime start = startDate.atTime(startTime);
            LocalDateTime end = endDate.atTime(endTime);

            // converting DateTime to EST to test if time entered is within business hours for EST 8am - 10pm
            ZonedDateTime startEST = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endEst = end.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime startTest1 = LocalTime.of(8, 0, 0).atDate(startDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime startTest2 = LocalTime.of(22, 0, 0).atDate(startDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endTest1 = LocalTime.of(8, 0, 0).atDate(endDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endTest2 = LocalTime.of(22, 0, 0).atDate(endDate).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));

            if(startEST.isBefore(startTest1) || startEST.isAfter(startTest2))
                logicChecks = false;

            if(endEst.isBefore(endTest1) || endEst.isAfter(endTest2))
                logicChecks = false;

            if(!logicChecks)
                errorMessage = "Incorrect time";

            // logic check that the ending time should not be before start time
            if(end.isBefore(start)) {
                errorMessage = "Before start";
                logicChecks = false;
            }


            // checking for blank values
            if(title==null || description==null || location==null || type==null) {
                errorMessage = "No options";
                logicChecks = false;
            }

            // check if appointment time coincides with start or end date/time
            // connect to db
            JDBC.openConnection();
            if(!AppointmentDao.checkAppointmentAvailability(start.atZone(ZoneId.systemDefault()), end.atZone(ZoneId.systemDefault()), appointmentId)) {
                errorMessage = "Overlap";
                logicChecks = false;
            }


            // submit through DAO layer or print error message if checks failed
            if (logicChecks) {
                // add appointment
                Appointment appointment = new Appointment(appointmentId, title, description, location, type, start.atZone(ZoneId.systemDefault()), end.atZone(ZoneId.systemDefault()), customerId, userId, contactId);
                AppointmentDao.updateAppointment(appointment);

                // switch view to main appointments
                Parent parent = FXMLLoader.load(getClass().getResource("/view/AppointmentsMain.fxml"));
                Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

                Scene scene = new Scene(parent);
                stage.setTitle("Appointments");
                stage.setScene(scene);
                stage.show();
            } else {
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, AlertMessage.processMessage(errorMessage));
                alert.show();
            }

            // closing connection
            JDBC.closeConnection();
        } catch(Exception e) {
            System.out.println("updateAppointment exception caught: "+e.getMessage());
        }
    }
}
