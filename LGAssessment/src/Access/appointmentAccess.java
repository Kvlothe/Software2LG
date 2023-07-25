package Access;

import Utilities.DBConnection;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 *  @author Ladd Gillies Jr
 */


public class appointmentAccess
{
    /** Create an Observable List for all appointments and querying the Database.
     *
     * @return Returns the Observable List to populate tables.
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException
    {
        ObservableList<Appointment> appointmentsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            Integer appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");
            Appointment appointment = new Appointment(appointmentID, title, description, location, type, start, end, customerID, userID, contactID);
            appointmentsObservableList.add(appointment);
        }
        return appointmentsObservableList;
    }


    /** Method for deleting an Appointment
     *
     * @param customer Get Customer to delete
     * @param connection Get connection
     * @return Return the result of deleted appointment
     * @throws SQLException
     */
    public static int deleteAppointment(int customer, Connection connection) throws SQLException
    {
        String query = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, customer);
        int result = ps.executeUpdate();
        ps.close();
        return result;
    }
}