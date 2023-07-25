package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

import Access.appointmentAccess;
import Access.contactAccess;
import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Utilities.DBConnection;
import Utilities.Query;

/**
 * Class for Adding Appointment Controller.
 *
 * @author Ladd Gillies
 */
public class AddAppointmentController implements Initializable {

    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, String> custNameColumn;
    @FXML private TableColumn<Customer, String> custphoneNumberColumn;

    @FXML private TextField descriptionTxt;
    @FXML private TextField locationTxt;
    @FXML private TextField userIDTxt;
    @FXML TextField appWithTextField;
    @FXML TextField appTitleTextField;
    @FXML TextField appTypeTextField;
    @FXML DatePicker appDatePicker;
    @FXML ComboBox<String> appStartComboBox;
    @FXML ComboBox<String> appEndComboBox;
    @FXML ComboBox<String> contactDrop;


    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final ZoneId sysid = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    /** Button for Saving a New Appointment.
     *
     * @param event Switch Screen back to Main Menu
     * @throws IOException
     */
    public void saveButtonPushed(ActionEvent event) throws IOException, SQLException {

        if(validateAppointment()) {
            String title = appTitleTextField.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String appType = appTypeTextField.getText();
            String contact = contactDrop.getValue();

            int contactID = Integer.parseInt(contactAccess.findContactID(contact));
            Customer withCustomer = customersTableView.getSelectionModel().getSelectedItem();
            int customerId = withCustomer.getCustomerID();
            String customerName = withCustomer.getCustomerName();
            String loggedInUser = DBConnection.loggedInUser.getUsername();
            int userId = DBConnection.loggedInUser.getuserID();
            int userID = Integer.parseInt(userIDTxt.getText());

            LocalDate appDate = appDatePicker.getValue();
            LocalTime appStartTime = LocalTime.parse(appStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
            LocalTime appEndTime = LocalTime.parse(appEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

            LocalDateTime appStartDT = LocalDateTime.of(appDate, appStartTime);
            LocalDateTime appEndDT = LocalDateTime.of(appDate, appEndTime);
            //UTC timezone
            ZonedDateTime startUTC = appStartDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = appEndDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp startTS = Timestamp.valueOf(startUTC.toLocalDateTime());
            Timestamp endTS = Timestamp.valueOf(endUTC.toLocalDateTime());

            ZonedDateTime localStartZDT = appStartDT.atZone(sysid);
            ZonedDateTime localEndZDT = appEndDT.atZone(sysid);

            boolean id = Schedule.validateBusinessHours(appStartDT, appEndDT, appDate);

            if (id == false) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Please correct Appointment Time");
                alert.setContentText("Please choose an appointment time within business hours 8AM - 10PM EST");
                alert.showAndWait();
            }else{
                int newAppointmentID = Schedule.makeAppointmentID();
                //Send Insert statement to Query Class.
                Query.makeQuery("INSERT INTO appointments (Appointment_ID, Customer_ID, User_ID, Title, Description, Location, Contact_ID, Type, Start, End, Create_Date, Created_By, Last_Updated_By, Last_Update)\n" +
                        "VALUES (" + newAppointmentID + ", " + customerId + ", " + userId + ", '" + title + "', '" + description + "', '" + location + "', '" + contactID + "', '" + appType + "', '" + startTS + "', '" + endTS + "', NOW(), '" + loggedInUser + "', '" + loggedInUser + "', NOW())");


                Appointment newAppointment = new Appointment();
                newAppointment.setAppointmentID(newAppointmentID);
                newAppointment.setAppCustomerID(customerId);
                newAppointment.setAppmntDescription(description);
                newAppointment.setAppmntLocation(location);
                newAppointment.setContact(contact);
                newAppointment.setAppointmentTitle(title);
                newAppointment.setAppointmentType(appType);
                newAppointment.setAppointmentCustomer(customerName);
                newAppointment.setUserID(userID);
                newAppointment.setContactID(contactID);
                newAppointment.setAppointmentConsultant(loggedInUser);
                newAppointment.setStart(localStartZDT);
                newAppointment.setEnd(localEndZDT);

                LocalDate now = LocalDate.now();
                LocalDate monthLater = now.plusMonths(1);
                LocalDate weekLater = now.plusDays(7);

                Schedule.addAppointmentAll(newAppointment);

                if (appDate.isAfter(now.minusDays(1)) && appDate.isBefore(monthLater)) {
                    //add appointment to monthly list
                    Schedule.addAppointmentMonthly(newAppointment);
                }

                if (appDate.isEqual(now.minusDays(1)) && appDate.isBefore(weekLater)) {
                    //add appointment to weekly list
                    Schedule.addAppointmentWeekly(newAppointment);
                }
                //add appointment to all appointments list
                System.out.println(Schedule.getAllAppointments());

                //Return to Main Menu
                Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
                Scene mainScene = new Scene(mainViewParent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(mainScene);
                window.show();
            }
        }


    }

    /** Method for Validating Appointment.
     *
     * @return Returns Boolean Value
     */
    private boolean validateAppointment(){

        String appContact = contactDrop.getSelectionModel().getSelectedItem();
        String title = appTitleTextField.getText();
        String appType = appTypeTextField.getText();
        LocalDate appDate = appDatePicker.getValue();
        LocalTime appStartTime = LocalTime.parse(appStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime appEndTime = LocalTime.parse(appEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        LocalDateTime appStartDT = LocalDateTime.of(appDate, appStartTime);
        LocalDateTime appEndDT = LocalDateTime.of(appDate, appEndTime);
        //UTC timezone
        ZonedDateTime startUTC = appStartDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = appEndDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));

        String errorMessage = "";
        //check for null fields
        if (appContact == null || appContact.length() == 0) {
            errorMessage += "Please Select a Customer.\n";
        }
        if (title == null || title.length() == 0) {
            errorMessage += "Please enter an Appointment title.\n";
        }
        if (appType == null || appType.length() == 0) {
            errorMessage += "Please select an Appointment type.\n";
        }
        if (startUTC == null) {
            errorMessage += "Please select a Start time";
        }
        if(appDate.isBefore(LocalDate.now())){
            errorMessage += "Please select a date in the future.\n";
        }
        if (endUTC == null) {
            errorMessage += "Please select an End time.\n";
            //checks that Start and End times are not the same
            //and that end time is not before start time
        } else if (endUTC.equals(startUTC) || endUTC.isBefore(startUTC)){
            errorMessage += "End time must be after Start time.\n";
        } else try {
            //checks logged in user's appointments for time conflicts
            if (hasApptConflict(startUTC, endUTC)){
                errorMessage += "Appointment times conflict with Users existing appointments. Please select a new time.\n";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Please correct invalid Appointment fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }

    }


    /** Method for checking to see if there is a conflict.
     *
     * @param newStart Get start time
     * @param newEnd Get end time
     * @return Return Boolean
     * @throws SQLException
     */
    private boolean hasApptConflict(ZonedDateTime newStart, ZonedDateTime newEnd) throws SQLException
    {

        String consultant = DBConnection.loggedInUser.getUsername();

        try {
            Query.makeQuery("SELECT * FROM appointments "
                    + "WHERE ('"+ Timestamp.valueOf(newStart.toLocalDateTime()) +"' BETWEEN Start AND End OR '"+ Timestamp.valueOf(newEnd.toLocalDateTime()) +"' "+
                    "BETWEEN Start AND End OR '"+ Timestamp.valueOf(newStart.toLocalDateTime()) +"' < Start AND '" + Timestamp.valueOf(newEnd.toLocalDateTime()) + "' > End) "
                    + "AND (Created_By = '"+ consultant +"' AND Appointment_ID != 0)");

            ResultSet rs = Query.getResult();

            if(rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return false;
    }

    /** Button for Canceling an Action.
     *
     * @param event Switch Screen back to Main Menu
     * @throws IOException
     */
    public void cancelButtionPushed(ActionEvent event) throws IOException
    {
        Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
        Scene mainScene = new Scene(mainViewParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }

    /** Initialize.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try {


        // populate customer TableView
        custNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        custphoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPhoneNum()));
        customersTableView.setItems(Schedule.getAllCustomers());

        // Lambda Expression: adds an eventListener to TableView
        customersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                appWithTextField.setText(newSelection.getCustomerName());
            }
        });

        //set datepicker
        appDatePicker.setValue(LocalDate.now());

        /**
         * set time combo boxes based on most businesses hours (8am-10pm)
         */
        LocalTime time = LocalTime.of(0, 0);
        do {
            startTimes.add(time.format(timeDTF));
            endTimes.add(time.format(timeDTF));
            time = time.plusMinutes(15);
        } while (!time.equals(LocalTime.of(23, 45)));
        startTimes.remove(startTimes.size() - 1);
        endTimes.remove(0);

        appStartComboBox.setItems(startTimes);
        appEndComboBox.setItems(endTimes);
        appStartComboBox.getSelectionModel().select(LocalTime.of(0, 0).format(timeDTF));
        appEndComboBox.getSelectionModel().select(LocalTime.of(23, 45).format(timeDTF));
        ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        contactDrop.setItems(allContactsNames);
    }
    catch(Exception e)
    {
        e.printStackTrace();
        }
    }

    /** Method for Combo Boxes for both country and state/providence.
     *
     * @param event
     */
    @FXML public void contactDrop(ActionEvent event)
    {
        try
        {
            int id = 0;

            ObservableList<Appointment> getAllAppointmentData = appointmentAccess.getAllAppointments();
            ObservableList<Appointment> appointmentInfo = FXCollections.observableArrayList();
            ObservableList<Contacts> getAllContacts = contactAccess.getAllContacts();

            Appointment contactInfo;
            String contactName = contactDrop.getSelectionModel().getSelectedItem();
            for(Contacts contact:  getAllContacts)
            {
                if(contactName.equals(contact.getContactName()))
                {
                    id = contact.getContactID();
                }
            }
            for(Appointment appointment: getAllAppointmentData)
            {
                if (appointment.getContactID() == id) {
                    contactInfo = appointment;
                    appointmentInfo.add(contactInfo);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}