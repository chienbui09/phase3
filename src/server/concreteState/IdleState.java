package server.concreteState;

import database.DBConnection;
import model.Message;
import model.Type;
import model.User;
import server.server.ServerThread;
import state.UserState;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IdleState implements UserState, Serializable {
    public static final String USER_CREATE = "INSERT INTO user (name, password) VALUES (?, ?);";
    private static final ArrayList<ServerThread> instances = new ArrayList<>();
    private static final String USER_LOGIN = "SELECT * FROM user WHERE name = ? and password = ?";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM user WHERE name = ?";
    private User user;

    public IdleState(User user) {
        this.user = user;
    }

    private synchronized User checkLogin(User user) throws SQLException {
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
    public boolean wake(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in idle state");
        outputStream.writeObject(new Message(Type.WAKE, "not available"));
        return false;
    }

    @Override
    public boolean register(ObjectOutputStream outputStream) throws Exception {
        User isUserExisted = null;
        try {
            isUserExisted = isUserExisted(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (isUserExisted == null) {
            boolean isCreated = false;
            try {
                isCreated = createUser(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isCreated;
        } else {
            return false;
        }
    }

    @Override
    public boolean login(ObjectOutputStream outputStream) throws Exception {
        User existedUser = null;
        System.out.println("Inside Login:" + user.getUserName());
        try {
            existedUser = checkLogin(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existedUser != null;
    }

    @Override
    public void  echo(ObjectOutputStream outputStream, String message) throws Exception {
        System.out.println("user is in idle state");
        outputStream.writeObject(new Message(Type.ECHO, "not available"));;
    }

    @Override
    public boolean broadcast(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in idle state");
        outputStream.writeObject(new Message(Type.BROADCAST, "not available"));
        return false;
    }

    @Override
    public boolean logout(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in idle state");
        outputStream.writeObject(new Message(Type.LOGOUT, "not available"));
        return false;
    }

    @Override
    public boolean sleep(ObjectOutputStream outputStream) throws Exception {
        System.out.println("user is in idle state");
        outputStream.writeObject(new Message(Type.SLEEP, "not available"));
        return false;
    }

    @Override
    public String toString() {
        return "Idle state";
    }
}
