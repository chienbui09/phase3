package model;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

import server.concreteState.AuthenticationState;
import server.concreteState.IdleState;
import server.concreteState.SleepState;
import state.*;
public class User implements Serializable {
    private String userName;
    private String password;

    // declare some state of user state
    private UserState idleState;
    private UserState sleepState;
    private UserState authenticationState;
    private UserState userState;

    // constructor
    public User(){
        this.userName = new String();
        this.password = new String();
        this.idleState = new IdleState(this);
        this.sleepState = new SleepState(this);
        this.authenticationState = new AuthenticationState(this);
        userState = idleState;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.idleState = new IdleState(this);
        this.sleepState = new SleepState(this);
        this.authenticationState = new AuthenticationState(this);
        userState = idleState;
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

    public UserState getIdleState() {
        return idleState;
    }

    public void setIdleState(UserState idleState) {
        this.idleState = idleState;
    }

    public UserState getSleepState() {
        return sleepState;
    }

    public void setSleepState(UserState sleepState) {
        this.sleepState = sleepState;
    }

    public UserState getAuthenticationState() {
        return authenticationState;
    }

    public void setAuthenticationState(UserState authenticationState) {
        this.authenticationState = authenticationState;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public void wake(ObjectOutputStream outputStream) throws Exception {
        this.userState.wake(outputStream);
    }
    public void sleep(ObjectOutputStream outputStream) throws Exception {
        this.userState.sleep(outputStream);
    }
    public boolean login(ObjectOutputStream outputStream) throws Exception {
        return this.userState.login(outputStream);
    }
    public boolean logout(ObjectOutputStream outputStream) throws Exception {
        return this.userState.logout(outputStream);
    }
    public boolean register(ObjectOutputStream outputStream) throws Exception {
        return this.userState.register(outputStream);
    }
    public void echo(ObjectOutputStream outputStream, String message) throws Exception {
        userState.echo(outputStream, message);
    }
    public boolean broadcast(ObjectOutputStream outputStream) throws Exception {
        return this.userState.broadcast(outputStream);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("User:\t").append(userName);
        result.append("\nState:\t").append(userState).append("\n");
        return result.toString();
    }

    public void input(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        this.userName = scanner.nextLine();
        System.out.println("Enter password: ");
        this.password = scanner.nextLine();
    }

}

