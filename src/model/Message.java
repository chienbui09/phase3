package model;

import java.io.Serializable;

public class Message implements Serializable {

    private Type msgType;
    private User user;
    private HotType topic;
    String message;

    // user have flags which mark the statement of message handled sucessfully or not

    //constructor
    public Message(){
        this.user = new User();
        this.msgType = null;
        this.message = "";
        this.topic = null;
    }
    public Message (Type msgType, User user){
        this.user = user;
        this.msgType = msgType;
    }
    public Message(Type msgType, String message){
        this.msgType = msgType;
        this.message = message;
    }

    public Message(Type msgType, HotType topic) {
        this.msgType = msgType;
        this.topic = topic;
    }

    public Message(Type msgType, HotType topic, String message) {
        this.msgType = msgType;
        this.topic = topic;
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

    public HotType getTopic() {
        return topic;
    }

    public void setTopic(HotType topic) {
        this.topic = topic;
    }
}
