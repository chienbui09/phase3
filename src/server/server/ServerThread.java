package server.server;

import model.HotType;
import model.Message;
import model.Type;
import model.User;
import server.concreteState.AuthenticationState;
import server.concreteState.IdleState;
import server.observer.Listener;
import server.publisher.ConcreteSubject;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerThread implements Runnable, Listener {
//    private static final ArrayList<ServerThread> instances = new ArrayList<>();
    private static final ArrayList<ServerThread> totalConnected = new ArrayList<>();
    private static final ArrayList<ServerThread> totalAuthenticated = new ArrayList<>();
    private static final ArrayList<ServerThread> totalAuthenticatedNotSleeping = new ArrayList<>();
    private static final ArrayList<ServerThread> totalSleeping = new ArrayList<>();
    private static final ArrayList<ServerThread> totalIdle = new ArrayList<>();
    private final Socket clientSocket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private User user;
    private ConcreteSubject subject;

    public ServerThread(Socket accept, ConcreteSubject subject) throws IOException {
        this.clientSocket = accept;
        this.user = new User();
        this.input = new ObjectInputStream(accept.getInputStream());
        this.output = new ObjectOutputStream(accept.getOutputStream());
        this.subject = subject;
        addInstanceTotalConnected();
        addInstanceTotalIdle();
    }
//    private synchronized boolean registerObserver(HotTopic hotTopic) {
//        return concreteSubject.registerObserver(hotTopic, this);
//    }

//    private static  String handleEcho(String msgToHandle){
//        String regex = "[ ](?=[ ])|[^-_,A-Za-z0-9 ]+";
//        msgToHandle = msgToHandle.replaceAll(regex,"");
//        return msgToHandle;
//    }

    public synchronized void update(String message){
    try{
        if (user.getUserState() instanceof AuthenticationState){
            message = "\n----New message from your subscribed topic----\nContent: " + message;
            this.output.writeObject(new Message(Type.HOT, message));
            this.output.flush();
        } else {
            // do nothing
        }
    }catch (IOException e){
        e.printStackTrace();
    }
    }

    private synchronized boolean addSubscriber(HotType topic){
        return subject.addSubscriber(topic, this);
    }
    private synchronized void addInstance(){
        totalAuthenticated.add(this);
    }
    private synchronized void removeInstance(){
        totalAuthenticated.remove(this);
    }
    private synchronized void addInstanceTotalConnected(){
            totalConnected.add(this);
    }
    private synchronized void removeInstanceTotalConnected(){
            totalConnected.remove(this);
    }
    private synchronized void addInstanceTotalIdle(){
        totalIdle.add(this);
    }
    private synchronized void removeInstanceTotalIdle(){
        totalIdle.remove(this);
    }
    private synchronized void addInstanceSleeping(){
        totalSleeping.add(this);
    }
    private synchronized void removeInstanceSleeping(){
        totalSleeping.remove(this);
    }
    private synchronized void addInstanceAuthenNotSleeping(){
        totalAuthenticatedNotSleeping.add(this);
    }
    private synchronized void removeInstanceAuthenNotSleeping(){
        totalAuthenticatedNotSleeping.remove(this);
    }
    private synchronized void printConnectedClient(){
        int counter =0;
        for (ServerThread ignore : totalConnected){
            counter++;
        }
        if(counter == 1){
            System.out.println("There is " + counter + " client connected");
        } else {
            System.out.println("There are " + counter + " client connected");
        }
    }
     /*
    This method is used to send broadcast message to all connected clients, if the client is sleeping,
    it will not receive the message.
     */
    public void broadcastMessage(String msgToSend) throws IOException{
        int counter = 0;
        for(ServerThread client : totalAuthenticatedNotSleeping){
            if(!client.equals(this)){
                try{
                    counter++;
                    msgToSend = "\n\nBroadcast from: " + this.user.getUserName() + " | Content: " + msgToSend + "\n";
                    client.output.writeObject(new Message(Type.BROADCAST, msgToSend));
                    client.output.flush();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Broadcast to " + counter + " clients.");
        output.writeObject(new Message(Type.BROADCAST, "Broadcast to " + counter + " active clients successful!."));
        output.flush();
    }
    public void broadcastToHotTopic(){

    }

    public void printTopic(ObjectOutputStream outputStream) throws IOException {
        StringBuffer topic = new StringBuffer();
        topic.append("----Topic----\n");
        topic.append("1: GRADUATED\n");
        topic.append("2: NOT GRADUATED");
        outputStream.writeObject(new Message(Type.SUBSCRIBE, String.valueOf(topic)));
        outputStream.flush();
    }

    @Override
    public void run() {
        printConnectedClient();
        try
        {
            Message message;

            while (true){
                message = (Message) input.readObject();
                Type action = message.getMsgType();
                System.out.println("Client option: " + action +
                        "| State: " + this.user.getUserState());

                // if client want to exit, remove client.
                if(action == Type.EXIT){
                    System.out.println("client: " + clientSocket.getLocalAddress().toString()
                                        + " exit");
                    message.setMessage("exit");
                    output.writeObject(message);
                    output.flush();
                    removeInstance();
                    removeInstanceTotalConnected();
                    clientSocket.close();

                    break;
                }
                switch (action){
                    case LOGIN ->{
                        User userToLogin = message.getUser();
                        if(this.user.getUserState() instanceof IdleState) {
                            boolean isLoginSuccess = userToLogin.login(output);
                            if (isLoginSuccess) {
                                System.out.println("Login success!");
                                this.user.setUserName(userToLogin.getUserName());
                                this.user.setPassword(userToLogin.getPassword());
                                this.user.setUserState(this.user.getAuthenticationState());

                                //add user to authen users list
                                addInstance();
                                addInstanceAuthenNotSleeping();
                                removeInstanceTotalIdle();
                                printState();

                                output.writeObject(new Message(Type.LOGIN, "loginSucceed"));
                            } else {
                                output.writeObject(new Message(Type.LOGIN, "wrong username or password"));
                            }
                            output.flush();
                        }else {
                            output.writeObject(new Message(Type.LOGIN, "Not available"));
                            output.flush();
                        }
                        break;
                    }

                    case REGISTER ->{
                        User registerUser = message.getUser();
                        if(this.user.getUserState() instanceof IdleState) {
                            boolean isRegisterSuccess = registerUser.register(output);
                            if(isRegisterSuccess){
                                System.out.println("Create user Successfully");
                                output.writeObject(new Message(Type.REGISTER, "success"));
                            } else {
                                System.out.println("failed to create user!");
                                output.writeObject(new Message(Type.REGISTER, "username already existed"));
                            }
                            output.flush();
                        } else {
                            output.writeObject(new Message(Type.REGISTER, "In "+ this.user.getUserState() + " | not available"));
                            output.flush();
                        }
                        break;
                    }


                    case ECHO -> {
                        String messageEcho = (String) message.getMessage();
                        this.user.echo(output, messageEcho);
//                        while (true) {
//                            String msgToEcho = message.getMessage();
//                            if (msgToEcho.equalsIgnoreCase("exit")) {
//                                removeInstance();
//                                System.out.println("disconnect.");
//                                break;
//                            }
//                            System.out.println("Message: " + msgToEcho);
//                            message.setMessage(handleEcho(msgToEcho));
//                            output.writeObject(message);
//                            output.flush();
//
//                            message = (Message) input.readObject();
//                        }
                        break;
                    }
                    case BROADCAST -> {
                        String messageToBroadcast = (String) message.getMessage();
                        System.out.println("Broadcasting...");
                        boolean isAvailableBroadcast = user.broadcast(output);
                        if(isAvailableBroadcast) {
                            broadcastMessage(messageToBroadcast);
                        }
                        break;
                    }
                    case WAKE -> {
                        this.user.wake(output);
                        this.user.setUserState(this.user.getAuthenticationState());
                        removeInstanceSleeping();
                        addInstanceAuthenNotSleeping();
                        printState();
                        break;
                    }
                    case SLEEP -> {
                        this.user.sleep(output);
                        this.user.setUserState(this.user.getSleepState());
                        removeInstanceAuthenNotSleeping();
                        addInstanceSleeping();
                        printState();
                        break;
                    }
                    case LOGOUT -> {

                        this.user.logout(output);
                        this.user.setUserState(this.user.getIdleState());
                        removeInstanceAuthenNotSleeping();
                        addInstanceTotalIdle();
                        printState();
                        break;
                    }
                    case SUBSCRIBE -> {
                        boolean isAllowAction = this.user.broadcast(output);
                        boolean isSuccess = false;
                        HotType topic = message.getTopic();

                        if(isAllowAction){
                            isSuccess = addSubscriber(topic);
                            if(isSuccess){
                                output.writeObject(new Message(Type.SUBSCRIBE, "success to subscribe"));
                            }else {
                                output.writeObject(new Message(Type.SUBSCRIBE, "failed to subscribe"));
                            }
                            output.flush();
                        }else {
                            output.writeObject(new Message(Type.SUBSCRIBE, "failed to subscribe"));
                            output.flush();
                        }

                        break;
                    }
                    case HOT -> {
                        boolean isAllowAction = this.user.broadcast(output);
                        HotType topic = message.getTopic();

                        if(isAllowAction){
                            System.out.println("public from: " + this.user.getUserName());
                            System.out.println("message: " + message.getMessage());
                            subject.setMessageContext(topic, message.getMessage());
                            output.writeObject(new Message(Type.HOT, "success to public"));
                        } else {
                            output.writeObject(new Message(Type.HOT, "not allowed"));
                            output.flush();
                        }
                        break;
                    }
                }
            }
        } catch (IOException e){

            System.err.println("error");
            e.printStackTrace();
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("error");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void printState(){
        String leftAlignFormat = "| %-15s | %-4d |%n";
        System.out.format("+-----------------+------+%n");
        System.out.format("| State           | No.  |%n");
        System.out.format("+-----------------+------+%n");
        System.out.format(leftAlignFormat, "Connected", totalConnected.size());
        System.out.format(leftAlignFormat, "Authenticated", totalAuthenticated.size());
        System.out.format(leftAlignFormat, "Au Not Sleep", totalAuthenticatedNotSleeping.size());
        System.out.format(leftAlignFormat, "Sleeping", totalSleeping.size());
        System.out.format(leftAlignFormat, "Idle", totalIdle.size());
        System.out.format("+-----------------+------+%n");
    }
}



