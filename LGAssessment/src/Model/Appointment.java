package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/** Model Class for Appointment.
 *
 * @author Ladd Gillies
 */
public class Appointment {

    private int appmntID;
    private int appointmentID;
    private int appCustomerID;
    private String appmntTitle;
    private String appmntDescription;
    private String appmntLocation;
    private String appmntType;
    private ZonedDateTime appmntStart;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime appmntEnd;
    private String appmntCustomer;
    private String appmntConsultant;
    private Integer userID;
    private LocalDateTime appmntCreateDate;
    private String appmntCreatedBy;
    private Timestamp appmntLastUpdate;
    private String appmntLastUpdateBy;
    private int contactID;
    private String contact;


    /** Constructors.
     *
     */

    public Appointment(){

    }

    public Appointment(int appointmentID, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
    }

    /** Setters and Getters.
     *
     * @return Returns Appointment ID, Title, Description, Location, Type, Start Time, End Time, Customer ID, User Id, User Name, Contact Name, Contact ID.
     */
    public Integer getUserID() {
        return userID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAppmntDescription() {
        return appmntDescription;
    }

    public void setAppmntDescription(String appmntDescription) {
        this.appmntDescription = appmntDescription;
    }

    public String getAppmntLocation() {
        return appmntLocation;
    }

    public void setAppmntLocation(String appmntLocation) {
        this.appmntLocation = appmntLocation;
    }

    public void setAppmntID(int appmntID) {
        this.appmntID = appmntID;
    }

    public int getAppmntID(){return appmntID;}

    public void setAppointmentID(int ID) { this.appointmentID = ID;}

    public int getAppointmentID() { return appointmentID; }

    public void setAppCustomerID(int id){
        this.appCustomerID = id;
    }

    public int getAppCustomerID(){
        return appCustomerID;
    }

    public String getAppointmentTitle(){
        return appmntTitle;
    }

    public void setAppointmentTitle(String title){
        this.appmntTitle = title;
    }

    public String getAppointmentType(){
        return appmntType;
    }

    public void setAppointmentType(String type){
        this.appmntType = type;
    }

    //public void setAppointmentStart(ZonedDateTime start){ this.appmntStart = start; }

    //public ZonedDateTime getAppointmentStart(){return appmntStart;}

   //public void setAppointmentEnd(ZonedDateTime end){this.appmntEnd = end;}

    //public ZonedDateTime getAppointmentEnd(){return appmntEnd;}

    public void setAppointmentCustomer(String customer){
        this.appmntCustomer = customer;
    }

    public String getAppointmentCustomer(){
        return appmntCustomer;
    }

    public void setAppointmentConsultant(String consultant){
        this.appmntConsultant = consultant;
    }

    public String getAppointmentConsultant(){
        return appmntConsultant;
    }

    public int getContactID() { return contactID;}

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }
}


