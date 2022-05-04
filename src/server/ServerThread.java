package server;

import database.DBConnection;
import model.Message;
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
    public ServerThread(Socket accept) {
        this.clientSocket = accept;
        addInstance();
    }
    private static  String handleEcho(String msgToHandle){
        String regex = "[^0-9a-zA-Z]";
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
            InputStream  inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            ObjectInputStream input = new ObjectInputStream(inputStream);
            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            Message message = new Message();

            while (true){
                String action = input.readUTF();
                System.out.println("g");
                System.out.println("Client option: " + action);
                if(action.equalsIgnoreCase("exit")){
                    System.out.println("client: " + clientSocket.getLocalAddress().toString()
                                        + " exit");
                }
                switch (action){
                    case "login" ->{
                        User user = (User) input.readObject();
                        User existedUser = checkLogin(user);
                        if(existedUser !=null){
                            System.out.println("login successfully!");
                            message.setMessage("Login succeed");
//                            output.writeUTF("Login succeed!");
                            output.writeObject(message);
                            output.flush();
                        }else {
                            System.out.println("failed to login");
//                            output.writeUTF("Wrong username or password");
                            message.setMessage("fail");
                            output.writeObject(message);
                            output.flush();
                        }
                    }

                    case "register" ->{
                        message = (Message) input.readObject();
                        User user = message.getUser();
                        User isUserExisted = isUserExisted(user);
                        if(isUserExisted == null){
                            boolean isCreated = createUser(user);
                            if(isCreated){
                                System.out.println("Initialize use successfully!");
                                output.writeUTF("success");
                                output.flush();
                            } else {
                                System.out.println("Failed to creat user!");
                                output.writeUTF("fail");
                                output.flush();
                            }
                        } else {
                            output.writeUTF("Username already existed");
                            output.flush();
                        }
                    }

                    case "echo" -> {
                        while (true) {
                            message = (Message) input.readObject();
                            if (message.getMessage().equalsIgnoreCase("exit")) {
                                removeInstance();
                                System.out.println("disconnect.");
                                break;
                            }
                            System.out.println("Message: " + message);
                            message.setMessage(handleEcho(message.getMessage()));
                            output.writeObject(message);
                            output.flush();

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
