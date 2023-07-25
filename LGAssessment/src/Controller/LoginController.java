package Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import Model.Appointment;
import Model.Schedule;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Utilities.DBConnection;

/** Class for the Login Controller
 *
 * @author Ladd Gillies
 */
public class LoginController implements Initializable {

    @FXML private Label LocationLabel;
    @FXML private Label loginLabel;
    @FXML private Label PasswordLabel;
    @FXML private Label usernameLabel;
    @FXML private Button exitButton;
    @FXML private Button loginButton;
    @FXML private TextField locationTxt;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField userPasswordField;

    private String errorTitle;
    private String errorHeader;
    private String errorMessage;

    private static Locale userLocale;
    private static ZoneId userTimeZone;

    private final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("M/d/yy h:mm a");

    /**
     * Validates user's log in credentials and
     * changes scene to main application screen.
     */
    public void loginButtonPushed(ActionEvent event) throws IOException {


        String username = usernameTextField.getText();
        String password = userPasswordField.getText();
        boolean checkedUser = DBConnection.validateLogin(username, password);
        if (checkedUser) {
            Schedule.populateCustomersList();
            Schedule.populateWeeklyAppointmentsList();
            Schedule.populateMonthlyAppointmentsList();
            Schedule.populateAllAppointmentsList();
            userLocale = Locale.getDefault();
            userTimeZone = ZoneId.systemDefault();


            Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
            Scene mainScene = new Scene(mainViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();

            Appointment upcomingAppointment = Schedule.checkUpcomingAppointment();
            if (upcomingAppointment != null) {
                String message = String.format("You have a %s appointment with appointment ID of %s with %s scheduled at %s",
                        upcomingAppointment.getAppointmentType(),
                        upcomingAppointment.getAppmntID(),
                        upcomingAppointment.getAppointmentCustomer(),
                        upcomingAppointment.getStart().format(formatDateTime));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Reminder");
                alert.setHeaderText("Appointment Within 15 Minutes");
                alert.setContentText(message);
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reminder");
                alert.setHeaderText("No Upcoming Appointments");
                alert.setContentText("No Upcoming Appointments");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorTitle);
            alert.setHeaderText(errorHeader);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
    }

    /** Method for exiting the program.
     *
     * @param event Close connection and program.
     */
    @FXML void onActionExit(ActionEvent event)
    {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try
        {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);

            // Testing to make sure French language works.
            //Locale locales = new Locale("fr", "FR");
            //Locale.setDefault(locales);

            ZoneId zone = ZoneId.systemDefault();
            locationTxt.setText(String.valueOf(zone));
            /**
             *  For language check.
             */

            rb = ResourceBundle.getBundle("Utilities/Login", locale);
            loginLabel.setText(rb.getString("login"));
            usernameLabel.setText(rb.getString("userName"));
            PasswordLabel.setText(rb.getString("password"));
            loginButton.setText(rb.getString("login"));
            exitButton.setText(rb.getString("exit"));
            LocationLabel.setText(rb.getString("location"));
            errorMessage = rb.getString("errorMessage");
            errorHeader = rb.getString("errorHeader");
            errorTitle = rb.getString("errorTitle");
        }
        catch(MissingResourceException e)
        {
            System.out.println("Resource File Missing! " + e);
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static Locale getUserLocale() {
        return userLocale;
    }

    public static void setUserLocale(Locale userLocale) {
        LoginController.userLocale = userLocale;
    }

    public static ZoneId getUserTimeZone() {
        return userTimeZone;
    }

    public static void setUserTimeZone(ZoneId userTimeZone) {
        LoginController.userTimeZone = userTimeZone;
    }
}
