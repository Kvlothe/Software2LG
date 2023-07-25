package Access;

import Utilities.DBConnection;
import Model.firstLevelDivisions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Class that extends firstleveldivisions
 *
 * @author Ladd Gillies Jr
 */
public class firstLevelDivisionAccess extends firstLevelDivisions
{
    public firstLevelDivisionAccess(int divisionID, String divisionName, int country_ID)
    {
        super(divisionID, divisionName, country_ID);
    }

    /** Method for getting all 1st Level Divisions
     * Query Database for first-level-division data
     * @return Returns First-level-division Data
     * @throws SQLException
     */
    public static ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions() throws SQLException
    {
        ObservableList<firstLevelDivisionAccess> firstLevelDivisionAccessObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int country_ID = rs.getInt("Country_ID");
            firstLevelDivisionAccess firstLevelDivision = new firstLevelDivisionAccess(divisionID, divisionName, country_ID);
            firstLevelDivisionAccessObservableList.add(firstLevelDivision);
        }
        return firstLevelDivisionAccessObservableList;
    }

    public static String findFLDID(String division) throws SQLException
    {
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM first_level_divisions WHERE division =?");
        ps.setString(1, division);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            division = rs.getString("Division_ID");
        }
        return division;
    }

}
