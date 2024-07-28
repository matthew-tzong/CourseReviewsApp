package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInSceneController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button createLoginButton;
    @FXML
    private Button closeButton;
    @FXML
    private Label messageLabel;
    private DatabaseDriver databaseDriver;
    private Stage primaryStage;
    public LogInSceneController() {}
    
    public void initialize(Stage primaryStage, DatabaseDriver databaseDriver) {
        this.primaryStage = primaryStage;
        this.databaseDriver = databaseDriver;
        handleButtons();
    }
    private void handleButtons() {
        loginButton.setOnAction(e -> {
            try {
                handleLoginButton();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        createLoginButton.setOnAction(e -> handleCreateLoginButton());
        closeButton.setOnAction(e -> handleCloseButton());
    }
    @FXML
    private void handleLoginButton() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (databaseDriver.userLogin(username, password)) {
            switchToCourseSearchScene();
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }
    private void switchToCourseSearchScene() throws IOException {
        try {
            String username = usernameField.getText();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearchScene.fxml"));
            Scene scene = new Scene(loader.load());
            CourseSearchSceneController controller = loader.getController();
            controller.initialize(primaryStage, databaseDriver, username);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    @FXML
    private void handleCloseButton() {
        primaryStage.close();
    }

    @FXML
    private void handleCreateLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters long");
        } else if (username.isEmpty()) {
            messageLabel.setText("Username must not be empty");
        } else if (databaseDriver.addUser(username, password)) {
            messageLabel.setText("User has been created successfully, please log in with your credentials.");
        } else {
            messageLabel.setText("Username already exists, please choose another username");
        }
    }

}
