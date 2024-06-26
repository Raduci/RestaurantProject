package com.example.restaurantproject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.*;

import java.sql.*;
import java.util.*;

import javafx.stage.StageStyle;
import model.Meal;
import model.Spacer;
import model.User;
import service.DBConnector;
import service.UserManager;

import static service.DBConnector.JDBC_URL;
import static service.DBConnector.USER;
import static service.DBConnector.PASSWORD;

public class Main extends Application{
    private final TableView<Meal> tableView = new TableView<>();
    VBox root = new VBox(15);
    GridPane formPane = new GridPane();
    private HBox buttonBox;
    private boolean isLoggedIn = false;
    UserManager userManager = new UserManager();
    @Override
    public void start(Stage primaryStage) {
        setupTable();
        Font.loadFont(getClass().getResourceAsStream("Anton-Regular.ttf"), 220);
        root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setFillWidth(true);

        HBox infoBar = new HBox();
        infoBar.setStyle("-fx-background-color: black; -fx-padding: 15px;");
        infoBar.setAlignment(Pos.CENTER_LEFT);
        Label phoneLabel = new Label("0725 445 923");
        phoneLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");

        Label hoursLabel = new Label("M-D: 11:00 - 24:00");
        hoursLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        infoBar.getChildren().addAll(phoneLabel, new Spacer(), hoursLabel);

        StackPane containerForCentering = new StackPane();
        containerForCentering.getChildren().add(formPane);
        StackPane.setAlignment(formPane, Pos.CENTER);

        HBox navbar = createNavbar();
        formPane = new GridPane();
        formPane.setVgap(10);
        formPane.setHgap(10);
        formPane.setAlignment(Pos.CENTER);

        root.getChildren().addAll(infoBar, navbar, formPane);

        if(!isLoggedIn){
            createBeforeLoginForm(root, formPane);
        } else{
            createAfterLoginForm(root, formPane);
        }
        Scene scene = new Scene(root, 1024, 768);

        formPane.setAlignment(Pos.BOTTOM_CENTER);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("BeforeLoginCSS.css")).toExternalForm());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/deliciiurbane.png"))));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(null);
        primaryStage.setTitle("Lounge & Bistro Delicii Urbane");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void createAfterLoginForm(VBox root,GridPane formPane){
        formPane.getChildren().clear();
        StackPane stackPane = new StackPane();
        stackPane.getChildren().clear();

        Button menuButton = new Button("Display Menu");
        Button accountButton = new Button("Manage Account");
        Button orderButton = new Button("Order");

        buttonBox = new HBox(10, menuButton, accountButton, orderButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        stackPane.getChildren().add(buttonBox);
        StackPane.setAlignment(buttonBox, Pos.TOP_CENTER);

        String User = userManager.printUserIfAuthenticated();
        Label welcomeLabel = new Label("Bine ai venit, " + User + "!");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        welcomeLabel.setAlignment(Pos.CENTER);

        formPane.add(welcomeLabel,0,0);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        root.getChildren().addAll(formPane, stackPane);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
    }
    private HBox createNavbar() {
        HBox navbar = new HBox(25);
        navbar.setAlignment(Pos.CENTER);
        navbar.getStyleClass().add("navbar");

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/deliciiurbane.png"))));
        logoView.setFitHeight(115);
        logoView.setFitWidth(115);

        ImageView logoutIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logout.png"))));
        ImageView homeIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/HomeButton.png"))));
        ImageView infoIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/InfoIcon.png"))));
        ImageView contactIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/ContactIcon.png"))));


        Button homeButton = new Button("", homeIcon);
        Button aboutButton = new Button("", infoIcon);
        Button contactButton = new Button("", contactIcon);
        Button logoutButton = new Button("", logoutIcon);
        logoutButton.setOnAction(e -> Platform.exit());

        homeButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
            try {
                homePageForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        aboutButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
            showAboutUsForm(formPane);
        });
        contactButton.setOnAction(e -> {
            removeBeforeLoginForm(root);
            showContactForm(formPane);
        });



        Label welcomeLabel = new Label("Bine ați venit la Lounge & Bistro Delicii Urbane!");
        welcomeLabel.setFont(Font.loadFont(getClass().getResourceAsStream("Anton-Regular.ttf"), 20));
        welcomeLabel.setStyle("-fx-font-family: 'Anton'; -fx-font-size: 20; -fx-background-radius: 0;" + "-fx-border-radius: 0;");

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        navbar.getChildren().addAll(logoView, leftSpacer, welcomeLabel, rightSpacer, homeButton, aboutButton, contactButton, logoutButton);

        return navbar;
    }
    public void showAboutUsForm(GridPane formPane){
        formPane.getChildren().clear();
        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        Label povesteLabel = new Label("Care este povestea localului \"Lounge &Bistro Delicii Urbane\"? ");
        Label introducereTextLabel = new Label("Unde gustul a prins viață: o incursiune în gastronomie!");
        Label cuprinsTextLabel = new Label("Într-un cartier plin de viață al orașului, printre clădiri vechi și străzi aglomerate, se află un mic bistro cu o atmosferă aparte. Aici, fiecare farfurie pregătită are o poveste de spus, inspirată de agitația și diversitatea vieții urbane. Bistro-ul reprezintă un refugiu pentru cei dornici să descopere arome și gusturi noi, combinate în moduri inovatoare. Meniul său divers reflectă influențe din diferite culturi culinare, creând o experiență gastronomică internațională pentru clienții săi. Întreaga atmosferă din bistro respiră energie și creativitate, fiind un loc unde oamenii se simt inspirați și relaxați în același timp. Fiecare preparat este pregătit cu pasiune și atenție la detalii, aducând în farfurie o explozie de arome și texturi surprinzătoare. Bistro-ul este mai mult decât un simplu loc de luat masa - este o destinație pentru cei care își doresc să experimenteze deliciile  vieții urbane într-un mod inedit și captivant. Fiecare vizită în acest loc magic este o călătorie culinară plină de surprize și bucuria de a descoperi noi combinații și reinterpretări ale preparatelor tradiționale. Astfel, acest mic bistro a devenit un simbol al creativității și diversității din inima orașului, atrăgând clienți dornici să exploreze universul fascinant al deliciilor urbane.");

        povesteLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        introducereTextLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        cuprinsTextLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");

        cuprinsTextLabel.setMinSize(300, 100);
        cuprinsTextLabel.setWrapText(true);
        formPane.add(povesteLabel,0,0);
        formPane.add(introducereTextLabel,0,1);
        formPane.add(cuprinsTextLabel,0,2);


        GridPane.setHalignment(povesteLabel, HPos.CENTER);
        GridPane.setMargin(povesteLabel, new Insets(20, 0, 10, 0));
        GridPane.setHalignment(introducereTextLabel, HPos.CENTER);
        GridPane.setHalignment(cuprinsTextLabel, HPos.CENTER);
        formPane.setVgap(10);
    }
    private void showContactForm(GridPane formPane){

        formPane.getChildren().clear();
        formPane.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label("Nume:");
        Label prenumeLabel = new Label("Prenume:");
        Label emailLabel = new Label("Adresă de mail:");
        Label contactLabel = new Label("Contact:");

        TextField nameField = new TextField();
        TextField prenumeField = new TextField();
        TextField emailField = new TextField();
        TextArea contactArea = new TextArea();
        Button submitButton = new Button("Trimite!");


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
        GridPane.setHalignment(submitButton, HPos.CENTER);
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
        StackPane stackPane = new StackPane();
        Button loginButton = new Button("Intră în cont!");
        Button registerButton = new Button("Înregistrează-te!");

        loginButton.setOnAction(e -> {
            showLoginForm(formPane);
            loginButton.setVisible(false);
            registerButton.setVisible(false);
        });
        registerButton.setOnAction(e -> showRegisterForm(formPane));

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        stackPane.getChildren().add(buttonBox);
        StackPane.setAlignment(buttonBox, Pos.CENTER);

        root.getChildren().add(stackPane);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

    }
    private void removeBeforeLoginForm(VBox root) {
        root.getChildren().remove(buttonBox);
    }
    private void showLoginForm(GridPane formPane) {

        prepareLoginForm(formPane);
        formPane.getChildren().clear();
        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        Label usernameLabel = new Label("Nume de utilizator:");
        TextField usernameTextField = new TextField();
        Label passwordLabel = new Label("Parolă:");
        PasswordField passwordField = new PasswordField();
        Button submitButton = new Button("Intră în cont!");

        usernameLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        passwordLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");

        formPane.add(usernameLabel, 0, 0);
        formPane.add(usernameTextField, 1, 0);
        formPane.add(passwordLabel, 0, 1);
        formPane.add(passwordField, 1, 1);
        formPane.add(submitButton, 1, 2);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setValignment(submitButton, VPos.CENTER);

        submitButton.setOnAction(e -> {
            User user = new User(usernameTextField.getText(), passwordField.getText());
            try {
                boolean isAuthenticated = userManager.authenticate(user);
                if (isAuthenticated) {
                    DBConnector.getInstance().loginUsersIntoStore(user);
                    System.out.println("V-ați logat cu succes!");
                    formPane.getChildren().clear();
                    createAfterLoginForm(root, formPane);

                } else {
                    System.out.println("Nume de utilizator sau parolă greșită!");
                    isLoggedIn = false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

    }
    private void showRegisterForm(GridPane formPane) {

        formPane.getChildren().clear();
        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        TextField usernameTextField = new TextField();
        PasswordField passwordTextField = new PasswordField();
        TextField nameTextField = new TextField();
        TextField emailTextField = new TextField();
        TextField phoneTextField = new TextField();
        Button registerButton = new Button("Înregistrează-te!");

        Label usernameLabel = new Label("Nume de utilizator:");
        usernameLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label passwordLabel = new Label("Parolă:");
        passwordLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label nameLabel = new Label("Nume:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label emailLabel = new Label("Adresă de mail:");
        emailLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label phoneLabel = new Label("Număr de telefon:");
        phoneLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");

        formPane.addRow(0, usernameLabel, usernameTextField);
        formPane.addRow(1, passwordLabel, passwordTextField);
        formPane.addRow(2, nameLabel, nameTextField);
        formPane.addRow(3, emailLabel, emailTextField);
        formPane.addRow(4, phoneLabel, phoneTextField);
        formPane.addRow(5, registerButton);
        GridPane.setHalignment(registerButton, HPos.CENTER);
        GridPane.setValignment(registerButton, VPos.CENTER);

        registerButton.setOnAction(e -> {
            User user = new User(usernameTextField.getText(), passwordTextField.getText());
            user.setName(nameTextField.getText());
            user.setEmail(emailTextField.getText());
            user.setPhoneNumber(phoneTextField.getText());
            try {
                DBConnector.getInstance().addUsersToDB(user);
                userManager.addUser(user);
                System.out.println("V-ați înregistrat cu succes!");
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
        Label passwordLabel = new Label("Parolă:");
        PasswordField passwordField = new PasswordField();
        Button submitButton = new Button("Intră în cont!");

        formPane.add(usernameLabel, 0, 0);
        formPane.add(usernameTextField, 1, 0);
        formPane.add(passwordLabel, 0, 1);
        formPane.add(passwordField, 1, 1);
        formPane.add(submitButton, 1, 2);
        GridPane.setHalignment(submitButton, HPos.CENTER);
    }
    private void homePageForm() throws SQLException{
        formPane.getChildren().clear();

        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        Label optionsLabel = new Label("Alege una din următoarele opțiuni:");
        Label veziMeniuLabel = new Label("1. Preparate disponibile!");
        Label comandaLabel = new Label("2. Comandă un preparat!");

        optionsLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        veziMeniuLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        comandaLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        Button veziMeniuButton = new Button("Meniul nostru!");
        Button comandaButton = new Button("Comandă!");

        comandaButton.setOnAction(e -> showOrderForm(formPane));
        veziMeniuButton.setOnAction(e -> {
            try {
                displayMealsFromDB();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        veziMeniuButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        comandaButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        Image menuImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/meniuIcon.png")));
        Image commandImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/proceseazaComandaIcon.png")));

        veziMeniuButton.setGraphic(new ImageView(menuImage));
        comandaButton.setGraphic(new ImageView(commandImage));

        formPane.add(optionsLabel, 0, 0);
        formPane.add(veziMeniuLabel, 0, 1);
        formPane.add(veziMeniuButton, 1, 1);
        formPane.add(comandaLabel, 0, 3);
        formPane.add(comandaButton, 1, 3);

        GridPane.setHalignment(optionsLabel, HPos.CENTER);
        GridPane.setHalignment(veziMeniuButton, HPos.CENTER);

        GridPane.setHalignment(comandaButton, HPos.CENTER);

        GridPane.setMargin(veziMeniuButton, new Insets(10, 0, 10, 0));
        GridPane.setMargin(comandaButton, new Insets(10, 0, 10, 0));

        formPane.setVgap(10);
    }

        private void setupTable() {
            tableView.getColumns().clear();

            TableColumn<Meal, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn<Meal, Double> priceColumn = new TableColumn<>("Price");
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            TableColumn<Meal, String> quantityColumn = new TableColumn<>("Quantity");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

            tableView.getColumns().addAll(nameColumn, priceColumn, quantityColumn);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableView.setPrefSize(650, 550);

            if (!formPane.getChildren().contains(tableView)) {
                formPane.add(tableView, 0, 0);
                GridPane.setVgrow(tableView, Priority.ALWAYS);
                GridPane.setHgrow(tableView, Priority.ALWAYS);
                GridPane.setHalignment(tableView, HPos.CENTER);
                GridPane.setValignment(tableView, VPos.CENTER);
                GridPane.setMargin(tableView, new Insets(30, 150, 30, 270));
            }
        }

    private void displayMealsFromDB() throws SQLException {
        setupTable();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM meniu")) {

            tableView.getItems().clear();
            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                double price = resultSet.getDouble("Price");
                String quantity = resultSet.getString("Quantity");
                Meal meal = new Meal(name, price, quantity);
                tableView.getItems().add(meal);
            }
        }

        formPane.getChildren().clear();

        if (!formPane.getChildren().contains(tableView)) {
            formPane.add(tableView, 0, 0);
        }

        Button backButton = new Button("Înapoi!");
        Button orderButton = new Button("Comandă!");
        backButton.setOnAction(e -> {
            try {
                homePageForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        orderButton.setOnAction(e -> showOrderForm(formPane));


        HBox buttonBox = new HBox(10, backButton, orderButton);
        buttonBox.setAlignment(Pos.CENTER);

        formPane.add(buttonBox, 0, 1);
        GridPane.setHalignment(buttonBox, HPos.CENTER);
        GridPane.setValignment(buttonBox, VPos.CENTER);
        GridPane.setMargin(buttonBox, new Insets(20, 0, 0, 0));

        tableView.refresh();
    }

    public static void showOrderForm(GridPane formPane){
        formPane.getChildren().clear();
        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(50, 100, 50, 100));
        formPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-width: 0;");

        formPane.getChildren().clear();
        formPane.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label("Nume:");
        Label phoneNumberLabel = new Label("Număr de telefon:");
        Label adressLabel = new Label("Adresă de mail:");
        Label orderLabel = new Label("Comandă:");

        TextField nameField = new TextField();
        TextField prenumeField = new TextField();
        TextField emailField = new TextField();
        TextArea contactArea = new TextArea();
        Button submitButton = new Button("Trimite!");


        contactArea.setMinSize(300, 100);
        formPane.add(nameLabel, 0, 0);
        formPane.add(nameField, 0, 1);
        formPane.add(phoneNumberLabel, 0, 2);
        formPane.add(prenumeField, 0, 3);
        formPane.add(adressLabel, 0, 4);
        formPane.add(emailField, 0, 5);
        formPane.add(orderLabel, 0, 6);
        formPane.add(contactArea, 0, 7);

        formPane.add(submitButton, 0, 8);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0, 0, 0));

        submitButton.setOnAction(event -> {
            String nume = nameField.getText();
            String prenume = prenumeField.getText();
            String email = emailField.getText();
            String contact = contactArea.getText();

            saveOrderToDatabase(nume, prenume, email, contact);
        });
        formPane.setVgap(10);
    }

    private static void saveOrderToDatabase(String Nume, String NumarTelefon, String Adresa, String Comanda) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/meniurestaurant", "root", "");

            String sql = "INSERT INTO comenzi (Nume, NumarTelefon, Adresa, Comanda) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, Nume);
            pstmt.setString(2, NumarTelefon);
            pstmt.setString(3, Adresa);
            pstmt.setString(4, Comanda);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }


    public static void main(String[] args){
        launch(args);
    }
}
