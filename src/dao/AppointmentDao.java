package dao;

import controller.LoginController;
import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Contact;
import model.ReportByDivision;
import model.ReportByType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;

/**
 * This class is part of the data access layer to retrieve data from appointments table.
 */

public class AppointmentDao {

    /**
     * Simple select statement query to retrieve all appointment information from the table.
     * @return ObservableList of Appointment objects
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        String query = "Select * from appointments;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        while(rs.next()) {
            long appointmentId = rs.getLong("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");

            // ZonedDateTime is used to convert and save DateTime with Timezone.
            ZonedDateTime start = rs.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime end = rs.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
            System.out.println("Zoned start time received: "+start);
            long customerId = rs.getLong("Customer_ID");
            String customerName = CustomerDao.getCustomerById(customerId).getCustomerName();
            long userId = rs.getLong("User_ID");
            String userName = UserDao.getUserById(userId).getUserName();
            long contactId = rs.getLong("Contact_ID");
            // retrieving contactName from the Contact Data Access Layer
            String contactName = ContactDao.getContactById(contactId).getContactName();

            // initializing Appointment information
            Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
            appointment.setCustomerName(customerName);
            appointment.setUserName(userName);
            appointment.setContactName(contactName);
            appointments.add(appointment);
        }
        return appointments;
    }

    /**
     * Using information provided in argument to add new appointment. All checks need to be done in controller or logic layer prior to this.
     * @param newAppointment Appointment object with new appointment data stored.
     * @throws SQLException
     */
    public static void addAppointment(Appointment newAppointment) throws SQLException {
        // insert statement to push appointment info
        String query = "INSERT INTO appointments(title, description, location, type, start, end, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setString(1, newAppointment.getTitle());
        ps.setString(2, newAppointment.getDescription());
        ps.setString(3, newAppointment.getLocation());
        ps.setString(4, newAppointment.getType());
        System.out.println("DB receiving start: "+newAppointment.getStart().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() );
        System.out.println("DB receiving end: "+newAppointment.getEnd().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() );
        ps.setTimestamp(5, Timestamp.valueOf(newAppointment.getStart().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(6, Timestamp.valueOf(newAppointment.getEnd().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        ps.setString(8, LoginController.userLoggedIn.getUserName());
        ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        ps.setString(10, LoginController.userLoggedIn.getUserName());
        ps.setLong(11, newAppointment.getCustomerId());
        ps.setLong(12, newAppointment.getUserId());
        ps.setLong(13, newAppointment.getContactId());

        // executing insertion, no return expected
        ps.execute();
    }

    /**
     * Checks appointment availability between a start and end date. If the appointmentId is passed, then the availability
     * check is for updateAppointment; so it assumes that the start and end datetime will have atleast one match.
     * If appointmentId is set to -1, then appointment availability check is for adding a new appointment and matches will
     * need to be 0 to return true.
     * @param start
     * @param end
     * @param appointmentId
     * @return true if there are no appointment conflicts, false if there are conflicts.
     * @throws SQLException
     */
    public static boolean checkAppointmentAvailability(ZonedDateTime start, ZonedDateTime end, long appointmentId) throws SQLException {
        boolean appointmentAvailable = true;
        // case where check is for updating appointment

        // first check for if appoinment falls on an existing appointment
        String query = "Select count(*) from appointments where Start BETWEEN ? AND ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setTimestamp(1, Timestamp.valueOf(start.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(2, Timestamp.valueOf(end.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ResultSet rs = ps.executeQuery();
        rs.next();
        int result = rs.getInt("count(*)");
        if(result>0)
            appointmentAvailable = false;

        // second check for if appointment time falls on an existing appointment
        query = "Select count(*) from appointments where End BETWEEN ? AND ?";
        ps = JDBC.connection.prepareStatement(query);
        ps.setTimestamp(1, Timestamp.valueOf(start.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(2, Timestamp.valueOf(end.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        rs = ps.executeQuery();
        rs.next();
        result = rs.getInt("count(*)");
        // in the event that a new appointment is being added
        if(result>0)
            appointmentAvailable = false;
        // in the event that we are updating an appointment
        if(result==1 && appointmentId!=-1)
            appointmentAvailable = true;

        return appointmentAvailable;
    }

    /**
     * Deletes appointment based on the id provided.
     * @param appointmentId long variable for the appointmentId to match against.
     * @throws SQLException
     */
    public static void deleteAppointment(long appointmentId) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setLong(1, appointmentId);
        ps.execute();
    }

    /**
     * Updates appointment using the object provided.
     * @param appointment Appointment object with data stored.
     * @throws SQLException
     */
    public static void updateAppointment(Appointment appointment) throws SQLException {
        String query = "Update appointments set Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?, Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? where Appointment_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setString(1, appointment.getTitle());
        ps.setString(2, appointment.getDescription());
        ps.setString(3, appointment.getLocation());
        ps.setString(4, appointment.getType());
        ps.setTimestamp(5, Timestamp.valueOf(appointment.getStart().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(6, Timestamp.valueOf(appointment.getEnd().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        ps.setString(8, LoginController.userLoggedIn.getUserName());
        ps.setLong(9, appointment.getCustomerId());
        ps.setLong(10, appointment.getUserId());
        ps.setLong(11, appointment.getContactId());
        ps.setLong(12, appointment.getAppointmentId());
        ps.execute();
    }

    /**
     * Checks for appointments in range of start and end time then returns the list of appointments.
     * This is to check and display any upcoming appointments within 15 minutes, week and month.
     * @param startTime
     * @param endTime
     * @return ObservableList of Appointment objects
     * @throws SQLException
     */
    public static ObservableList<Appointment> checkAppointmentsInRange(ZonedDateTime startTime, ZonedDateTime endTime) throws SQLException {

        String query = "Select * from appointments where Start Between ? and ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setTimestamp(1, Timestamp.valueOf(startTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
        ps.setTimestamp(2, Timestamp.valueOf(endTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));

        ResultSet rs = ps.executeQuery();

        ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
        while(rs.next()) {
            long appointmentId = rs.getLong("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");

            // Setting zone to UTC then converting to systemdefault timezone
            ZonedDateTime start = rs.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime end = rs.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

            long customerId = rs.getLong("Customer_ID");
            String customerName = CustomerDao.getCustomerById(customerId).getCustomerName();
            long userId = rs.getLong("User_ID");
            String userName = UserDao.getUserById(userId).getUserName();
            long contactId = rs.getLong("Contact_ID");
            String contactName = ContactDao.getContactById(contactId).getContactName();

            Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
            appointment.setCustomerName(customerName);
            appointment.setUserName(userName);
            appointment.setContactName(contactName);
            upcomingAppointments.add(appointment);
        }

        return upcomingAppointments;
    }

    /**
     * Retrieve appointments using the contact information passed as argument.
     * @param contact
     * @return ObservableList of Appointment objects
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAppointmentsByContact(Contact contact) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        String query = "Select * from appointments where Contact_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setLong(1, contact.getContactId());

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            long appointmentId = rs.getLong("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");


            ZonedDateTime start = rs.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime end = rs.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

            long customerId = rs.getLong("Customer_ID");
            String customerName = CustomerDao.getCustomerById(customerId).getCustomerName();
            long userId = rs.getLong("User_ID");
            String userName = UserDao.getUserById(userId).getUserName();
            long contactId = rs.getLong("Contact_ID");
            String contactName = ContactDao.getContactById(contactId).getContactName();

            Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
            appointment.setCustomerName(customerName);
            appointment.setUserName(userName);
            appointment.setContactName(contactName);
            appointments.add(appointment);
        }
        return appointments;
    }

    /**
     * Queries db to create a report by type and date to show number of appointments.
     * @return ObservableList of ReportByType objects
     * @throws SQLException
     */
    public static ObservableList<ReportByType> getReportByType() throws SQLException {
        ObservableList<ReportByType> reports = FXCollections.observableArrayList();

        String query = "Select Type, Month(Start), count(*) from appointments group by Type, Month(Start);";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int monthNum = rs.getInt("Month(Start)");

            // lambda function # 2
            // gets corresponding month to the number passed as parameter
            GetMonthInterface getMonthObj = (int number) -> {
                if(number==1)
                    return "January";
                else if(number==2)
                    return "February";
                else if(number==3)
                    return "March";
                else if(number==4)
                    return "April";
                else if(number==5)
                    return "May";
                else if(number==6)
                    return "June";
                else if(number==7)
                    return "July";
                else if(number==8)
                    return "August";
                else if(number==9)
                    return "September";
                else if(number==10)
                    return "October";
                else if(number==11)
                    return "November";
                else return "December";
            };
            // using the lambda function to get the String month
            String month = getMonthObj.getMonth(monthNum);

            String type = rs.getString("Type");
            long total = rs.getLong("count(*)");

            ReportByType report = new ReportByType(month, type, total);
            reports.add(report);
        }

        return reports;
    }

    /**
     * This interface is used for lambda function implementation to get month from the number provided by the SQL query.
     */
    interface GetMonthInterface {
        String getMonth(int number);
    }
}
