// Un-used controller class for viewing customer tables intended for use with menu.

package Controller;

import Model.Customer;
import Model.Schedule;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/** Controller class for viewing customers with buttons for adding and editing.
 *
 * @author Ladd Gillies Jr
 */

public class ViewCustomerController
{
    @FXML
    private TableColumn<Customer, String> addressTbl;

    @FXML
    private TableColumn<Customer, Integer> customerIDTbl;

    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<Customer, Integer> divisionIDTbl;

    @FXML
    private TableColumn<Customer, String> divisionTbl;

    @FXML
    private TableColumn<Customer, String> nameTbl;

    @FXML
    private TableColumn<Customer, String> phoneNumberTbl;

    @FXML
    private TableColumn<Customer, String> postalCodeTbl;
    public static Customer modifyCustomer;
    public static int modifyCustomerIndex;

    @FXML
    void addCustomerButton(ActionEvent event) throws IOException
    {
        Parent addCustomerParent = FXMLLoader.load(getClass().getResource("/View/AddCustomer.fxml"));
        Scene mainScene = new Scene(addCustomerParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();

    }

    @FXML
    public void deleteCustomerButton(javafx.event.ActionEvent event) throws SQLException {
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
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Confirm Delete");
            alert.setContentText("Are you sure you want to delete this customer?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK)
            {
                Customer c = customersTableView.getSelectionModel().getSelectedItem();
                Schedule.deleteCustomer(c);
                customersTableView.setItems(Schedule.getAllCustomers());
            }
        }

    }

    @FXML
    void EditCustomerButton(ActionEvent event) throws IOException {
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
        //@Override

        public void initialize()
        {
            try {
                customerIDTbl.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
                nameTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
                addressTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerAddress()));
                postalCodeTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPostalCode()));
                phoneNumberTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPhoneNum()));
                divisionTbl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerCountry()));
                divisionIDTbl.setCellValueFactory(new PropertyValueFactory<>("Division_ID"));
                customersTableView.setItems(Schedule.getAllCustomers());
                //populateCustomerTable();
            }catch(Exception e){e.printStackTrace();
        }


    }

    //this table method uses Lambda expressions to conveniently populate table columns
    //public void populateCustomerTable() {
        //
    //}
 }
