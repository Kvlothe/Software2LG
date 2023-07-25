/** Un-used menu controller I never spent the time to fully implement.
 *
 */

package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Menu {
    @FXML private Button ExitButton;
    @FXML private Button AppointmentsButton;
    @FXML private Button ReportsButton;
    @FXML private Button CustomerButton;

    @FXML
    void AppointmentsButton(ActionEvent event) throws IOException
    {
        Parent viewAppointments = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
        Scene mainScene = new Scene(viewAppointments);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();

    }

    @FXML
    void CustomerButton(ActionEvent event) throws IOException
    {
        Parent customer = FXMLLoader.load(getClass().getResource("/View/ViewCustomer.fxml"));
        Scene reportScene = new Scene(customer);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(reportScene);
        window.show();

    }

    @FXML
    void ExitButton(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();

    }

    @FXML
    void ReportsButton(ActionEvent event) throws IOException
    {
        Parent reports = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
        Scene reportScene = new Scene(reports);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(reportScene);
        window.show();

    }

}
