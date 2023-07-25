package Model;

/** Model Class for User.
 *
 * @author Ladd Gillies
 */
public class User {

    private Integer userID;
    private String username;

/*
Getters and Setter for User.
 */
    public Integer getuserID(){
        return userID;
    }
    public void setuserID(Integer id){
        userID = id;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
}