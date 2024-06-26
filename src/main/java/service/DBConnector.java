package service;

import com.example.restaurantproject.Main;
import model.Meal;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBConnector {

    private static DBConnector INSTANCE;
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/meniurestaurant";
    public static final String USER = "root";
    public static final String PASSWORD = "";


    public static final DBConnector getInstance(){
        if(INSTANCE == null){
            INSTANCE =new DBConnector();
        }
        return INSTANCE;
    }
    Connection connection;
    PreparedStatement pStatement;
    public void addUsersToDB(User user) throws SQLException{
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        String sql = "INSERT INTO users (ID, Username, Password, Name, Email, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
        pStatement = connection.prepareStatement(sql);
        pStatement.setInt(1, user.getId());
        pStatement.setString(2, user.getUsername());
        pStatement.setString(3, user.getPassword());
        pStatement.setString(4, user.getName());
        pStatement.setString(5, user.getEmail());
        pStatement.setString(6, user.getPhoneNumber());

        System.out.println("You succesfully created an account! ");

        pStatement.executeUpdate();
        pStatement.close();
        connection.close();
    }
    public void loginUsersIntoStore(User user) throws SQLException{
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        pStatement = connection.prepareStatement(sql);
        pStatement.setString(1, user.getUsername());
        pStatement.setString(2, user.getPassword());

        ResultSet resultSet = pStatement.executeQuery();
        boolean found = false;

        if(resultSet.next()) {
            int id = resultSet.getInt("ID");
            if (id != 0) {
                found = true;
            }
        }

        if(found){
            System.out.println("Login was successful!");
            UserManager.getInstance().authenticate(user);
        }
        if(!found){
            System.out.println("Invalid login credentials");
        }
        pStatement.close();
        connection.close();
    }
}

