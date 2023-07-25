package Controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import Access.contactAccess;
import Access.appointmentAccess;
import Model.Contacts;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Model.Appointment;
import Model.AppointmentByMonth;
import Model.Schedule;
import Utilities.DBConnection;
import Utilities.Query;

/** Controller for the reports page.
 *
 * @author Ladd Gillies
 */
public class ReportsController implements Initializable {

    @FXML private Label consultantNameLabel;
    @FXML private TableView<Appointment> scheduleTableView;
    @FXML private TableColumn<Appointment, String> schedCustomerColumn;
    @FXML private TableColumn<Appointment, String> schedTitleColumn;
    @FXML private TableColumn<Appointment, String> schedTypeColumn;
    @FXML private TableColumn<Appointment, String> schedStartColumn;
    @FXML private TableColumn<Appointment, String> schedEndColumn;

    @FXML private TableView<Appointment> contactTableView;
    @FXML private TableColumn<Appointment, String> appointmentIDTbl;
    @FXML private TableColumn<Appointment, String> typeTbl;
    @FXML private TableColumn<Appointment, String> titleTbl;
    @FXML private TableColumn<Appointment, String> descriptionTbl;
    @FXML private TableColumn<Appointment, String> startTbl;
    @FXML private TableColumn<Appointment, String> endTbl;
    @FXML private TableColumn<Appointment, Integer> customerIDTbl;

    @FXML private TableView<AppointmentByMonth> typeByMonthTableView;
    @FXML private TableColumn<AppointmentByMonth, String> byMonthColumn;
    @FXML private TableColumn<AppointmentByMonth, String> byMonthTypeColumn;
    @FXML private TableColumn<AppointmentByMonth, String> byMonthTotalColumn;

    @FXML private ComboBox<String> contactComboBox;
    @FXML private BarChart scoreChart;

    private final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("M/d/yy h:mm a");
    private String userName = DBConnection.loggedInUser.getUsername();

    /** Return to Main Menu
     *
     * @param event Switch Screen to Main Menu
     * @throws IOException
     */
    public void homeButtonPushed(ActionEvent event) throws IOException
    {
        Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
        Scene mainScene = new Scene(mainViewParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }

    @FXML public void contactDrop(ActionEvent event)
    {
        try
        {
            int id = 0;

            ObservableList<Appointment> getAllAppointmentData = Schedule.getAllAppointments();
            ObservableList<Appointment> appointmentInfo = FXCollections.observableArrayList();
            ObservableList<Contacts> getAllContacts = contactAccess.getAllContacts();

            Appointment contactInfo;
            String contactName = contactComboBox.getSelectionModel().getSelectedItem();
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
            contactTableView.setItems(appointmentInfo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /** Method for populating schedule table.
     * Use of Lambda Expression to make populating table easier.
     */
    public void populateScheduleTable()
    {
        consultantNameLabel.setText("Schedule For User: " + userName);
        schedCustomerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentCustomer()));
        schedTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentTitle()));
        schedTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentType()));
        schedStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDateTime)));
        schedEndColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getEnd().format(formatDateTime)));
        scheduleTableView.setItems(Schedule.getConsultantAppointments());
    }

    /** Method for populating appoint by month.
     * Use of Lambda Expression to make populating table easier.
     */
    public void populateAppTypeByMonthTable()
    {
        byMonthColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonth()));
        byMonthTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        byMonthTotalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAmount()));
        typeByMonthTableView.setItems(Schedule.getAppointmentTypesByMonth());
    }

    /** Method for populating chart.
     *
     */
    public void populateScoreChart()
    {
        ObservableList<XYChart.Data<String, Integer>> data = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        try {
            Query.makeQuery("SELECT User_Name, COUNT(User_Name) AS Appointments\n" +
                    "FROM appointments, users\n" +
                    "WHERE appointments.User_ID = users.User_ID\n" +
                    "GROUP BY User_Name");
            ResultSet rs = Query.getResult();

            while(rs.next()){
                String user = rs.getString("User_Name");
                Integer count = rs.getInt("Appointments");
                data.add(new XYChart.Data<>(user, count));
            }
        } catch (SQLException ex){
            System.out.println("Error: " + ex.getMessage());
        }
        series.getData().addAll(data);
        scoreChart.getData().add(series);
    }

    /** Method for opening up the logs.txt that is created every login.
     *
     * @param event
     */
    public void logsButtonPushed(ActionEvent event) {
        File file = new File("login_activity.txt");
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    System.out.println("Error Opening Log File: " + e.getMessage());
                }
            }
        }
    }


    /**
     * Initializes the controller class.
     * Lambda Expression for easy table population.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Populate tables.
        populateScheduleTable();
        populateAppTypeByMonthTable();
        populateScoreChart();


        appointmentIDTbl.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        customerIDTbl.setCellValueFactory(new PropertyValueFactory<>("appCustomerID"));

        // Lambda Expression to easily populate contact table.
        descriptionTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppmntDescription()));
        typeTbl.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getAppointmentType()));
        titleTbl.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getAppointmentTitle()));
        startTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDateTime)));
        endTbl.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getEnd().format(formatDateTime)));


        ObservableList<Contacts> contactsObservableList = null;
        try {
            contactsObservableList = contactAccess.getAllContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> allContactNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contacts -> allContactNames.add(contacts.getContactName()));
        contactComboBox.setItems(allContactNames);
    }

}
