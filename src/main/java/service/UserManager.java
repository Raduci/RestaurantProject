package service;

import exception.UserAlreadyExistException;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserManager {
    Connection connection;
    PreparedStatement pStatement;
    Statement statement;
    ResultSet rs;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/meniurestaurant";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static UserManager INSTANCE;

    private static User authenticatedUser;

    private List<User> userList;

    public UserManager() {
        this.userList = new ArrayList<>();
    }

    public static final UserManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserManager();
        }
        return INSTANCE;
    }

    public void addUser(User user) {
        for (User usr : userList){
            if(usr.getUsername().equalsIgnoreCase(user.getUsername())){
                throw new UserAlreadyExistException(user);
            }
        }
        userList.add(user);
    }

    public boolean authenticate(User user) throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        pStatement = connection.prepareStatement(sql);
        pStatement.setString(1, user.getUsername());
        pStatement.setString(2, user.getPassword());

        ResultSet resultSet = pStatement.executeQuery();
        boolean isAuthenticated = resultSet.next();


        if(isAuthenticated){
            authenticatedUser = user;
        }
        pStatement.close();
        connection.close();
        return isAuthenticated;
    }

    public static User getAuthenticatedUser(){
        return authenticatedUser;
    }

    public void logout() {
        System.out.println("The user has been successfully logged out " + authenticatedUser.getUsername().toUpperCase());
        authenticatedUser = null;
    }

    public String printUserIfAuthenticated() {
        if (authenticatedUser != null) {
            System.out.print("[" + authenticatedUser.getUsername().toUpperCase() + "] ");
        }
        assert authenticatedUser != null;
        return authenticatedUser.getUsername();
    }

    public static void printWelcomeMessageIfAuthenticated(){
        if(authenticatedUser != null){
            System.out.println("Welcome " + authenticatedUser.getUsername());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManager that = (UserManager) o;
        return userList.equals(that.userList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userList);
    }

    public static User getUser() {
        User user = new User();
        return user;
    }
}
