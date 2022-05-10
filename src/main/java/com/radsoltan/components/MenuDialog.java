package com.radsoltan.components;

import com.radsoltan.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MenuDialog {
    private final Stage stage;
    private final FXMLLoader fxmlLoader;

    public MenuDialog(String fxml) {
        fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        stage = new Stage();
//        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setResizable(false);
        stage.initOwner(App.getStage());
    }

    public void show() throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
