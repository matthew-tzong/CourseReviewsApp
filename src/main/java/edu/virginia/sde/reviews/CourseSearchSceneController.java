package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CourseSearchSceneController {
    @FXML
    private Button logOutButton;
    @FXML
    private Button myReviewsButton;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField courseNumberField;
    @FXML
    private TextField courseTitleField;
    @FXML
    private Button addCourseButton;
    @FXML
    private Button searchCourseButton;
    @FXML
    private Label messageLabel;
    @FXML
    private ListView<String> courseListView;
    private DatabaseDriver databaseDriver;
    private Stage primaryStage;
    private String username;

    public CourseSearchSceneController() {}

    public void initialize(Stage primaryStage, DatabaseDriver databaseDriver, String username) throws IOException {
        this.primaryStage = primaryStage;
        this.databaseDriver = databaseDriver;
        this.username = username;
        showCourses();
        handleButtons();
        courseListView.setOnMouseClicked(event -> {
            try {
                handleSelectAction();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleButtons() {
        addCourseButton.setOnAction(e -> handleAddCourseButton());
        searchCourseButton.setOnAction(e -> handleSearchCourseButton());
        myReviewsButton.setOnAction(e -> {
            try {
                handleMyReviewsButton();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        logOutButton.setOnAction(e -> {
            try {
                handleLogOutButton();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    private void showCourses() {
        List <Course> courses = databaseDriver.getCourses();
        for (Course course : courses) {
            courseListView.getItems().add(course.toString());
        }
    }

    private void handleSelectAction() throws IOException, SQLException {
        String selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            String[] parts = selectedCourse.split("\t\t");
            String subject = parts[0];
            int number = Integer.parseInt(parts[1]);
            String course = parts[2];
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseReviewsScene.fxml"));
            Scene scene = new Scene(loader.load());
            CourseReviewsSceneController controller = loader.getController();
            controller.initialize(primaryStage, databaseDriver, username, course, subject, number);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        else {
            messageLabel.setText("Please select a class to get its reviews.");
        }
    }

    private void handleSearchCourseButton() {
        List <Course> courses = new ArrayList<>();
        int number = 0;
        String subject = subjectField.getText();
        String title = courseTitleField.getText();
        if (title.isEmpty() && subject.isEmpty() && courseNumberField.getText().isEmpty()) {
            messageLabel.setText("Please enter your search parameters");
        }
        else {
            if (!courseNumberField.getText().isEmpty())
                try {
                    number = Integer.parseInt((courseNumberField.getText()));
                } catch (NumberFormatException e) {
                    messageLabel.setText("Invalid Course Number");
                    return;
                }
            }
        if (courseTitleField.getText().isEmpty() || courseNumberField.getText().isEmpty() || subjectField.getText().isEmpty()) {
            if (!courseTitleField.getText().isEmpty()) {
                courses = databaseDriver.searchCoursesByTitle(title);
            }
            else if (!subjectField.getText().isEmpty()) {
                courses = databaseDriver.searchCoursesBySubject(subject);
            }
            else if (!courseNumberField.getText().isEmpty()) {
                courses = databaseDriver.searchCoursesByNumber(number);
            }
        }
        else {
            courses = databaseDriver.searchCourses(subject, number, title);
        }
        if (courses.isEmpty()) {
            messageLabel.setText("No courses found, double check search parameters");
            updateCourseListView(courses);
        } else {
            updateCourseListView(courses);
    }
}

    public void updateCourseListView(List<Course> courses) {
        courseListView.getItems().clear();
        for (Course course : courses) {
            courseListView.getItems().add(course.toString());
        }
    }
    private boolean validateParameters(String subject, String number, String title) {
        if (subject.length() < 2 || subject.length() > 4) {
            return false;
        }
        if (number.length() != 4) {
            return false;
        }
        return !title.isEmpty() && title.length() <= 50;
    }



    private void handleAddCourseButton() {
        String subject = subjectField.getText();
        String title = courseTitleField.getText();
        int number = 0;
        if (title.isEmpty() || subject.isEmpty() || courseNumberField.getText().isEmpty()) {
            messageLabel.setText("Please enter your add parameters");
            return;
        } try {
            number = Integer.parseInt((courseNumberField.getText()));
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid Course Number");
            return;
        }
        String numString = courseNumberField.getText();
        if (validateParameters(subject, numString, title) && databaseDriver.validateNonDuplicateCourse(subject, number, title)) {
            databaseDriver.addCourse(subject, number, title);
            List <Course> courses = databaseDriver.getCourses();
            updateCourseListView(courses);
        }
        else {
            messageLabel.setText("Invalid data format for data added or duplicate course");
        }
    }

    private void handleLogOutButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInScene.fxml"));
        Scene scene = new Scene(loader.load());
        LogInSceneController controller = loader.getController();
        controller.initialize(primaryStage, databaseDriver);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMyReviewsButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyReviewsScene.fxml"));
        Scene scene = new Scene(loader.load());
        MyReviewsSceneController controller = loader.getController();
        controller.initialize(primaryStage, databaseDriver, username);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}