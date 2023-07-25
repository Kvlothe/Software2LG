package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

import Controller.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Utilities.DBConnection;
import Utilities.Query;

/** Model Class for the Schedule.
 *
 * @author Ladd Gillies.
 */
public class Schedule
{
    //Observable Lists for all the Tables
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Country> allCountries = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();

    //get zoneId for user's local time
    private static ZoneId localZoneId = ZoneId.systemDefault();

    /** Populate a List with Customers.
     *
     * @return Returns all Customers List.
     */
    public static ObservableList<Customer> getAllCustomers(){

        return allCustomers;
    }

    /** Method for creating a new appointment ID.
     *
     * @return Returns a new appointment ID based on the size of the current list.
     * Possibility for problems if appointments are deleted and added without updating the other IDs.
     */
    public static int makeAppointmentID(){
        int newID = allAppointments.size() + 1;
        return newID;
    }

    /** Method for creating a new customer ID.
     *
     * @return Returns a new customer ID based on the size of the current list.
     * Same possible problem as Appointment ID with duplicate Primary Keys. Fix needed.
     */
    public static int makeCustomerID(){
        int newID = allCustomers.size() + 1;
        return newID;
    }

    /** Method for adding a new Customer.
     *
     * @param c Get new Customer to add to current List.
     */
    public static void addCustomer(Customer c){
        allCustomers.add(c);
    }

    /** Method for Deleting a Customer.
     *
     * @param c Get ID for which customer to delete.
     */
    public static void deleteCustomer(Customer c) throws SQLException {

        Query.makeQuery("DELETE FROM appointments WHERE Customer_ID = '" +c.getCustomerID() +"'");
        allAppointments.remove(c);
        Query.makeQuery("DELETE FROM customers WHERE Customer_ID = '" + c.getCustomerID() + "'");
        allCustomers.remove(c);
        allAppointments.clear();
        weeklyAppointments.clear();
        monthlyAppointments.clear();
        populateAllAppointmentsList();
        populateWeeklyAppointmentsList();
        populateMonthlyAppointmentsList();
    }

    /** Method for getting all Appointments.
     *
     * @return Returns all Appointments.
     */
    public static ObservableList<Appointment> getAllAppointments(){

        return allAppointments;
    }

    /** Method for getting monthly appointments.
     *
     * @return Returns Monthly Appointments.
     */
    public static ObservableList<Appointment> getMonthlyAppointments(){

        return monthlyAppointments;
    }

    /** Method for adding an appointment to all Appointments.
     *
     * @param a Get new appointment to add to list.
     */
    public static void addAppointmentAll(Appointment a){
        allAppointments.add(a);
    }
    /** Method for adding an appointment to monthly Appointments.
     *
     * @param a Get new appointment to add to list.
     */
    public static void addAppointmentMonthly(Appointment a){
        monthlyAppointments.add(a);
    }
    /** Method for adding an appointment to weekly Appointments.
     *
     * @param a Get new appointment to add to list.
     */
    public static void addAppointmentWeekly(Appointment a){
        weeklyAppointments.add(a);
    }

    /** Methods for removing appointments from All, Monthly and Weekly Tables.
     *
     * @param a Get appointment to remove from tables.
     */
    public static void removeAppointmentMonthly(Appointment a){
        monthlyAppointments.remove(a);
    }
    public static void removeAppointmentWeekly(Appointment a){
        weeklyAppointments.remove(a);
    }
    public static void removeAppointmentFromAll(Appointment a){
        allAppointments.remove(a);
    }

    /** Method for Deleting an Appointment.
     *
     * @param a Get Appointment ID for determining which one to delete.
     */
    public static void deleteAppointment(Appointment a){
        Query.makeQuery("DELETE FROM appointments WHERE Appointment_ID = '" + a.getAppointmentID() + "'");
        allAppointments.clear();
        weeklyAppointments.clear();
        monthlyAppointments.clear();
        populateAllAppointmentsList();
        populateWeeklyAppointmentsList();
        populateMonthlyAppointmentsList();
    }

    // Observable Lists for Country and Appointment
    public static ObservableList<Country> getAllCountries(){ return allCountries; }

    public static ObservableList<Appointment> getWeeklyAppointments(){ return weeklyAppointments; }


    /** Method for Populating the Customer Table.
     *
     */
    public static void populateCustomersList(){
        try {
            Query.makeQuery("SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID join countries on first_level_divisions.Country_ID = countries.Country_ID");
            ResultSet customerSet = Query.getResult();
            while(customerSet.next()){
                Customer c = new Customer();
                c.setCustomerID(customerSet.getInt("Customer_ID"));
                c.setCustomerName(customerSet.getString("Customer_Name"));
                c.setCustomerAddress(customerSet.getString("Address"));
                c.setCustomerPhone(customerSet.getString("Phone"));
                c.setCustomerCountry(customerSet.getString("Country"));
                c.setFLDID(customerSet.getInt("Division_ID"));
                c.setCustomerCity(customerSet.getString("Division"));
                c.setCustomerPostalCode(customerSet.getString("Postal_Code"));

                Schedule.getAllCustomers().add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Method for Populating Weekly Appointment Table upon radio button selection.
     *
     */
    public static void populateWeeklyAppointmentsList(){
        try {
            Query.makeQuery("SELECT * FROM appointments JOIN customers ON appointments.Customer_ID=customers.Customer_ID \n" +
                    "JOIN users ON appointments.User_ID=users.User_ID \n" +
                    "WHERE START BETWEEN NOW() AND (SELECT ADDDATE(NOW(), INTERVAL 7 DAY))");
            ResultSet appmntSet = Query.getResult();
            while(appmntSet.next()){
                Appointment appmnt = new Appointment();
                appmnt.setAppmntID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppointmentID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppCustomerID(appmntSet.getInt("Customer_ID"));
                appmnt.setAppointmentTitle(appmntSet.getString("Title"));
                appmnt.setAppmntDescription(appmntSet.getString("Description"));
                appmnt.setAppmntLocation(appmntSet.getString("Location"));
                appmnt.setAppointmentType(appmntSet.getString("Type"));
                appmnt.setAppointmentCustomer(appmntSet.getString("Customer_Name"));
                appmnt.setAppointmentConsultant(appmntSet.getString("User_Name"));
                appmnt.setUserID(appmntSet.getInt("User_ID"));
                appmnt.setContactID(appmntSet.getInt("Contact_ID"));
                //appmnt.setContact(appmntSet.getString("Contact_Name"));

                LocalDateTime startldt = appmntSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endldt = appmntSet.getTimestamp("End").toLocalDateTime();

                ZonedDateTime startzdt = startldt.atZone(ZoneId.of("UTC"));
                ZonedDateTime endzdt = endldt.atZone(ZoneId.of("UTC"));
                //convert to user's local time zone
                ZonedDateTime localStartZDT = startzdt.withZoneSameInstant(localZoneId);
                ZonedDateTime localEndZDT = endzdt.withZoneSameInstant(localZoneId);

                appmnt.setStart(startzdt);
                appmnt.setEnd(endzdt);
                Schedule.getWeeklyAppointments().add(appmnt);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Method for Populating the monthly Appointment Table upon radio button selection.
     *
     */
    public static void populateMonthlyAppointmentsList(){
        try {
            Query.makeQuery("SELECT * FROM appointments JOIN customers ON appointments.Customer_ID=customers.Customer_ID JOIN users ON appointments.User_ID=users.User_ID\n" +
                    "WHERE start between (SELECT DATE_ADD(DATE_ADD(LAST_DAY(current_date()), INTERVAL 1 DAY), INTERVAL - 1 MONTH)) and DATE_ADD(LAST_DAY(current_date()), INTERVAL 1 DAY)");

            ResultSet appmntSet = Query.getResult();
            while(appmntSet.next()){
                Appointment appmnt = new Appointment();
                appmnt.setAppointmentID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppmntID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppCustomerID(appmntSet.getInt("Customer_ID"));
                appmnt.setAppointmentTitle(appmntSet.getString("Title"));
                appmnt.setAppmntDescription(appmntSet.getString("Description"));
                appmnt.setAppointmentType(appmntSet.getString("Type"));
                appmnt.setAppointmentCustomer(appmntSet.getString("Customer_Name"));
                appmnt.setAppointmentConsultant(appmntSet.getString("User_Name"));
                appmnt.setAppmntLocation(appmntSet.getString("Location"));
                appmnt.setUserID(appmntSet.getInt("User_ID"));
                appmnt.setContactID(appmntSet.getInt("Contact_ID"));
                ///appmnt.setContact(appmntSet.getString("Contact_Name"));


                LocalDateTime startldt = appmntSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endldt = appmntSet.getTimestamp("End").toLocalDateTime();

                ZonedDateTime startzdt = startldt.atZone(ZoneId.of("UTC"));
                ZonedDateTime endzdt = endldt.atZone(ZoneId.of("UTC"));
                //convert to user's local time zone
                ZonedDateTime localStartZDT = startzdt.withZoneSameInstant(localZoneId);
                ZonedDateTime localEndZDT = endzdt.withZoneSameInstant(localZoneId);

                appmnt.setStart(startzdt);
                appmnt.setEnd(endzdt);
                Schedule.getMonthlyAppointments().add(appmnt);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Method for populating all appointments table.
     *
     */
    public static void populateAllAppointmentsList(){
        try {
            Query.makeQuery("SELECT * FROM appointments JOIN customers ON appointments.Customer_ID=customers.Customer_ID JOIN users ON appointments.User_ID=users.user_ID JOIN contacts ON appointments.Contact_ID=contacts.contact_ID");
            ResultSet appmntSet = Query.getResult();
            while(appmntSet.next()){
                Appointment appmnt = new Appointment();
                appmnt.setAppointmentID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppmntID(appmntSet.getInt("Appointment_ID"));
                appmnt.setAppCustomerID(appmntSet.getInt("Customer_ID"));
                appmnt.setAppointmentTitle(appmntSet.getString("Title"));
                appmnt.setAppmntDescription(appmntSet.getString("Description"));
                appmnt.setAppointmentType(appmntSet.getString("Type"));
                appmnt.setAppointmentCustomer(appmntSet.getString("Customer_Name"));
                appmnt.setAppointmentConsultant(appmntSet.getString("User_Name"));
                appmnt.setContact(appmntSet.getString("Contact_Name"));
                appmnt.setUserID(appmntSet.getInt("User_ID"));
                appmnt.setContactID(appmntSet.getInt("Contact_ID"));
                appmnt.setAppmntLocation(appmntSet.getString("Location"));


                LocalDateTime startldt = appmntSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endldt = appmntSet.getTimestamp("End").toLocalDateTime();

                ZonedDateTime startzdt = startldt.atZone(ZoneId.of("UTC"));
                ZonedDateTime endzdt = endldt.atZone(ZoneId.of("UTC"));

                //convert to user's local time zone
                ZonedDateTime localStartZDT = startzdt.withZoneSameInstant(localZoneId);
                ZonedDateTime localEndZDT = endzdt.withZoneSameInstant(localZoneId);

                appmnt.setStart(startzdt);
                appmnt.setEnd(endzdt);
                Schedule.getAllAppointments().add(appmnt);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateCustomer(int index, Customer c) {
        allCustomers.set(index, c);
    }

    public static void updateWeeklyAppointment(int index, Appointment a){
        weeklyAppointments.set(index, a);
    }

    public static void updateMonthlyAppointment(int index, Appointment a){
        monthlyAppointments.set(index, a);
    }

    public static void updateAllAppointment(int index, Appointment a){
        allAppointments.set(index, a);
    }

    /** Method for getting the current users appointments.
     *
     * @return Returns Logged in Users Appointments.  Needed for Weekly and monthly tables.
     */
    public static ObservableList<Appointment> getConsultantAppointments(){

        ObservableList<Appointment> consultantAppointments = FXCollections.observableArrayList();
        int consultantId = DBConnection.loggedInUser.getuserID();

        try {
            Query.makeQuery("SELECT * FROM appointments JOIN customers ON appointments.Customer_ID=customers.Customer_ID JOIN users ON appointments.User_ID=users.User_ID\n" +
                    "WHERE appointments.User_ID = " + consultantId + "");

            ResultSet consultantSet = Query.getResult();
            while(consultantSet.next()){
                Appointment appmnt = new Appointment();
                appmnt.setAppmntID(consultantSet.getInt("Appointment_ID"));
                appmnt.setAppCustomerID(consultantSet.getInt("Customer_ID"));
                appmnt.setAppointmentTitle(consultantSet.getString("Title"));
                appmnt.setAppmntLocation(consultantSet.getString("Location"));
                appmnt.setAppmntDescription(consultantSet.getString("Description"));
                appmnt.setAppointmentType(consultantSet.getString("Type"));
                appmnt.setAppointmentCustomer(consultantSet.getString("Customer_Name"));
                appmnt.setAppointmentConsultant(consultantSet.getString("User_Name"));

                LocalDateTime startldt = consultantSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endldt = consultantSet.getTimestamp("End").toLocalDateTime();

                ZonedDateTime startzdt = startldt.atZone(ZoneId.of("UTC"));
                ZonedDateTime endzdt = endldt.atZone(ZoneId.of("UTC"));
                //convert to user's local time zone
                ZonedDateTime localStartZDT = startzdt.withZoneSameInstant(localZoneId);
                ZonedDateTime localEndZDT = endzdt.withZoneSameInstant(localZoneId);

                appmnt.setStart(startzdt);
                appmnt.setEnd(endzdt);
                consultantAppointments.add(appmnt);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return consultantAppointments;
    }

    /** Method for validating Business hours.
     *
     * @param startDateTime Get Start Time
     * @param endDateTime Get End Time
     * @param apptDate Get Date
     * @return Return Boolean
     */
    public static Boolean validateBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate apptDate) {
        // (8am to 10pm EST, Not including weekends)
        // Turn into zonedDateTimeObject, so we can evaluate whatever time was entered in user time zone against EST

        ZonedDateTime startZonedDateTime = ZonedDateTime.of(startDateTime, LoginController.getUserTimeZone());
        ZonedDateTime endZonedDateTime = ZonedDateTime.of(endDateTime, LoginController.getUserTimeZone());

        ZonedDateTime startBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime endBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));

        // If startTime is before or after business hours
        // If end time is before or after business hours
        // if startTime is after endTime - these should cover all possible times entered and validate input.
        if (startZonedDateTime.isBefore(startBusinessHours) | startZonedDateTime.isAfter(endBusinessHours) |
                endZonedDateTime.isBefore(startBusinessHours) | endZonedDateTime.isAfter(endBusinessHours) |
                startZonedDateTime.isAfter(endZonedDateTime)) {
            return false;

        }
        else {
            return true;
        }

    }

    /** Method for Getting and Counting Appointments by Type and month.
     * Needed for Reports Tab/Page.
     * @return Returns List for populating Table on Reports.
     */
    public static ObservableList<AppointmentByMonth> getAppointmentTypesByMonth(){
        ObservableList<AppointmentByMonth> appTypesByMonthList = FXCollections.observableArrayList();

        try {
            Query.makeQuery("SELECT MONTHNAME(`start`) AS Month, type, COUNT(*) as Amount\n" +
                    "FROM appointments\n" +
                    "GROUP BY MONTHNAME(`Start`), Type");
            ResultSet typeByMonthSet = Query.getResult();
            while(typeByMonthSet.next()){
                AppointmentByMonth appTypeByMonth = new AppointmentByMonth();
                appTypeByMonth.setMonth(typeByMonthSet.getString("Month"));
                appTypeByMonth.setType(typeByMonthSet.getString("Type"));
                appTypeByMonth.setAmount(typeByMonthSet.getString("Amount"));
                appTypesByMonthList.add(appTypeByMonth);
            }

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return appTypesByMonthList;
    }

    /** Method for checking for upcoming appointments.
     *
     * @return Returns Boolean to determine if appointments are upcoming.
     */
    public static Appointment checkUpcomingAppointment(){
        Appointment upcomingAppointment;
        int currentUserId = DBConnection.loggedInUser.getuserID();

        LocalDateTime now = LocalDateTime.now();
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atZone(zid);
        LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime ldt2 = ldt.plusMinutes(15);

        try{

            Query.makeQuery("SELECT * FROM appointments \n" +
                    "JOIN customers ON appointments.Customer_ID=customers.Customer_ID JOIN users ON appointments.User_ID=Users.User_Id\n" +
                    "WHERE Start BETWEEN '" + ldt + "' AND '" + ldt2 + "' AND " +
                    "appointments.User_ID=" + currentUserId + "");

            ResultSet rs = Query.getResult();
            if(rs.next()){
                upcomingAppointment = new Appointment();
                upcomingAppointment.setAppmntID(rs.getInt("Appointment_ID"));
                upcomingAppointment.setAppCustomerID(rs.getInt("Customer_ID"));
                upcomingAppointment.setAppointmentTitle(rs.getString("Title"));
                upcomingAppointment.setAppmntDescription(rs.getString("Description"));
                upcomingAppointment.setAppmntLocation(rs.getString("Location"));
                upcomingAppointment.setAppointmentType(rs.getString("Type"));
                upcomingAppointment.setAppointmentCustomer(rs.getString("Customer_Name"));
                upcomingAppointment.setAppointmentConsultant(rs.getString("User_Name"));

                LocalDateTime startldt = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endldt = rs.getTimestamp("End").toLocalDateTime();
                ZonedDateTime startzdt = startldt.atZone(ZoneId.of("UTC"));
                ZonedDateTime endzdt = endldt.atZone(ZoneId.of("UTC"));
                //convert to user's local time zone
                ZonedDateTime localStartZDT = startzdt.withZoneSameInstant(localZoneId);
                ZonedDateTime localEndZDT = endzdt.withZoneSameInstant(localZoneId);


                upcomingAppointment.setStart(startzdt);
                upcomingAppointment.setEnd(endzdt);
                return upcomingAppointment;
            }
        }catch(SQLException ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return null;
    }
}
