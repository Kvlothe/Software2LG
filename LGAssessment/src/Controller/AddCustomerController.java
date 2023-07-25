package Controller;

import Access.countryAccess;
import Access.contactAccess;
import Access.firstLevelDivisionAccess;
import Model.*;
import Utilities.InvalidCustomerException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Utilities.DBConnection;
import Utilities.Query;

/**
 * Class for Add Customer Controller.
 *
 * @author Ladd Gillies
 */
public class AddCustomerController implements Initializable {

    @FXML TextField custNameTextField;
    @FXML TextField custAddressTextField;
    @FXML TextField custPostalTextField;
    @FXML TextField custPhoneNumberTextField;
    @FXML private ComboBox<String> countryDrop;
    @FXML private ComboBox<String> firstLevelDivisionDrop;

    //private Address customerAddress = new Address();
    //private Customer division = new Customer();
    //private Customer customerCountry = new Customer();
    //private String customerCountry;

    /** Button for Saving a New Customer.
     *
     * @param event Switch Screen back to Main menu
     * @throws IOException
     */
    public void saveCustomerButtonPushed(ActionEvent event) throws IOException, SQLException {

        String customerName = custNameTextField.getText();
        String country = countryDrop.getValue();
        String address = custAddressTextField.getText();
        String division = firstLevelDivisionDrop.getValue();
        String postalCode = custPostalTextField.getText();
        String phoneNumber = custPhoneNumberTextField.getText();
        String loggedInUser = DBConnection.loggedInUser.getUsername();
        int FLDID = Integer.parseInt(firstLevelDivisionAccess.findFLDID(division));

        try{
            if(validateInput(customerName, country, address, division, postalCode, phoneNumber)){

                int newID = Schedule.makeCustomerID();
                //insert into customer
                Query.makeQuery("INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)\n" +
                        "VALUES ('"+newID+"', '" + customerName + "', '" + address + "', '"+postalCode+"','"+phoneNumber+"', NOW(), '" + loggedInUser + "',NOW(),  '" + loggedInUser + "', '"+FLDID+"')");

                //add new customer and update customer list
                Customer newCustomer = new Customer();
                newCustomer.setCustomerID(newID);
                newCustomer.setCustomerName(customerName);
                newCustomer.setCustomerAddress(address);
                newCustomer.setCustomerPhone(phoneNumber);
                newCustomer.setCustomerCountry(country);
                newCustomer.setCustomerCity(division);
                newCustomer.setCustomerPostalCode(postalCode);
                newCustomer.setFLDID(FLDID);
                Schedule.addCustomer(newCustomer);

                //change scene to main
                Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/MainMenu.fxml"));
                Scene mainScene = new Scene(mainViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(mainScene);
                window.show();
            }

        } catch(InvalidCustomerException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Missing information!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /** Method for canceling action and returning to main screen.
     *
     * @param event
     * @throws IOException
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancelation");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Information not saved will be lost!");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            //change scene to main
            Parent mainViewParent = FXMLLoader.load(getClass().getResource("/View/MainMenu.fxml"));
            //create new scene object
            Scene mainScene = new Scene(mainViewParent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        }
    }

    /** Method for validating the input to make sure values are entered.
     *
     * @param name Get name.
     * @param country Get Country.
     * @param address Get address.
     * @param city Get city/providence.
     * @param postal Get postal code.
     * @param phone Get phone number.
     * @return Returns Boolean Value.
     * @throws InvalidCustomerException
     */
    public boolean validateInput(String name, String country, String address, String city, String postal, String phone) throws InvalidCustomerException{

        if(name.equals("")){
            throw new InvalidCustomerException("Please enter customer's name");
        }
        if(country.equals("")){
            throw new InvalidCustomerException("Please enter a country");
        }
        if(address.equals("")){
            throw new InvalidCustomerException("Please enter a customer's address");
        }
        if(city.equals("")){
            throw new InvalidCustomerException("Please enter a customer's city");
        }
        if(postal.equals("")){
            throw new InvalidCustomerException("Please enter a customer's postal code");
        }
        if(phone.equals("")){
            throw new InvalidCustomerException("Please enter a customer's phone number");
        }
        return true;
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
            ObservableList<countryAccess> allCountries = countryAccess.getCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<String> firstLevelDivisionNames = FXCollections.observableArrayList();
            ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
            ObservableList<String> allContactsNames = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionAccess> allFirstLevelDivisions = firstLevelDivisionAccess.getAllFirstLevelDivisions();

            contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));

            allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
            countryDrop.setItems(countryNames);
            allFirstLevelDivisions.forEach(firstLevelDivision -> firstLevelDivisionNames.add(firstLevelDivision.getDivisionName()));
            firstLevelDivisionDrop.setItems(firstLevelDivisionNames);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Method for Combo boxes for country and state/providence.
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    void FLDCombo(ActionEvent event) throws SQLException
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


