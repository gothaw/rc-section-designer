package com.radsoltan;

import com.radsoltan.util.Constants;

import com.radsoltan.util.UIText;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * App class that is used to launch the application.
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    /**
     * Method that runs when starting the application. It sets scene and its properties.
     *
     * @param primaryStage primary stage of the application
     * @throws Exception exception when starting the app
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creating new scene from primary .fxml file
        scene = new Scene(loadFXML("primary"), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        // Setting primary stage and its properties
        primaryStage.setScene(scene);
        primaryStage.setTitle(UIText.APP_TITLE);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/beam.png").toExternalForm()));
        primaryStage.setResizable(true);
        setMinMaxStageSize(primaryStage);
        stage = primaryStage;
        // Showing primary stage
        primaryStage.show();
    }

    /**
     * It changes the view of the scene by loading fxml file.
     *
     * @param fxml fxml file to show in the main view
     * @throws IOException Exception for failed or interrupted I/O operation
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Sets max and minimal size for a stage. It defines how much user can resize the stage.
     *
     * @param stage stage to set max and min size to
     */
    public static void setMinMaxStageSize(Stage stage) {
        stage.setMinHeight(Constants.MIN_WINDOW_HEIGHT);
        stage.setMaxHeight(Constants.MAX_WINDOW_HEIGHT);
        stage.setMinWidth(Constants.MIN_WINDOW_WIDTH);
        stage.setMaxWidth(Constants.MAX_WINDOW_WIDTH);
    }

    /**
     * It loads an fxml file using FXMLLoader.
     *
     * @param fxml fxml file name in src/resources/fxml/
     * @return fxml file
     * @throws IOException Exception for failed or interrupted I/O operation
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Getter for stage field.
     *
     * @return stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Main method. Launches application.
     *
     * @param args application args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
