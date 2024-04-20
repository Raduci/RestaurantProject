package com.example.restaurantproject;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import javafx.scene.transform.Rotate;

import java.sql.*;
import java.util.*;
import java.util.Date;

import model.User;
import service.DBConnector;
import service.UserManager;

public class Main extends Application{
    private VBox root;
    private GridPane formPane;
    private HBox buttonBox;
    private boolean isLoggedIn = false;
    UserManager userManager = new UserManager();
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);

        HBox navbar = new HBox(10);
        navbar.setAlignment(Pos.CENTER);

        navbar.getStyleClass().add("navbar");
        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/2.png"))));
        logoView.setFitHeight(50);
        logoView.setFitWidth(50);

        Label welcomeLabel = new Label("Bine ati venit la Marius Grill!");
        welcomeLabel.getStyleClass().add("welcome-message");

        ImageView homeIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/HomeButton.png"))));
        ImageView infoIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/InfoIcon.png"))));
        ImageView contactIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/ContactIcon.png"))));

        Button homeButton = new Button("",homeIcon);
        Button aboutButton = new Button("",infoIcon);
        Button contactButton = new Button("",contactIcon);

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        navbar.getChildren().addAll(logoView, leftSpacer, welcomeLabel,rightSpacer, homeButton, aboutButton, contactButton);

        GridPane formPane = new GridPane();
        formPane.setVgap(10);
        formPane.setHgap(10);
        formPane.setAlignment(Pos.CENTER);

        homeButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
        });

        aboutButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
            showAboutUsForm(formPane);
        });

        contactButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
            showContactForm(formPane);
        });
        createBeforeLoginForm(root, formPane);
        root.getChildren().add(0, navbar);
        root.getChildren().addAll(formPane);

        Scene scene = new Scene(root, 640, 560);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("BeforeLoginCSS.css")).toExternalForm());
        primaryStage.setTitle("Resturant Marius Grill");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public void showAboutUsForm(GridPane formPane){
        formPane.getChildren().clear();
    }
    private void showContactForm(GridPane formPane){

        formPane.getChildren().clear();
        formPane.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label("Nume:");
        Label prenumeLabel = new Label("Prenume:");
        Label emailLabel = new Label("Adresa de mail:");
        Label contactLabel = new Label("Contact:");

        TextField nameField = new TextField();
        TextField prenumeField = new TextField();
        TextField emailField = new TextField();
        TextArea contactArea = new TextArea();
        Button submitButton = new Button("Trimite");


        contactArea.setMinSize(300, 100);
        formPane.add(nameLabel, 0, 0);
        formPane.add(nameField, 0, 1);
        formPane.add(prenumeLabel, 0, 2);
        formPane.add(prenumeField, 0, 3);
        formPane.add(emailLabel, 0, 4);
        formPane.add(emailField, 0, 5);
        formPane.add(contactLabel, 0, 6);
        formPane.add(contactArea, 0, 7);

        formPane.add(submitButton, 0, 8);
        GridPane.setHalignment(submitButton, HPos.CENTER); // Center the button horizontally
        GridPane.setMargin(submitButton, new Insets(20, 0, 0, 0));

        submitButton.setOnAction(event -> {
            String nume = nameField.getText();
            String prenume = prenumeField.getText();
            String email = emailField.getText();
            String contact = contactArea.getText();

            saveToDatabase(nume, prenume, email, contact);
        });

        formPane.setVgap(10);
    }
    private void createBeforeLoginForm(VBox root, GridPane formPane) {
        Button loginButton = new Button("Intra in cont!");
        Button registerButton = new Button("Inregistreza-te!");

        loginButton.setOnAction(e -> showLoginForm(formPane));
        registerButton.setOnAction(e -> showRegisterForm(formPane));

        if (buttonBox == null) {
            buttonBox = new HBox(10, loginButton, registerButton);
            buttonBox.setAlignment(Pos.CENTER);
        }
        if (!root.getChildren().contains(buttonBox)) {
            root.getChildren().add(buttonBox);
        }

    }
    private void removeBeforeLoginForm(VBox root) {
        root.getChildren().remove(buttonBox);
    }
    private void showBeforeLoginForm(GridPane formPane){
    }
    private void showLoggedInForm(GridPane formPane) {
        formPane.getChildren().clear();

    }

    private boolean performLogin() {
        // Your login logic
        // This should return true if login is successful, false otherwise
        return true; // Placeholder for demonstration purposes
    }
    private void showLoginForm(GridPane formPane) {

            prepareLoginForm(formPane);
            formPane.getChildren().clear();
            formPane.setAlignment(Pos.BOTTOM_CENTER);

            Label usernameLabel = new Label("Nume de utilizator:");
            TextField usernameTextField = new TextField();
            Label passwordLabel = new Label("Parola:");
            PasswordField passwordField = new PasswordField();
            Button submitButton = new Button("Log In");

            formPane.add(usernameLabel, 0, 0);
            formPane.add(usernameTextField, 1, 0);
            formPane.add(passwordLabel, 0, 1);
            formPane.add(passwordField, 1, 1);
            formPane.add(submitButton, 1, 2);
            GridPane.setHalignment(submitButton, HPos.CENTER);

            submitButton.setOnAction(e -> {
                User user = new User(usernameTextField.getText(), passwordField.getText());
                try {
                    boolean isAuthenticated = userManager.authenticate(user);
                    if (isAuthenticated) {
                        DBConnector.getInstance().loginUsersIntoStore(user);
                        System.out.println("Login-ul a fost realizat cu succes!");
                        showLoggedInForm(formPane);
                    } else {
                        System.out.println("Username sau parola gresita!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

}
    private void showRegisterForm(GridPane formPane) {

            formPane.getChildren().clear();
            formPane.setAlignment(Pos.BOTTOM_CENTER);

            TextField usernameTextField = new TextField();
            PasswordField passwordTextField = new PasswordField();
            TextField nameTextField = new TextField();
            TextField emailTextField = new TextField();
            TextField phoneTextField = new TextField();
            Button registerButton = new Button("Register");

            formPane.addRow(0, new Label("Nume de utilizator:"), usernameTextField);
            formPane.addRow(1, new Label("Parola:"), passwordTextField);
            formPane.addRow(2, new Label("Nume:"), nameTextField);
            formPane.addRow(3, new Label("Email:"), emailTextField);
            formPane.addRow(4, new Label("Numar de telefon:"), phoneTextField);
            formPane.addRow(5, registerButton);
            GridPane.setHalignment(registerButton, HPos.CENTER);

            registerButton.setOnAction(e -> {
                User user = new User(usernameTextField.getText(), passwordTextField.getText());
                user.setName(nameTextField.getText());
                user.setEmail(emailTextField.getText());
                user.setPhoneNumber(phoneTextField.getText());
                try {
                    DBConnector.getInstance().addUsersToDB(user);
                    userManager.addUser(user);
                    System.out.println("Inregistrarea s-a realizat cu succes!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
    }

    private void saveToDatabase(String nume, String prenume, String email, String contact) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/meniurestaurant", "root", "");

            String sql = "INSERT INTO contact (Nume, Prenume, Email, Contact) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, email);
            pstmt.setString(4, contact);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }
    private void prepareLoginForm(GridPane formPane) {
        formPane.getChildren().clear();
        formPane.setAlignment(Pos.BOTTOM_CENTER);

        Label usernameLabel = new Label("Nume de utilizator:");
        TextField usernameTextField = new TextField();
        Label passwordLabel = new Label("Parola:");
        PasswordField passwordField = new PasswordField();
        Button submitButton = new Button("Log In");

        formPane.add(usernameLabel, 0, 0);
        formPane.add(usernameTextField, 1, 0);
        formPane.add(passwordLabel, 0, 1);
        formPane.add(passwordField, 1, 1);
        formPane.add(submitButton, 1, 2);
        GridPane.setHalignment(submitButton, HPos.CENTER);
    }

    public static void main(String[] args){
        launch(args);
    }

}



