package Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Utility Class for Accessing SQL Database for Querying.
 *
 * @author Ladd Gillies.
 */
public class Query
{
    private static String query;
    private static Statement statement;
    private static ResultSet result;

    // accepts query and execute based on query type
    public static void makeQuery(String q){
        query = q;

        try{
            //create Statement Object
            statement = DBConnection.getConnection().createStatement();

            //Determine query execution
            if(query.toLowerCase().startsWith("select")){
                result = statement.executeQuery(query);
            }
            if(query.toLowerCase().startsWith("delete") || query.toLowerCase().startsWith("insert") || query.toLowerCase().startsWith("update")){
                statement.executeUpdate(query);
            }
        } catch(SQLException e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ResultSet getResult(){
        return result;
    }
}
