package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import Model.User;

/** Utility Class for Accessing the Database.
 *
 * @author Ladd Gillies
 */

public class DBConnection {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String ipAddress = "//127.0.0.1:3306/client_schedule";
    private static final String jdbcUrl = protocol + vendor + ipAddress + "?connectionTimeZone = SERVER";
    private static final String MYSQLJBCDriver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection conn = null;
    public static User loggedInUser;

    private static final String FILENAME = "login_activity.txt";


    //This connects to the DB
    public static Connection startConnection(){
        try {
            Class.forName(MYSQLJBCDriver);
            conn = (Connection)DriverManager.getConnection(jdbcUrl, userName, password); //returns our database conncetion
            System.out.println("Connection Successfull");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return conn;
    }

    //This method returns the DB connection
    public static Connection getConnection(){
        return conn;
    }

    //This closes the DB connection
    public static void closeConnection(){
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //return user that is currently logged in
    public User getLoggedInUser(){
        return loggedInUser;
    }

    //Validate login attempted
    public static Boolean validateLogin(String username, String password){
        Query.makeQuery("SELECT * FROM users WHERE User_Name='" + username + "' AND Password='" + password + "'");
        ResultSet userSet = Query.getResult();
        try {
            //call next() because internal cursor initially doesn't point to anything
            if(userSet.next()){
                loggedInUser = new User();
                loggedInUser.setUsername(userSet.getString("User_Name"));
                loggedInUser.setuserID(userSet.getInt("User_ID"));
                //Log user successful log-in activity and append to .txt file if one exists
                log(username, true);
                return true;
            } else {
                //log failed log in attempt and append to .txt file if one exists
                log(username, false);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    /** Method for Creating a login log.
     *
     * @param userName Get User Name
     * @param success Get Success or Failure.
     */
    public static void log(String userName, boolean success){
        try (FileWriter fw = new FileWriter(FILENAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(ZonedDateTime.now() + " User: " + userName + " Login_Attempt: " + (success ? " Success" : " Failure"));
        } catch (IOException e) {
            System.out.println("Logger Error: " + e.getMessage());
        }
    }

}
