package server.concreteState;

import model.Message;
import model.Type;
import model.User;
import state.UserState;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SleepState implements UserState, Serializable {

    private User user;

    public SleepState(User user) {
        this.user = user;
    }

    @Override
    public boolean wake(ObjectOutputStream outputStream) throws Exception {
        System.out.println("waking");
        outputStream.writeObject(new Message(Type.WAKE,"waked up"));
        outputStream.flush();
        return true;
    }

    @Override
    public boolean register(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in sleep state");
        outputStream.writeObject(new Message(Type.SLEEP,"not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public boolean login(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in sleep state");
        outputStream.writeObject(new Message(Type.LOGIN,"not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public void echo(ObjectOutputStream outputStream, String message) throws Exception {
        System.out.println("user is in sleep state");
        outputStream.writeObject(new Message(Type.ECHO,"not available"));
        outputStream.flush();
    }

    @Override
    public boolean broadcast(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in sleep state");
        outputStream.writeObject(new Message(Type.BROADCAST,"not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public boolean logout(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in sleep state");
        outputStream.writeObject(new Message(Type.LOGOUT,"not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public boolean sleep(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user was in sleep state");
        outputStream.writeObject(new Message(Type.SLEEP,"not available"));
        outputStream.flush();
        return false;
    }

    @Override
    public String toString() {
        return "Sleep State";
    }
}
