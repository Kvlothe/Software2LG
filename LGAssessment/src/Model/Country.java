package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import Utilities.Query;

/** Model Class for Country.
 *
 * @author Ladd Gillies Jr
 */
public class Country
{
    private int countryId;
    private String country;
    private String countryName;


    /** Constructor.
     *
     * @param countryID Get CountryID.
     * @param countryName Get Country Name.
     */
    public Country(int countryID, String countryName)
    {
        this.countryId = countryID;
        this.countryName = countryName;
    }

    public Country() {

    }

    /** Getters and Setters for Country.
     *
     * @return Country ID or Country Name.
     */
    public int getCountryId() { return countryId; }

    public String getCountryName() { return countryName; }

    public void setCountryId(int countryId) { this.countryId = countryId; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }
}
