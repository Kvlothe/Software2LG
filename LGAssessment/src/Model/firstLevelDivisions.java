package Model;

/** Model Class for First Level Divisions.
 *
 * @author Ladd Gillies
 */
public class firstLevelDivisions
{
    private int divisionID;
    private String divisionName;
    public int countryID;

    /** Constructor
     *
     * @param divisionID Get Division ID.
     * @param divisionName Get Division Name.
     * @param countryID Get Country ID.
     */
    public firstLevelDivisions(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /*
    Getters and Setters for First Level Divisions.
     */
    public int getDivisionID() {
        return divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public int getCountryID() {
        return countryID;
    }
}

