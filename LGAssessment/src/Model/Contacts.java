package Model;

/** Model Class for Contacts.
 * @author Ladd Gillies
 */
public class Contacts
{
    private String email;
    private Integer contactID;
    private String contactName;

    public Contacts(String email, Integer contactID, String contactName)
    {
        this.email = email;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    public String getEmail()
    {
        return email;
    }

    public Integer getContactID()
    {
        return contactID;
    }

    public String getContactName() {
        return contactName;
    }
}