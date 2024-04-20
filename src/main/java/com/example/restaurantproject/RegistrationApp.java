//package com.example.restaurantproject;
//
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//import javafx.application.Application;
//import model.*;
//import service.DBConnector;
//import service.UserManager;
//
//
//import java.sql.*;
//import java.util.*;
//import java.util.Date;
//
//import static javafx.application.Application.launch;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//public class RegistrationApp extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        UserManager userManager = new UserManager();
//        Scanner input = new Scanner(System.in);  // This will not be used in JavaFX. Instead, use TextField for input.
//
//        User user = new User();
//
//        GridPane grid = new GridPane();
//        grid.setVgap(10);
//        grid.setHgap(10);
//
//        Label usernameLabel = new Label("Enter username:");
//        TextField usernameTextField = new TextField();
//        grid.add(usernameLabel, 0, 0);
//        grid.add(usernameTextField, 1, 0);
//
//        Label passwordLabel = new Label("Enter password:");
//        PasswordField passwordField = new PasswordField();
//        grid.add(passwordLabel, 0, 1);
//        grid.add(passwordField, 1, 1);
//
//        Label nameLabel = new Label("Enter your name:");
//        TextField nameTextField = new TextField();
//        grid.add(nameLabel, 0, 2);
//        grid.add(nameTextField, 1, 2);
//
//        Label emailLabel = new Label("Enter your email:");
//        TextField emailTextField = new TextField();
//        grid.add(emailLabel, 0, 3);
//        grid.add(emailTextField, 1, 3);
//
//        Label phoneLabel = new Label("Enter your phone number:");
//        TextField phoneTextField = new TextField();
//        grid.add(phoneLabel, 0, 4);
//        grid.add(phoneTextField, 1, 4);
//
//        Button registerButton = new Button("Register");
//        registerButton.setOnAction(e -> {
//            user.setUsername(usernameTextField.getText());
//            user.setPassword(passwordField.getText());
//            user.setName(nameTextField.getText());
//            user.setEmail(emailTextField.getText());
//            user.setPhoneNumber(phoneTextField.getText());
//
//            try {
//                DBConnector.getInstance().addUsersToDB(user);
//            } catch (SQLException ex) {
//                throw new RuntimeException(ex);
//            }
//            userManager.addUser(user);
//        });
//        grid.add(registerButton, 1, 5);
//
//        Scene scene = new Scene(grid, 300, 250);
//        primaryStage.setTitle("Registration Form");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//
