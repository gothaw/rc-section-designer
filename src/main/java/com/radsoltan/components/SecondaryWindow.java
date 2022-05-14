package com.radsoltan.components;

import com.radsoltan.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class used to create a secondary window that shows up on the top of main stage.
 */
public class SecondaryWindow {
    private final Stage stage;
    private final FXMLLoader fxmlLoader;

    /**
     * Constructor.
     * It loads the fxml file. Instantiates the stage and sets its properties.
     *
     * @param width window width in pixels
     * @param height window height in pixels
     * @param title window title
     * @param fxml name of the fxml file in the resources
     */
    public SecondaryWindow(double width, double height, String title, String fxml) {
        fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        stage = new Stage();
        stage.setResizable(false);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle(title);
        stage.getIcons().add(new Image(getClass().getResource("/images/beam.png").toExternalForm()));
        // Blocking events to the other wpp windows
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(App.getStage());
    }

    /**
     * Shows the secondary window.
     *
     * @throws IOException Exception for failed or interrupted I/O operation
     */
    public void show() throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Getter for stage field.
     *
     * @return stage
     */
    public Stage getStage() {
        return stage;
    }
}
