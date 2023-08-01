package dao;

import dao.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DivisionDao {

    /**
     * Simple select query to get list of first_level_divisions.
     * @return ObservableList of Division objects
     * @throws SQLException
     */
    public static ObservableList<Division> getAllDivisions() throws SQLException {
        ObservableList<Division> allDivisions = FXCollections.observableArrayList();

        String query = "Select * from first_level_divisions;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            long divisionId = rs.getLong("Division_ID");
            String division = rs.getString("Division");
            long countryId = rs.getLong("Country_ID");
            Division eachDivision = new Division(divisionId, division, countryId);
            allDivisions.add(eachDivision);
        }

        return allDivisions;
    }

    /**
     * Searches divisions for a particular country using the provided id.
     * @param searchCountryId a long variable to search with
     * @return ObservableList of Strings containing division names matching with the countryId
     * @throws SQLException
     */
    public static ObservableList<String> getDivisionsForCountry(long searchCountryId) throws SQLException {
        ObservableList<String> divisions = FXCollections.observableArrayList();

        String query = "Select * from first_level_divisions where Country_ID = ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setLong(1, searchCountryId);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            String division = rs.getString("Division");
            divisions.add(division);
        }

        return divisions;
    }

    /**
     * Uses a string to search for a division Id using its name.
     * @param searchDivision string to match division
     * @return long variable containing division Id of the match or -1 if no match found.
     * @throws SQLException
     */
    public static long getDivisionIdByName(String searchDivision) throws SQLException {
        long divisionId = -1;

        String query = "Select * from first_level_divisions where Division=?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setString(1, searchDivision);

        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            divisionId = rs.getLong("Division_ID");
        }
        return divisionId;
    }
}
