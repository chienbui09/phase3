package server;

import database.DBConnection;
import model.Message;
import model.Type;
import model.User;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ServerThread implements Runnable{
    public static final String USER_CREATE = "INSERT INTO user (name, password) VALUES (?, ?);";
    private static final ArrayList<ServerThread> instances = new ArrayList<>();
    private static final String USER_LOGIN = "SELECT * FROM user WHERE name = ? and password = ?";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM user WHERE name = ?";
    private final Socket clientSocket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public ServerThread(Socket accept) throws IOException {
        this.clientSocket = accept;
        User user = new User();
        this.input = new ObjectInputStream(accept.getInputStream());
        this.output = new ObjectOutputStream(accept.getOutputStream());
        this.addInstance();
    }
    private static  String handleEcho(String msgToHandle){
        String regex = "[ ](?=[ ])|[^-_,A-Za-z0-9 ]+";
        msgToHandle = msgToHandle.replaceAll(regex,"");
        return msgToHandle;


    }

    private synchronized void addInstance(){
        instances.add(this);
    }
    private synchronized void removeInstance(){
        instances.remove(this);
    }

    private synchronized User checkLogin(User user) throws SQLException{
        try(DBConnection dbHelper  = DBConnection.getDBHelper();
            Connection connection = dbHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement(USER_LOGIN)){
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            ResultSet results = statement.executeQuery();
            User existedUser = null;
            if(results.next()){
                existedUser = new User();
                existedUser.setUserName(results.getString("name"));
            }
            return existedUser;
        }
    }
    private synchronized Boolean createUser(User user) throws SQLException {
        boolean rowUpdated = false;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_CREATE)) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            rowUpdated = statement.executeUpdate() > 0;

            return rowUpdated;
        }
    }
    private synchronized User isUserExisted(User user) throws SQLException {
        User existedUser = null;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            statement.setString(1, user.getUserName());
            ResultSet results = statement.executeQuery();
            if (results.next()){
                existedUser = new User();
                existedUser.setUserName(results.getString("name"));
            }
            return existedUser;
        }
    }
    @Override
    public void run() {
        try
        {

            Message message;

            while (true){
                message = (Message) input.readObject();

                Type action = message.getMsgType();
                System.out.println("Client option: " + action);
                if(action == Type.EXIT){
                    System.out.println("client: " + clientSocket.getLocalAddress().toString()
                                        + " exit");
                    message.setMessage("exit");
                    output.writeObject(message);
                    output.flush();
                }
                switch (action){
                    case LOGIN ->{
                        User user = message.getUser();
                        User existedUser = checkLogin(user);
                        if(existedUser !=null){
                            System.out.println("login successfully!");
                            message.setMessage("Login succeed");
                            output.writeObject(message);
                            output.flush();
                        }else {
                            System.out.println("failed to login");
                            message.setMessage("fail");
                            output.writeObject(message);
                            output.flush();
                        }
                    }

                    case REGISTER ->{
                        User user = message.getUser();
                        User isUserExisted = isUserExisted(user);
                        if(isUserExisted == null){
                            boolean isCreated = createUser(user);
                            if(isCreated){
                                System.out.println("Initialize use successfully!");
                                message.setMessage("success");
                                output.writeObject(message);
                                output.flush();
                            } else {
                                System.out.println("Failed to creat user!");
                                message.setMessage("fail");
                                output.writeObject(message);
                                output.flush();
                            }
                        } else {
                            System.out.println("Username already existed: " +
                                                user.getUserName());
                            message.setMessage("Username already existed");
                            output.writeObject(message);
                            output.flush();
                        }
                    }

                    case ECHO -> {
                        while (true) {
                            String msgToEcho = message.getMessage();
                            if (msgToEcho.equalsIgnoreCase("exit")) {
                                removeInstance();
                                System.out.println("disconnect.");
                                break;
                            }
                            System.out.println("Message: " + msgToEcho);
                            message.setMessage(handleEcho(msgToEcho));
                            output.writeObject(message);
                            output.flush();

                            message = (Message) input.readObject();
                        }

                    }

                }
            }
        } catch (IOException e){

            System.err.println("error");
            e.printStackTrace();
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("errorrr");
            e.printStackTrace();
        }
    }
}
