package dao;

import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access class to interact with the contacts table in the db.
 */
public class ContactDao {

    /**
     * Simple select statement to retrieve all contacts in a list.
     * @return ObservableList of Contact objects
     * @throws SQLException
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException {
        String query = "Select * from contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        while(rs.next()) {
            // getting the data from the columns to populate contact object and list
            long contactId = rs.getLong("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String email = rs.getString("Email");
            Contact newContact = new Contact(contactId, contactName, email);
            contacts.add(newContact);
        }
        return contacts;
    }

    /**
     * Searches contacts table for the id passed and returns the contact information in an object.
     * @param contactId long variable to search for the contact
     * @return Contact object which is a match
     * @throws SQLException
     */
    public static Contact getContactById(long contactId) throws SQLException {
        String query = "Select * from contacts where Contact_ID=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setLong(1, contactId);

        ResultSet rs = ps.executeQuery();

        String contactName = "";
        String email = "";
        while(rs.next()) {
            contactName = rs.getString("Contact_Name");
            email = rs.getString("Email");
        }
        Contact contact = new Contact(contactId, contactName, email);
        return contact;
    }
}
