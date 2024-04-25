package com.example.restaurantproject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import javafx.scene.transform.Rotate;
import javafx.scene.text.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import model.Meal;
import model.Spacer;
import model.User;
import org.controlsfx.control.spreadsheet.Grid;
import service.DBConnector;
import service.UserManager;

import static service.DBConnector.JDBC_URL;
import static service.DBConnector.USER;
import static service.DBConnector.PASSWORD;

public class Main extends Application{
    Connection connection;
    PreparedStatement pStatement;
    Statement statement;
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

        Label hoursLabel = new Label("L-D: 11:00 - 23:00");
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
        }else{
        createAfterLoginForm(root, formPane);
        }

        Scene scene = new Scene(root, 1920, 1080);

        formPane.setAlignment(Pos.BOTTOM_CENTER);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("BeforeLoginCSS.css")).toExternalForm());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/2.png"))));
        primaryStage.setTitle("Resturant Marius Grill");
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


//        menuButton.setOnAction(e -> arata meniul);
//        accountButton.setOnAction(e -> meniu de cont sterge cont schimba parola etc);
//        orderButton.setOnAction(e -> vezi comanda unde arata comanda + pret final + alege tips);


            buttonBox = new HBox(10, menuButton, accountButton, orderButton);
            buttonBox.setAlignment(Pos.TOP_CENTER);

            stackPane.getChildren().add(buttonBox);
            StackPane.setAlignment(buttonBox, Pos.TOP_CENTER);

        String User = userManager.printUserIfAuthenticated();
        Label welcomeLabel = new Label("Welcome," + User);
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

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/2.png"))));
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



        Label welcomeLabel = new Label("Bine ati venit la Marius Grill!");
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

        Label povesteLabel = new Label("Povestea Noastra!");
        Label introducereTextLabel = new Label("Cum a inceput totul?");
        Label cuprinsTextLabel = new Label("\"Marius Grill\" a fost fondat de Marius Popescu, un bucătar pasionat care a început să vândă mâncăruri gătite pe grătar dintr-un mic stand situat în centrul orașului său natal, în timpul unei perioade de revitalizare urbană. Marius a recunoscut potențialul de a crea o experiență culinară unică bazată pe tradițiile locale de gătit carne la grătar și a deschis primul restaurant \"Marius Grill\" în 2010. Acesta a devenit rapid popular pentru calitatea ingredientelor locale și pentru atmosfera sa caldă și primitoare. Sub brandingul lui Marius, care a îmbrățișat titlul de \"Maestru al Grătarului\", afacerea a crescut, iar imaginea sa a devenit un simbol recunoscut în publicitatea \"Marius Grill\". Expansiunea rapidă a afacerii a făcut ca gestionarea acesteia să devină o provocare, determinându-l pe Marius să se alieze cu parteneri de afaceri pentru a ajuta la administrare. \"Marius Grill\" a fost una dintre primele lanțuri de restaurante de tip grill care s-a extins la nivel internațional, deschizând sucursale în mai multe țări europene până la mijlocul anilor 2010. De-a lungul anilor, \"Marius Grill\" a întâmpinat succes variabil, navigând prin schimbări de proprietate și strategii de marketing diverse. În 2020, Marius a decis să transforme \"Marius Grill\" într-o franciză globală, cu scopul de a aduce bucătăria sa autentică pe piața internațională. Rețeaua a continuat să crească, adaptându-se piețelor locale și păstrându-și angajamentul față de calitate și autenticitate. În prezent, \"Marius Grill\" numără peste 50 de locații în Europa și se extinde rapid în alte regiuni, păstrându-și esența de restaurant de familie, chiar și pe măsură ce se adaptează la gusturile globale.");

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
        Button loginButton = new Button("Intra in cont!");
        Button registerButton = new Button("Inregistreaza-te!");

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
            Label passwordLabel = new Label("Parola:");
            PasswordField passwordField = new PasswordField();
            Button submitButton = new Button("Log In");

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
                    System.out.println("Login-ul a fost realizat cu succes!");
                    formPane.getChildren().clear();
                    createAfterLoginForm(root, formPane);

                } else {
                    System.out.println("Username sau parola gresita!");
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
            Button registerButton = new Button("Register");

        Label usernameLabel = new Label("Nume de utilizator:");
        usernameLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label passwordLabel = new Label("Parola:");
        passwordLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label nameLabel = new Label("Nume:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        Label phoneLabel = new Label("Numar de telefon:");
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
    private void homePageForm() throws SQLException{formPane.getChildren().clear();
        formPane.setAlignment(Pos.CENTER);

        Label optionsLabel = new Label("Alege una din urmatoarele optiuni:");
        Label veziMeniuLabel = new Label("1. Vezi ce este disponibil in meniu");
        Label veziComandaLabel = new Label("2. Vezi comanda ta:");
        Label comandaLabel = new Label("3. Comanda un meniu:");

        optionsLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        veziMeniuLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        veziComandaLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        comandaLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        Button veziMeniuButton = new Button("Vezi Meniu");
        Button veziComandaButton = new Button("Vezi Comanda");
        Button comandaButton = new Button("Comanda Meniu");

        veziMeniuButton.setOnAction(e -> {
            displayMealsFromDB();
        });

        veziMeniuButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        veziComandaButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        comandaButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        Image menuImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/meniuIcon.png")));
        Image orderImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/comandaIcon.png")));
        Image commandImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/proceseazaComandaIcon.png")));

        veziMeniuButton.setGraphic(new ImageView(menuImage));
        veziComandaButton.setGraphic(new ImageView(orderImage));
        comandaButton.setGraphic(new ImageView(commandImage));

        formPane.add(optionsLabel, 0, 0);
        formPane.add(veziMeniuLabel, 0, 1);
        formPane.add(veziMeniuButton, 1, 1);
        formPane.add(veziComandaLabel, 0, 2);
        formPane.add(veziComandaButton, 1, 2);
        formPane.add(comandaLabel, 0, 3);
        formPane.add(comandaButton, 1, 3);

        GridPane.setHalignment(optionsLabel, HPos.CENTER);
        GridPane.setHalignment(veziMeniuButton, HPos.CENTER);
        GridPane.setHalignment(veziComandaButton, HPos.CENTER);
        GridPane.setHalignment(comandaButton, HPos.CENTER);

        GridPane.setMargin(veziMeniuButton, new Insets(10, 0, 10, 0));
        GridPane.setMargin(veziComandaButton, new Insets(10, 0, 10, 0));
        GridPane.setMargin(comandaButton, new Insets(10, 0, 10, 0));

        formPane.setVgap(10);
    }

    private void setupTable(){
        tableView.getColumns().clear();

        TableColumn<Meal,String> nameColumn = new TableColumn<>("Name");
        TableColumn<Meal, Double> priceColumn = new TableColumn<>("Price");
        TableColumn<Meal, String> quantityColumn = new TableColumn<>("Quantity");

        nameColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Meal, Double>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("quantity"));

        tableView.getColumns().addAll(nameColumn, priceColumn, quantityColumn);

        tableView.setPrefSize(750, 400);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setPrefWidth(750);
        formPane.add(scrollPane, 0, 0);
        GridPane.setVgrow(scrollPane, Priority.ALWAYS);


    }
    private void displayTable(GridPane formPane){
        if (!formPane.getChildren().contains(tableView)) {
            formPane.add(tableView, 0, 0);
        }
        tableView.setPrefWidth(Double.MAX_VALUE);
        tableView.setPrefHeight(Double.MAX_VALUE);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        GridPane.setVgrow(tableView, Priority.ALWAYS);
    }

    private void displayTableInPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Menu Table");
        popupStage.initModality(Modality.APPLICATION_MODAL);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene popupScene = new Scene(scrollPane, 800, 600);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void displayMealsFromDB() {
        setupTable();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM meniu")) {

//            tableView.getItems().clear();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                double price = resultSet.getDouble("Price");
                String quantity = resultSet.getString("Quantity");
                System.out.println("Fetched meal: " + name + ", " + price + ", " + quantity);
                Meal meal = new Meal(name, price, quantity);
                tableView.getItems().add(meal);
                System.out.println("Current table view size: " + tableView.getItems().size());
            }
            System.out.println(tableView.getItems().size() + " rows fetched from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching data: " + e.getMessage());
        }

        Platform.runLater(this::displayTableInPopup);
        tableView.refresh();
    }



    public static void main(String[] args){
        launch(args);
    }
}
