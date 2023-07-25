package Access;

import Utilities.DBConnection;
import Model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  @author Ladd Gillies Jr
 */



public class countryAccess extends Country
{
    public countryAccess(int countryID , String countryName)
    {
        super(countryID, countryName);
    }

    /** Create Observable List for all Countries.
     * Query Database to get Country ID and Country Name.
     *
     * @return Returns all Countries
     * @throws SQLException
     */
    public static ObservableList<countryAccess> getCountries() throws SQLException
    {
        ObservableList<countryAccess> countriesObservableList = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country FROM countries";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            countryAccess country = new countryAccess(countryID, countryName);
            countriesObservableList.add(country);
        }
        return countriesObservableList;
    }
}
