package Access;

import Utilities.DBConnection;
import Model.Contacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  @author Ladd Gillies Jr
 */



public class contactAccess
{
    /** Create an Observable List for all Contacts.
     * Query Database to get all Contacts.
     *
     * @return Returns all Contacts.
     * @throws SQLException
     */
    public static ObservableList<Contacts> getAllContacts() throws SQLException
    {
        ObservableList<Contacts> contactsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            Contacts contact = new Contacts(contactEmail, contactID, contactName);
            contactsObservableList.add(contact);
        }
        return contactsObservableList;
    }

    /** Method for finding Contact ID.
     *
     * @param contactID Get Contact ID
     * @return Return Contact ID
     * @throws SQLException
     */
    public static String findContactID(String contactID) throws SQLException
    {
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name =?");
        ps.setString(1, contactID);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            contactID = rs.getString("Contact_ID");
        }
        return contactID;
    }
}
