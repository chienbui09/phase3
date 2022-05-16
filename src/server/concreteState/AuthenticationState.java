package server.concreteState;

import model.Message;
import model.Type;
import model.User;
import state.*;

import java.io.ObjectOutputStream;

public class AuthenticationState implements UserState {

    private User user;
    public AuthenticationState(User user){
        this.user = user;
    }
    @Override
    public boolean wake(ObjectOutputStream outputStream) throws Exception {
        System.out.println("not need! client is waked");
        outputStream.writeObject(new Message(Type.WAKE, "not available"));
        return false;
    }

    @Override
    public boolean register(ObjectOutputStream outputStream) throws Exception {

        return false;
    }

    @Override
    public boolean login(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeObject(new Message(Type.WAKE, "not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public String echo(ObjectOutputStream outputStream, String message) throws Exception {
        return handleMsgEcho(message);
//        outputStream.writeObject();
    }

    @Override
    public boolean broadcast(ObjectOutputStream outputStream) throws Exception {
        return true;
    }

    @Override
    public boolean logout(ObjectOutputStream outputStream) throws Exception {
        this.user.setUserState(user.getSleepState());
        outputStream.writeObject(new Message(Type.LOGOUT,"loged out"));
        outputStream.flush();
        return true;
    }

    @Override
    public boolean sleep(ObjectOutputStream outputStream) throws Exception {
        user.setUserState(user.getSleepState());
        System.out.println("change to Sleep state");
        outputStream.writeObject(new Message(Type.SLEEP, "changed to Sleep state"));
        outputStream.flush();
        return false;
    }

    public String handleMsgEcho(String msgToHandle){

        msgToHandle = msgToHandle.trim().toLowerCase();
        //remove redundant space
        msgToHandle = msgToHandle.replaceAll("\\s+","");

        //remove special character
        String regex = "[ ](?=[ ])|[^-_,A-Za-z0-9 ]+";
        msgToHandle = msgToHandle.replaceAll(regex,"").toUpperCase();

        String firstLet = msgToHandle.substring(0,1);
        String rem = msgToHandle.substring(1);
        msgToHandle = firstLet.concat(rem);
        return msgToHandle;
    }

    @Override
    public String toString() {
        return "Authentication state";
    }
}
