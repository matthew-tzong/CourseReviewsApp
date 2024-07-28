package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class CourseReviewsSceneController {
    @FXML
    private Label courseTitle;
    @FXML
    private Button addReviewButton;
    @FXML
    private Button deleteReviewButton;
    @FXML
    private Button editReviewButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField ratingInput;
    @FXML
    private TextField commentInput;

    private DatabaseDriver databaseDriver;
    private Stage primaryStage;
    @FXML
    private Label courseRating;
    @FXML
    private Label courseSubject;
    @FXML
    private Label courseNumber;
    @FXML
    private ListView<String> reviewListView;
    @FXML
    private Label messageLabel;
    private String username;
    private String course;
    private float rating;
    private String subject;
    private int number;
    public CourseReviewsSceneController(){}

    public void initialize(Stage primaryStage, DatabaseDriver databaseDriver, String username, String course, String subject, int number) throws SQLException {
        this.primaryStage = primaryStage;
        this.databaseDriver = databaseDriver;
        this.username = username;
        this.course = course;
        float averageRating = databaseDriver.getAverageRating(course, subject, number);
        this.rating = Math.round(averageRating * 100.0f) / 100.0f;
        this.subject = subject;
        this.number = number;
        showReviews();
        handleButtons();
    }

    private void handleButtons() {
        addReviewButton.setOnAction(e -> {
            try {
                handleAddReviewButton();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteReviewButton.setOnAction(e-> {
            try {
                handleDeleteReviewButton();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        editReviewButton.setOnAction(e-> {
            try {
                handleEditReviewButton();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        backButton.setOnAction(e-> handleBackButton());
    }

    private void showReviews() throws SQLException {
        courseTitle.setText(course);
        courseRating.setText(String.valueOf(Math.round(databaseDriver.getAverageRating(course, subject, number) * 100.0f) / 100.0f));
        courseNumber.setText(String.valueOf(number));
        courseSubject.setText(subject);
        List<Review> reviews = databaseDriver.getCourseReviews(course, subject, number);
        for (Review review : reviews) {
            reviewListView.getItems().add(review.toString());
        }
    }

    private void handleAddReviewButton() throws SQLException {
        int rating = 0;
        try {
            rating = Integer.parseInt(ratingInput.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Please choose a whole number between 1 and 5");
            return;
        }
        if (rating > 5 || rating < 1) {
            messageLabel.setText("Please choose a rating between 1 and 5");
            return;
        }
        if (!databaseDriver.validateUserAddReview(username, course, number, subject)) {
            messageLabel.setText("User has already reviewed the course.");
        }
        else {
            if (!commentInput.getText().isEmpty()) {
                String comment = commentInput.getText();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Review newReview = new Review(username, course, rating, comment, timestamp,subject,number);
                databaseDriver.addReview(newReview);
                List<Review> reviews = databaseDriver.getCourseReviews(course,subject,number);
                updateReviewListView(reviews);
            } else {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String comment = "";
                Review newReview = new Review(username, course, rating, comment, timestamp,subject,number);
                databaseDriver.addReview(newReview);
                List<Review> reviews = databaseDriver.getCourseReviews(course,subject,number);
                updateReviewListView(reviews);
            }
        }
    }

    public void updateReviewListView(List<Review> reviews) throws SQLException {
        courseTitle.setText(course);
        courseRating.setText(String.valueOf(Math.round(databaseDriver.getAverageRating(course, subject, number) * 100.0f) / 100.0f));
        courseNumber.setText(String.valueOf(number));
        courseSubject.setText(subject);
        reviewListView.getItems().clear();
        for (Review review : reviews) {
            reviewListView.getItems().add(review.toString());
        }
    }
    private void handleDeleteReviewButton() throws SQLException {
        String comment = "";
        String selectedReview = reviewListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            if (!ratingInput.getText().isEmpty() || !commentInput.getText().isEmpty()) {
                messageLabel.setText("Remove your parameters first");
                return;
            }
            String[] parts = selectedReview.split("\t\t");
            String ratingStr = parts[1];
            int rating = Integer.parseInt(ratingStr);
            String timestampStr = parts[2];
            if (parts.length > 3) {
                comment = parts[3];
            }
            Timestamp timestamp = Timestamp.valueOf(timestampStr);
            Review reviewToDelete = new Review(username, course, rating, comment, timestamp,subject,number);
            if (ratingInput.getText().isEmpty()) {
                databaseDriver.deleteReview(reviewToDelete);
                List<Review> reviews = databaseDriver.getCourseReviews(course,subject,number);
                updateReviewListView(reviews);
            }
        }
        else {
            messageLabel.setText("Please select your own review to delete.");
        }
    }
    private void handleEditReviewButton() throws SQLException {
        String comment = "";
        String selectedReview = reviewListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            String[] parts = selectedReview.split("\t\t");
            String ratingStr = parts[1];
            int rating = Integer.parseInt(ratingStr);
            String timestampStr = parts[2];
            if (!databaseDriver.validateUserReview(username, timestampStr)) {
                messageLabel.setText("Please choose your own review");
                return;
            }
            if (parts.length > 3) {
                comment = parts[3];
            }
            Timestamp timestamp = Timestamp.valueOf(timestampStr);
            Review reviewToEdit = new Review(username, course, rating, comment, timestamp,subject,number);
            if (ratingInput.getText().isEmpty()) {
                messageLabel.setText("Please enter a new rating and/or comment");
            } else {
                handleEditReviewAction(reviewToEdit);
            }
        }
            else {
                messageLabel.setText("Please select one of your reviews to edit.");
            }
    }
    private void handleEditReviewAction(Review reviewToEdit) throws SQLException {
        int newRating = Integer.parseInt(ratingInput.getText());
        if (rating > 5 || rating < 1) {
            messageLabel.setText("Please choose a rating between 1 and 5");
            return;
        }
        String newComment = "";
        if (!commentInput.getText().isEmpty()) {
            newComment = commentInput.getText();
        }
        reviewToEdit.setRating(newRating);
        reviewToEdit.setComment(newComment);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reviewToEdit.setTimestamp(timestamp);
        databaseDriver.editReview(reviewToEdit);
        List<Review> reviews = databaseDriver.getCourseReviews(course,subject,number);
        updateReviewListView(reviews);
        messageLabel.setText("");
    }
    private void handleBackButton() {
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
