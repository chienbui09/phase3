package model;

import java.io.Serializable;

public class Message implements Serializable {

    private Type msgType;
    private User user;
    String message;

    // user have flags which mark the statement of message handled sucessfully or not
    private boolean connected;
    private boolean registed;
    private boolean logedin;

    //constructor
    public Message(){
        this.user = new User();
        this.connected = false;
        this.logedin = false;
        this.registed = false;
    }
    public Message (Type msgType, User user){
        this.user = user;
        this.msgType = msgType;
    }
    public Message(Type msgType, String message){
        this.msgType = msgType;
        this.message = message;
    }

    //Getter & Setter

    public Type getMsgType() {
        return msgType;
    }

    public void setMsgType(Type msgType) {
        this.msgType = msgType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
