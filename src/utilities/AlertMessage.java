package utilities;

import javafx.scene.control.Alert;

/**
 * Utility class to display a elongated message based on the key string passed.
 */
public class AlertMessage extends Alert {
    public AlertMessage(AlertType alertType, String message) {
        super(alertType, message);
    }

    public static String processMessage(String message) {
        String processedMessage="";

        switch(message) {
            case "Empty":
                processedMessage = "One or more fields are empty. Please enter values for them and try again.";
                break;
            case "No options":
                processedMessage = "No options are chosen. Please select an option and try again.";
                break;
            case "Incorrect time":
                processedMessage = "Appointment time must be between 8 am and 10 pm, including weekends.";
                break;
            case "Overlap":
                processedMessage = "Appointment time overlaps with another customer's appointment.";
                break;
            case "Incorrect login":
                processedMessage = "Incorrect login credentials entered. Please try again.";
                break;
            case "No Appointments":
                processedMessage = "There are no upcoming appointments.";
                break;
            case "Before start":
                processedMessage = "Start date and time cannot be after end date and time.";
                break;
            default:
                processedMessage = message;
        }
        return processedMessage;
    }
}
