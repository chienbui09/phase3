package model;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String password;

    // constructor
    public User(){
        this.userName = new String();
        this.password = new String();
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // getter & setter

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
