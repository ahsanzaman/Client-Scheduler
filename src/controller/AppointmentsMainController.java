package controller;

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

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import dao.AppointmentDao;
import model.Appointment;
import utilities.AlertMessage;

/**
 * Controller class for AppointmentsMain page UI.
 */

public class AppointmentsMainController implements Initializable {
    @FXML
    public TableColumn appointmentIDCol;
    @FXML
    public TableColumn titleCol;
    @FXML
    public TableColumn descriptionCol;
    @FXML
    public TableColumn locationCol;
    @FXML
    public TableColumn typeCol;
    @FXML
    public TableColumn startCol;
    @FXML
    public TableColumn endCol;
    @FXML
    public TableColumn customerIDCol;
    @FXML
    public TableColumn userIDCol;
    @FXML
    public TableColumn contactNameCol;
    @FXML
    public TableView appointmentsTable;
    @FXML
    public RadioButton currentWeekRadio;
    @FXML
    public RadioButton currentMonthRadio;
    @FXML
    public RadioButton allAppointmentsRadio;
    public static Appointment selectedAppointment;


    /**
     * Initializes AppointmentsMain page by retrieving appointments and populating into table.
     * This also checks for any upcoming appointments and posts an appropriate alert for it.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Initializing appointments main page.");

            // opening jdbc connection and getting all appointments
            JDBC.openConnection();
            ObservableList<Appointment> appointments = AppointmentDao.getAllAppointments();

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

            // checking for upcoming appointments in 15 minutes
            ObservableList<Appointment> upcomingAppointments = AppointmentDao.checkAppointmentsInRange(LocalDateTime.now().atZone(ZoneId.systemDefault()), LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()));

            if(upcomingAppointments.size()!=0) {
                String upcomingAppointmentsStr = "";

                for(Appointment appointment: upcomingAppointments) {
                    upcomingAppointmentsStr += "Appointment ID #" + appointment.getAppointmentId() + " at " + appointment.getStartStr() + "\n";
                }
                System.out.println("Printing upcoming appointments");
                Alert alert = new AlertMessage(Alert.AlertType.INFORMATION, AlertMessage.processMessage("There are upcoming appointments!\n"+upcomingAppointmentsStr));
                alert.show();
            } else {
                System.out.println("No upcoming appointments");
                Alert alert = new AlertMessage(Alert.AlertType.INFORMATION, AlertMessage.processMessage("No Appointments"));
                alert.show();
            }
            // closing db connection
            JDBC.closeConnection();

        }
        catch(Exception e) {
            System.out.println("AppointmentsMainController failed to initialize: "+e.getMessage());
        }
    }

    /**
     * Switches view to login UI
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
     * Switches view to Add Appointment
     * @param actionEvent
     */
    public void addNewAppointment(ActionEvent actionEvent) {
        System.out.println("Add appointment button clicked. Changing view to add appointment.");
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
            Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

            Scene scene = new Scene(parent);
            stage.setTitle("Add New Appointment");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            System.out.println("addNewAppointment Exception caught: "+e.getMessage());
        }
    }

    /**
     * Switches view to mainmenu.
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
     * Saves selected appointment and submits delete request through dao layer.
     * @param actionEvent
     */
    public void deleteAppointment(ActionEvent actionEvent) {
        System.out.println("Deleting appointment.");
        try {
            selectedAppointment = (Appointment)appointmentsTable.getSelectionModel().getSelectedItem();
             if(selectedAppointment!=null) {
                 Alert alert = new Alert(Alert.AlertType.WARNING, "You are about to delete this appointment. Continue?", ButtonType.OK, ButtonType.CANCEL);
                 Optional<ButtonType> result = alert.showAndWait();

                 if(result.get() == ButtonType.OK) {

                     // opening connection to database
                     JDBC.openConnection();

                     AppointmentDao.deleteAppointment(selectedAppointment.getAppointmentId());

                     ObservableList<Appointment> appointments = AppointmentDao.getAllAppointments();

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

                     // closing connection to db
                     JDBC.closeConnection();
                 } else {
                     System.out.println("Delete cancelled from confirmation.");
                 }
             }
             else {
                 System.out.println("No options chosen.");
                 Alert alert = new AlertMessage(Alert.AlertType.ERROR, AlertMessage.processMessage("No options"));
                 alert.show();
             }

        } catch(Exception e) {
            System.out.println("deleteAppointment exception thrown: "+e.getMessage());
        }
    }

    /**
     * Saves selected appointment and then changes view to updateappointment view.
     * @param actionEvent
     */
    public void updateAppointment(ActionEvent actionEvent) {
        System.out.println("Updating appointment.");
        try {
            selectedAppointment = (Appointment)appointmentsTable.getSelectionModel().getSelectedItem();

            if(selectedAppointment!=null) {
                // change view to updateAppointment view
                Parent parent = FXMLLoader.load(getClass().getResource("/view/UpdateAppointment.fxml"));
                Stage stage = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());

                Scene scene = new Scene(parent);
                stage.setTitle("Update Appointment");
                stage.setScene(scene);
                stage.show();

            } else {
                System.out.println("No options chosen!");
                Alert alert = new AlertMessage(Alert.AlertType.ERROR, AlertMessage.processMessage("Incorrect time"));
                alert.show();
            }
        } catch(Exception e) {
            System.out.println("updateAppointment exception thrown: "+e.getMessage());
        }
    }

    /**
     * This method is invoked when a different option is chosen with the radio buttons on top for week, month, all.
     * It updates the table information according to the option selected.
     * @param actionEvent
     */
    public void updateView(ActionEvent actionEvent) {
        try {
            // opening jdbc connection and getting all appointments
            JDBC.openConnection();

            if(currentMonthRadio.isSelected()) {
                ObservableList<Appointment> appointments = AppointmentDao.checkAppointmentsInRange(LocalDateTime.now().atZone(ZoneId.systemDefault()), LocalDateTime.now().plusMonths(1).atZone(ZoneId.systemDefault()));

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
            } else if(currentWeekRadio.isSelected()) {
                ObservableList<Appointment> appointments = AppointmentDao.checkAppointmentsInRange(LocalDateTime.now().atZone(ZoneId.systemDefault()), LocalDateTime.now().plusWeeks(1).atZone(ZoneId.systemDefault()));

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

            } else {

                ObservableList<Appointment> appointments = AppointmentDao.getAllAppointments();

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
            }

            // closing db connection
            JDBC.closeConnection();
        } catch(Exception e) {
            System.out.println("Exception thrown in updateView: "+e.getMessage());
        }
    }
}
