package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Access.appointmentAccess;
import Access.contactAccess;
import Model.Contacts;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Model.Customer;
import Model.Schedule;
import static Controller.MainController.selectedAppointment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import Model.Appointment;
import Utilities.DBConnection;
import Utilities.Query;

/** Controller class for editing appointments.
 *
 * @author Ladd Gillies Jr
 */

public class EditAppointmentController implements Initializable
{
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, String> custNameColumn;
    @FXML private TableColumn<Customer, String> custphoneNumberColumn;

    @FXML private TextField descriptionTxt;
    @FXML private TextField locationTxt;
    @FXML TextField appWith;
    @FXML TextField titleTxt;
    @FXML TextField typeTxt;
    @FXML private TextField userIDTxt;

    @FXML ComboBox<String> contactDrop;
    @FXML DatePicker appDatePicker;
    @FXML ComboBox<String> startComboBox;
    @FXML ComboBox<String> endComboBox;




    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final ZoneId sysid = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    /** Method for Saving the edited appointment.
     *
     * @param event
     * @throws IOException
     */
    public void saveButtonPushed(ActionEvent event) throws IOException{

        if(validateAppointment()) {
            String customerName = appWith.getText();
            String appointmentTitle = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String appointmentType = typeTxt.getText();
            int userID = Integer.parseInt(userIDTxt.getText());
            String loggedInUser = DBConnection.loggedInUser.getUsername();
            int appointmentId = selectedAppointment.getAppointmentID();
            int appCustomerId;

            if (customersTableView.getSelectionModel().getSelectedItem() == null) {
                appCustomerId = selectedAppointment.getAppCustomerID();
            } else {
                appCustomerId = customersTableView.getSelectionModel().getSelectedItem().getCustomerID();
            }

            LocalDate appDate = appDatePicker.getValue();
            LocalTime appStartTime = LocalTime.parse(startComboBox.getSelectionModel().getSelectedItem(), timeDTF);
            LocalTime appEndTime = LocalTime.parse(endComboBox.getSelectionModel().getSelectedItem(), timeDTF);

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
            } else {


                /** Makes a query to update the appointment.
                 *
                 */
                Query.makeQuery("UPDATE appointments SET Customer_ID = '" + appCustomerId + "', Title = '" + appointmentTitle + "', Description = '" + description + "', Location = '" + location + "', Type = '" + appointmentType + "', Start = '" + startTS + "' , End = '" + endTS + "', User_ID = '" +userID+ "', Last_Update = NOW() WHERE Appointment_ID = " + appointmentId + "");

                // create appointment
                Appointment freshAppointment = new Appointment();
                freshAppointment.setAppCustomerID(appCustomerId);
                freshAppointment.setAppmntDescription(description);
                freshAppointment.setAppmntLocation(location);
                freshAppointment.setAppointmentTitle(appointmentTitle);
                freshAppointment.setAppointmentType(appointmentType);
                freshAppointment.setAppointmentCustomer(customerName);
                freshAppointment.setAppointmentConsultant(loggedInUser);
                freshAppointment.setUserID(userID);
                freshAppointment.setStart(localStartZDT);
                freshAppointment.setEnd(localEndZDT);

                LocalDate now = LocalDate.now();
                LocalDate monthLater = now.plusMonths(1);
                LocalDate weekLater = now.plusDays(7);
                Schedule.getAllAppointments().clear();
                Schedule.getWeeklyAppointments().clear();
                Schedule.getMonthlyAppointments().clear();
                Schedule.populateAllAppointmentsList();
                Schedule.populateWeeklyAppointmentsList();
                Schedule.populateMonthlyAppointmentsList();

                //change scene to main
                Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
                Scene mainScene = new Scene(mainViewParent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(mainScene);
                window.show();
            }
        }
    }

    /** Method for validating the appointment changes to make sure there are no conflicts.
     *
     * @return Returns a boolean.
     */
    private boolean validateAppointment(){
        String appCustomer = appWith.getText();
        String title = titleTxt.getText();
        String appType = typeTxt.getText();
        LocalDate appDate = appDatePicker.getValue();
        LocalTime appStartTime = LocalTime.parse(startComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime appEndTime = LocalTime.parse(endComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        LocalDateTime appStartDT = LocalDateTime.of(appDate, appStartTime);
        LocalDateTime appEndDT = LocalDateTime.of(appDate, appEndTime);
        //UTC timezone
        ZonedDateTime startUTC = appStartDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = appEndDT.atZone(sysid).withZoneSameInstant(ZoneId.of("UTC"));

        String errorMessage = "";
        //check for null fields
        if (appCustomer == null || appCustomer.length() == 0) {
            errorMessage += "Please Select a Customer.\n";
        }
        if (title == null || title.length() == 0) {
            errorMessage += "Please enter an Appointment title.\n";
        }
        if (appType == null || appType.length() == 0) {
            errorMessage += "Please select an Appointment type.\n";
        }
        if(appDate == null){
            errorMessage += "Please select a Day";
        }
        if (startUTC == null) {
            errorMessage += "Please select a Start time";
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

    /** Method for handling a conflict of appointment scheduling.
     *
     * @param newStart Get new Start time.
     * @param newEnd Get new end time.
     * @return Returns boolean value.
     * @throws SQLException
     */
    private boolean hasApptConflict(ZonedDateTime newStart, ZonedDateTime newEnd) throws SQLException {

        int appointmentId = selectedAppointment.getAppointmentID();
        String consultant = selectedAppointment.getAppointmentConsultant();

        // Query appointment for times to check.
        try {
            Query.makeQuery("SELECT * FROM appointments "
                    + "WHERE ('"+ Timestamp.valueOf(newStart.toLocalDateTime()) +"' BETWEEN Start AND End OR '"+ Timestamp.valueOf(newEnd.toLocalDateTime()) +"' "+
                    "BETWEEN Start AND End OR '"+ Timestamp.valueOf(newStart.toLocalDateTime()) +"' < Start AND '" + Timestamp.valueOf(newEnd.toLocalDateTime()) + "' > End) "
                    + "AND (Created_By = '"+ consultant +"' AND Appointment_ID != " + appointmentId + ")");

            ResultSet rs = Query.getResult();

            if(rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return false;
    }


    /** Method for canceling action and returning to main menu.
     *
     * @param event
     * @throws IOException
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException{
        //change scene to main
        Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
        Scene mainScene = new Scene(mainViewParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            // populate customer TableView
            custNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
            custphoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPhoneNum()));
            customersTableView.setItems(Schedule.getAllCustomers());
            appWith.setText(selectedAppointment.getAppointmentCustomer());

            //Lambda Expression: adds an eventListener to TableView
            customersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    appWith.setText(newSelection.getCustomerName());
                }
            });

            /**
             * set time combo boxes based on normal business hours 8am-10pm
             */
            LocalTime time = LocalTime.of(0, 0);
            do {
                startTimes.add(time.format(timeDTF));
                endTimes.add(time.format(timeDTF));
                time = time.plusMinutes(15);
            } while (!time.equals(LocalTime.of(23, 45)));
            startTimes.remove(startTimes.size() - 1);
            endTimes.remove(0);

            startComboBox.setItems(startTimes);
            endComboBox.setItems(endTimes);
            startComboBox.getSelectionModel().select(LocalTime.of(0, 0).format(timeDTF));
            endComboBox.getSelectionModel().select(LocalTime.of(23, 59).format(timeDTF));


            String m = String.format("%s", selectedAppointment.getUserID());
            ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
            ObservableList<String> allContactsNames = FXCollections.observableArrayList();
            contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
            contactDrop.setItems(allContactsNames);
            //set fields to values from selected appointment
            titleTxt.setText(selectedAppointment.getAppointmentTitle());
            locationTxt.setText(selectedAppointment.getAppmntLocation());
            descriptionTxt.setText(selectedAppointment.getAppmntDescription());
            contactDrop.getSelectionModel().select(selectedAppointment.getContact());
            typeTxt.setText(selectedAppointment.getAppointmentType());
            userIDTxt.setText(m);

            startComboBox.getSelectionModel().select(selectedAppointment.getStart().toLocalTime().format(timeDTF));
            endComboBox.getSelectionModel().select(selectedAppointment.getEnd().toLocalTime().format(timeDTF));

        }catch(Exception e){e.printStackTrace();}
    }

    /** Method for contact combo box.
     *
     * @param event
     */
    public void contactDrop(ActionEvent event)
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
