package dao;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import dao.helper.JDBC;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to access data from the users table in the db.
 */
public class UserDao {

    /**
     * Retrieves a User based on the username and password passed as arguments.
     * @param username
     * @param password
     * @return User match found for the parameters.
     * @throws SQLException
     */
    public static User getUser(String username, String password) throws SQLException {
        String query = "Select * from users where User_Name = ? AND Password=?";

        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        User retrievedUser = new User(-1, "");
        while(rs.next()) {
            long userId = rs.getLong("User_ID");
            String userName = rs.getString("User_Name");
            retrievedUser = new User(userId, userName);
        }
        return retrievedUser;
    }

    /**
     * Retrieve a user with a user Id number.
     * @param userId
     * @return
     * @throws SQLException
     */
    public static User getUserById(long userId) throws SQLException {
        String query = "Select * from users where User_ID=?;";
        String userName = "";

        PreparedStatement ps = JDBC.connection.prepareStatement(query);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            userName = rs.getString("User_Name");
        }

        User user = new User(userId, userName);
        return user;
    }
}
