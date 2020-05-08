package com.radsoltan;

import com.radsoltan.util.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFXML("primary"), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(Constants.APP_TITLE);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/beam.png").toExternalForm()));
        primaryStage.setResizable(true);
        setMinMaxStageSize(primaryStage);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void setMinMaxStageSize (Stage stage) {
        stage.setMinHeight(Constants.MIN_WINDOW_HEIGHT);
        stage.setMaxHeight(Constants.MAX_WINDOW_HEIGHT);
        stage.setMinWidth(Constants.MIN_WINDOW_WIDTH);
        stage.setMaxWidth(Constants.MAX_WINDOW_WIDTH);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
