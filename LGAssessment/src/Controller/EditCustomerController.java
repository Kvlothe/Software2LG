package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Access.countryAccess;
import Access.firstLevelDivisionAccess;
import Model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static Controller.MainController.modifyCustomer;
import static Controller.MainController.modifyCustomerIndex;
import static Model.Schedule.updateCustomer;
import Model.Customer;
import Utilities.DBConnection;
import Utilities.Query;

/**
 * FXML Controller class
 *
 * @author Ladd Gillies
 */
public class EditCustomerController implements Initializable {

    @FXML
    TextField custNameTextField;
    @FXML
    TextField custCountryTextField;
    @FXML
    TextField custAddressTextField;
    @FXML
    TextField custCityTextField;
    @FXML
    TextField custPostalTextField;
    @FXML
    TextField custPhoneNumberTextField;
    @FXML
    private ComboBox<String> countryDrop;
    @FXML
    private ComboBox<String> firstLevelDivisionDrop;

    /**
     * This saves updated customer to DB, updates allCustomersList
     * and returns to main scene
     */
    public void saveButtonPushed(ActionEvent event) throws IOException, SQLException {

        String customerName = custNameTextField.getText();
        String country = countryDrop.getValue();
        String address = custAddressTextField.getText();
        String division = firstLevelDivisionDrop.getValue();
        int divisionId = Integer.parseInt(firstLevelDivisionAccess.findFLDID(division));
        String postalCode = custPostalTextField.getText();
        String phoneNumber = custPhoneNumberTextField.getText();
        String loggedInUser = DBConnection.loggedInUser.getUsername();
        int customerId = modifyCustomer.getCustomerID();

        // Make query to update customer.
        Query.makeQuery("UPDATE customers SET Customer_Name = '" + customerName + "', Address = '"+address+ "', Phone = "+phoneNumber+ ", Postal_Code = '"+postalCode+"', Last_Update = NOW(), Last_Updated_By = '" + loggedInUser + "', Created_By = '" +loggedInUser+ "', Division_ID = '"+divisionId+  "', Create_Date = NOW() WHERE Customer_ID = " + customerId + "");

        //create new customer and set values
        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerID(customerId);
        updatedCustomer.setCustomerName(customerName);
        updatedCustomer.setCustomerAddress(address);
        updatedCustomer.setCustomerPhone(phoneNumber);
        updatedCustomer.setCustomerCountry(country);
        updatedCustomer.setFLDID(divisionId);
        updatedCustomer.setCustomerCity(division);
        updatedCustomer.setCustomerPostalCode(postalCode);
        //update customerList
        updateCustomer(modifyCustomerIndex, updatedCustomer);

        //change scene to main
        Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/mainMenu.fxml"));
        Scene mainScene = new Scene(mainViewParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.show();

    }

    /** Method for canceling action and returning to main menu.
     *
     * @param event Returns you to main menu.
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

        custNameTextField.setText(modifyCustomer.getCustomerName());
        countryDrop.setValue(modifyCustomer.getCustomerCountry());
        custAddressTextField.setText(modifyCustomer.getCustomerAddress());
        firstLevelDivisionDrop.setValue(modifyCustomer.getCustomerCity());
        custPostalTextField.setText(modifyCustomer.getCustomerPostalCode());
        custPhoneNumberTextField.setText(modifyCustomer.getCustomerPhoneNum());
        try {
            ObservableList<countryAccess> allCountries = countryAccess.getCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<String> firstLevelDivisionNames = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionAccess> allFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();
            allFirstLevelDivisions.forEach(firstLevelDivision -> firstLevelDivisionNames.add(firstLevelDivision.getDivisionName()));
            firstLevelDivisionDrop.setItems(firstLevelDivisionNames);
            allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
            countryDrop.setItems(countryNames);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Method for the combo boxes for both country and state/providence.
     *
     * @param event
     * @throws SQLException
     */
    @FXML void FLDCombo(ActionEvent event) throws SQLException
    {
        try
        {
            String selectedCountry = countryDrop.getSelectionModel().getSelectedItem();
            ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();
            ObservableList<String> USDivision = FXCollections.observableArrayList();
            ObservableList<String> UKDivision = FXCollections.observableArrayList();
            ObservableList<String> CDivision = FXCollections.observableArrayList();

            getAllFirstLevelDivisions.forEach(firstLevelDivision ->
            {
                if(firstLevelDivision.getCountryID() == 1)
                {
                    USDivision.add(firstLevelDivision.getDivisionName());
                }
                else if(firstLevelDivision.getCountryID() == 2)
                {
                    UKDivision.add(firstLevelDivision.getDivisionName());
                }
                else if(firstLevelDivision.getCountryID() == 3)
                {
                    CDivision.add(firstLevelDivision.getDivisionName());
                }
            });
            if(selectedCountry.equals("U.S"))
            {
                firstLevelDivisionDrop.setItems(USDivision);
            }
            else if(selectedCountry.equals("UK"))
            {
                firstLevelDivisionDrop.setItems(UKDivision);
            }
            else if(selectedCountry.equals("Canada"))
            {
                firstLevelDivisionDrop.setItems(CDivision);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
