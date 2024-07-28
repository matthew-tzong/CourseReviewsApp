package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReviewsSceneController {
    @FXML
    private ListView<String> reviewListView;
    @FXML
    private Button backButton;

    private DatabaseDriver databaseDriver;
    private Stage primaryStage;
    private String username;

    public void initialize(Stage primaryStage, DatabaseDriver databaseDriver, String username) {
        this.primaryStage = primaryStage;
        this.databaseDriver = databaseDriver;
        this.username = username;
        loadUserReviews();
        backButton.setOnAction(e -> goBackToCourseSearchScene());
        reviewListView.setOnMouseClicked(e -> {
            try {
                handleReviewAction();
            } catch (IOException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void handleReviewAction() throws IOException, SQLException {
        String selectedItem = reviewListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] parts = selectedItem.split("\t\t");
            String course = parts[0];
            int number = databaseDriver.getCourseNumber(course);
            String subject = databaseDriver.getSubject(course);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseReviewsScene.fxml"));
            Scene scene = new Scene(loader.load());
            CourseReviewsSceneController controller = loader.getController();
            controller.initialize(primaryStage, databaseDriver, username, course, subject, number);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    private void loadUserReviews() {
        List<Review> reviews = databaseDriver.getUserReviews(username);
        reviewListView.getItems().clear();
        for (Review review : reviews) {
            reviewListView.getItems().add(review.toString());
        }
    }

    private void goBackToCourseSearchScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearchScene.fxml"));
            Scene scene = new Scene(loader.load());
            CourseSearchSceneController controller = loader.getController();
            controller.initialize(primaryStage, databaseDriver, username);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
