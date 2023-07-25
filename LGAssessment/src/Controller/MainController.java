package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/** Class for the main menu controller.
 *
 * @author Ladd Gillies
 */
public class MainController implements Initializable {

    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, String> custNameColumn;
    @FXML private TableColumn<Customer, String> custAddressColumn;
    @FXML private TableColumn<Customer, String> custphoneNumberColumn;
    @FXML private TableColumn<Customer, String> custCountryColumn;
    @FXML private TableColumn<Customer, String> custDivisionIDColumn;
    @FXML private TableColumn<Customer, String> custPostalColumn;
    @FXML private TableColumn<Customer, Integer> IDTbl;
    @FXML private TableColumn<Customer, Integer> custDivisionIDTbl;
    @FXML private RadioButton weeklyRadioButton;
    @FXML private RadioButton monthlyRadioButton;
    @FXML private RadioButton allRadioButton;
    private ToggleGroup appmntToggleGroup;
    @FXML private TableView<Appointment> appmntTableView;
    @FXML private TableColumn<Appointment, String> customerTbl;
    @FXML private TableColumn<Appointment, String> descriptionTbl;
    @FXML private TableColumn<Appointment, String> locationTbl;
    @FXML private TableColumn<Appointment, String> userTbl;
    @FXML private TableColumn<Appointment, String> startTbl;
    @FXML private TableColumn<Appointment, String> endTbl;
    @FXML private TableColumn<Appointment, String> titleTbl;
    @FXML private TableColumn<Appointment, String> typeTbl;
    @FXML private TableColumn<Appointment, Integer> appointmentIDtbl;
    @FXML private TableColumn<Customer, Integer> customerIDTbl;
    @FXML private TableColumn<User, Integer> userIDTbl;
    @FXML private TableColumn<Appointment, String> contactTbl;
    @FXML private TableColumn<Contacts, Integer> contactIDTbl;

    public static Customer modifyCustomer;
    public static int modifyCustomerIndex;
    public static Appointment selectedAppointment;
    public static int modifyAppointmentIndex;
    private final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("M/d/yy h:mm a");

    public void menuButton(ActionEvent event) throws IOException
    {
        Parent mainMenu = FXMLLoader.load(getClass().getResource("/View/menu.fxml"));
        Scene mainScene = new Scene(mainMenu);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }
    /** Change Screen to add a new Customer.
     *
     * @param event Switch Screen
     * @throws IOException
     */
    public void addCustomerButtonPushed(ActionEvent event) throws IOException
    {

        Parent addCustomerParent = FXMLLoader.load(getClass().getResource("/View/AddCustomer.fxml"));
        Scene mainScene = new Scene(addCustomerParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }

    /** Change Screen to Edit customer.
     *
     * @param event Switch Screen
     * @throws IOException
     */
    public void modifyCustomerButtonPushed(ActionEvent event) throws IOException {

        if (customersTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No customer selected!");
            alert.setContentText("Please ensure a customer is selected to be modified.");
            alert.showAndWait();
        } else {
            modifyCustomer = customersTableView.getSelectionModel().getSelectedItem();
            modifyCustomerIndex = Schedule.getAllCustomers().indexOf(modifyCustomer);
            Parent modifyCustomerParent = FXMLLoader.load(getClass().getResource("/View/EditCustomer.fxml"));
            Scene mainScene = new Scene(modifyCustomerParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        }

    }

    /** Method to delete customer.
     *
     * @param event
     */
    public void deleteCustomerButtonPushed(ActionEvent event) throws SQLException {
        if (customersTableView.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No customer selected!");
            alert.setContentText("Please ensure a customer is selected to be deleted.");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            String message = String.format("You want to delete %s from the Customer list?", customersTableView.getSelectionModel().getSelectedItem().getCustomerName());
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Confirm Delete");
            alert.setContentText(message);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK)
            {
                Customer c = customersTableView.getSelectionModel().getSelectedItem();
                Schedule.deleteCustomer(c);
                customersTableView.setItems(Schedule.getAllCustomers());


            }
            appmntTableView.setItems(Schedule.getAllAppointments());
        }

    }

    /** Method for changing screen to add appointment.
     *
     * @param event Switch screen to add appointment.
     * @throws IOException
     */
    public void addAppButtonPushed(ActionEvent event) throws IOException {
        Parent addAppParent = FXMLLoader.load(getClass().getResource("/View/AddAppointment.fxml"));
        Scene mainScene = new Scene(addAppParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();
    }

    /** Method for editing an appointment.
     *
     * @param event Switch screen to edit appointment.
     * @throws IOException
     */
    public void modAppButtonPushed(ActionEvent event) throws IOException {
        if (appmntTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No appointment selected!");
            alert.setContentText("Please ensure a appointment is selected to be modified.");
            alert.showAndWait();
        } else {
            selectedAppointment = appmntTableView.getSelectionModel().getSelectedItem();
            modifyAppointmentIndex = Schedule.getAllAppointments().indexOf(selectedAppointment);

            //change scene
            Parent modAppParent = FXMLLoader.load(getClass().getResource("/View/EditAppointment.fxml"));
            Scene modAppScene = new Scene(modAppParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(modAppScene);
            window.show();
        }
    }

    /** Method for deleting an appointment.
     *
     * @param event
     */
    public void deleteAppmntButtonPushed(ActionEvent event) {

        if (appmntTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No appointment selected!");
            alert.setContentText("Please ensure an appointment is selected to be deleted.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            String message = String.format("You want to delete %s Appointment with ID of %s.",
                    appmntTableView.getSelectionModel().getSelectedItem().getAppointmentType(),
                    appmntTableView.getSelectionModel().getSelectedItem().getAppointmentID());
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Confirm Delete");
            alert.setContentText(message);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //delete appointment
                Appointment a = appmntTableView.getSelectionModel().getSelectedItem();
                Schedule.deleteAppointment(a);
                if (appmntToggleGroup.getSelectedToggle().equals(this.allRadioButton)) {
                    appmntTableView.setItems(Schedule.getAllAppointments());
                }
                if (appmntToggleGroup.getSelectedToggle().equals(this.monthlyRadioButton)) {
                    appmntTableView.setItems(Schedule.getMonthlyAppointments());
                }
                if (appmntToggleGroup.getSelectedToggle().equals(this.weeklyRadioButton)) {
                    appmntTableView.setItems(Schedule.getWeeklyAppointments());
                }

            }
        }

    }

    /** Method to switch screen to reports menu.
     *
     * @param event
     * @throws IOException
     */
    public void reportsButtonPushed(ActionEvent event) throws IOException {
        //change scene
        Parent modAppParent = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
        Scene reportScene = new Scene(modAppParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(reportScene);
        window.show();
    }


    /** Method for exiting the program.
     *
     * @param event
     * @throws IOException
     */
    public void exitButtonPushed(ActionEvent event) throws IOException {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * This method will update appointment TableView whenever a different
     * radio button is selected.
     */
    public void radioButtonChanged() {
        if (appmntToggleGroup.getSelectedToggle().equals(this.allRadioButton)) {
            //change TableView data to all appointments
            populateAllAppointmentTable();
        }
        if (appmntToggleGroup.getSelectedToggle().equals(this.monthlyRadioButton)) {
            //change TableView data to monthly appointments
            populateMonthlyAppointmentTable();
        }
        if (appmntToggleGroup.getSelectedToggle().equals(this.weeklyRadioButton)) {
            //change TableView data to weekly appointments
            populateWeeklyAppointmentTable();
        }
    }

    /** Method for populating the customers table.
     * Lambda Expression to populate table easily.
     */
    public void populateCustomerTable() {
        ObservableList<Customer> allCustomerList = Schedule.getAllCustomers();
        IDTbl.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custDivisionIDTbl.setCellValueFactory(new PropertyValueFactory<>("FLDID"));
        customersTableView.setItems(allCustomerList);
        //this table method uses Lambda expressions to conveniently populate table columns
        custNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        custAddressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerAddress()));
        custphoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPhoneNum()));
        custPostalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPostalCode()));
        custCountryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerCountry()));
        custDivisionIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerCity()));
        customersTableView.setItems(Schedule.getAllCustomers());
    }

    /** Method for populating all appointments table.
     * Lambda Expression to populate table easily.
     */
    public void populateAllAppointmentTable() {

        ObservableList<Appointment> allAppointmentsList = Schedule.getAllAppointments();
        appointmentIDtbl.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        userIDTbl.setCellValueFactory(new PropertyValueFactory<>("userID"));
        customerIDTbl.setCellValueFactory(new PropertyValueFactory<>("appCustomerID"));
        contactIDTbl.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appmntTableView.setItems(allAppointmentsList);
        //this table method uses Lambda expressions to conveniently populate table columns
        titleTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentTitle()));
        typeTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentType()));
        customerTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentCustomer()));
        //userTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentConsultant()));
        descriptionTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppmntDescription()));
        contactTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContact()));
        locationTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppmntLocation()));

        startTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDateTime)));
        endTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDateTime)));
        appmntTableView.setItems(Schedule.getAllAppointments());


    }

    /** Method for populating the table after month tab is selected.
     * Lambda Expression to populate table easily.
     */
    public void populateMonthlyAppointmentTable() {
        ObservableList<Appointment> allAppointmentsList = Schedule.getAllAppointments();
        appointmentIDtbl.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        userIDTbl.setCellValueFactory(new PropertyValueFactory<>("userID"));
        customerIDTbl.setCellValueFactory(new PropertyValueFactory<>("appCustomerID"));
        contactIDTbl.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appmntTableView.setItems(allAppointmentsList);
        //this table method uses Lambda expressions to conveniently populate table columns
        titleTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentTitle()));
        typeTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentType()));
        customerTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentCustomer()));
        //userTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentConsultant()));

        startTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDateTime)));
        endTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDateTime)));
        appmntTableView.setItems(Schedule.getMonthlyAppointments());
    }

    /** Method for populating the table after the week tab is selected.
     * Lambda Expression to populate table easily.
     */
    public void populateWeeklyAppointmentTable() {

        ObservableList<Appointment> allAppointmentsList = Schedule.getAllAppointments();
        appointmentIDtbl.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        userIDTbl.setCellValueFactory(new PropertyValueFactory<>("userID"));
        customerIDTbl.setCellValueFactory(new PropertyValueFactory<>("appCustomerID"));
        contactIDTbl.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appmntTableView.setItems(allAppointmentsList);
        //this table method uses Lambda expressions to conveniently populate table columns
        titleTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentTitle()));
        typeTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentType()));
        customerTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentCustomer()));
        //userTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentConsultant()));
        startTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDateTime)));
        endTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDateTime)));
        appmntTableView.setItems(Schedule.getWeeklyAppointments());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appmntToggleGroup = new ToggleGroup();
        this.weeklyRadioButton.setToggleGroup(appmntToggleGroup);
        this.monthlyRadioButton.setToggleGroup(appmntToggleGroup);
        this.allRadioButton.setToggleGroup(appmntToggleGroup);

        //this sets default radio button
        allRadioButton.setSelected(true);
        //populate tables
        populateAllAppointmentTable();
        populateCustomerTable();

    }
}
