package dao;

import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Data access class to query countries table in the db.
 */
public class CountryDao {

    /**
     * Retrieves all countries with a simple select statement query.
     * @return ObservableList of Country objects
     * @throws SQLException
     */
    public static ObservableList<Country> getAllCountries() throws SQLException {
        String query = "Select * from countries;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Country> countries = FXCollections.observableArrayList();
        while(rs.next()) {
            // retrieving data from columns and creating a country object to add to list.
            long countryId = rs.getLong("Country_ID");
            String countryName = rs.getString("Country");
            Country eachCountry = new Country(countryId, countryName);
            countries.add(eachCountry);
        }
        return countries;
    }

    /**
     * Searches the countries table for a country using a string.
     * @param searchCountryName string to search the country by.
     * @return Country object match for the string entered.
     * @throws SQLException
     */
    public static Country getCountryByName(String searchCountryName) throws SQLException {
        String query = "Select * from countries where Country = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setString(1, searchCountryName);

        ResultSet rs = ps.executeQuery();

        Country searchedCountry = new Country(-1, "");
        while(rs.next()) {
            // retrieving data from columns and passing it to Country object
            long countryId = rs.getLong("Country_ID");
            String countryName = rs.getString("Country");
            searchedCountry = new Country(countryId, countryName);
        }

        return searchedCountry;
    }
}
