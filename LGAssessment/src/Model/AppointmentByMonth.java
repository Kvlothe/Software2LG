package Model;

/** Model Class for Appointments by Month.
 * @author Ladd Gillies
 */
public class AppointmentByMonth
{
    private String month;
    private String type;
    private String amount;

    /** Setters and Getters.
     *
     * @return Returns Month, Type and Amount.
     */
    public String getMonth() { return month; }

    public void setMonth(String month) { this.month = month; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getAmount() { return amount;}

    public void setAmount(String amount) { this.amount = amount; }
}
