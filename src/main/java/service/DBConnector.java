package service;

import model.Meal;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBConnector {

    private static DBConnector INSTANCE;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/meniurestaurant";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    public static final DBConnector getInstance(){
        if(INSTANCE == null){
            INSTANCE =new DBConnector();
        }
        return INSTANCE;
    }
    Connection connection;
    PreparedStatement pStatement;
    Statement statement;


//    public void addMealToDB(Meal meal) throws SQLException {
//        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
//        String sql = "INSERT INTO products (ID, Name, Price, Quantity) VALUES (?, ?, ?, ?)";
//        pStatement = connection.prepareStatement(sql);
//        pStatement.setInt(1, meal.getID());
//        pStatement.setString(2, meal.getName());
//        pStatement.setDouble(3, meal.getPrice());
//        pStatement.setString(4, meal.getQuantity());
//        pStatement.executeUpdate();
//        pStatement.close();
//        connection.close();
//    }

    public void displayMealsFromDB() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM meniu");

//        System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Name", "Price", "Quantity");



        System.out.println("ID    Name                     Price      Quantity");
        System.out.println("----  -----------------------  ---------  -------------------------");

        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String name = resultSet.getString("Name");
            double price = resultSet.getDouble("Price");
            String quantity = resultSet.getString("Quantity");

            if (name.length() > 30) {
                name = name.substring(0, 25) + "...";
            }
            System.out.printf("%-5d %-30s %-10.2f %-50s\n", id, name, price, quantity);
        }


    }

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
    public List<Meal> orderMeal(List<Integer> itemIds) {
        List<Meal> meals = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            String sql = "SELECT id, name, price FROM meals WHERE id = ?";

            for (Integer id : itemIds) {
                pStatement = connection.prepareStatement(sql);
                pStatement.setInt(1, id);
                resultSet = pStatement.executeQuery();

                if (resultSet.next()) {
                    Meal meal = new Meal(resultSet.getInt("ID"), resultSet.getString("Name"), resultSet.getDouble("Price"),resultSet.getString("Quantity"));
                    meals.add(meal);
                }
                pStatement.close();
            }
        } catch (SQLException e) {
            System.out.println("Database error during meal ordering.");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (pStatement != null) pStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error when closing the database connection.");
                e.printStackTrace();
            }
        }
        return meals;
    }

    public double calculateTotalPrice(List<Meal> meals) {
        double totalPrice = 0.0;

        for (Meal meal : meals) {
            totalPrice += meal.getPrice();
        }

        System.out.println("Total Order Price: $" + totalPrice);
        return totalPrice;
    }



}

