package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;

public class CourseReviewsApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseDriver databaseDriver = new DatabaseDriver();
        databaseDriver.connect();
        databaseDriver.createTables();
        FXMLLoader fxmlLoader = new FXMLLoader(CourseReviewsApplication.class.getResource("LoginScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LogInSceneController controller = fxmlLoader.getController();
        controller.initialize(primaryStage, databaseDriver);
        primaryStage.setScene(scene);
        primaryStage.show();
        databaseDriver.disconnect();
    }
}
