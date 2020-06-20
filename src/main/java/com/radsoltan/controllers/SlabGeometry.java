package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Shape;
import com.radsoltan.model.geometry.SlabStrip;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SlabGeometry extends Controller {

    @FXML
    private VBox container;
    @FXML
    public PositiveIntegerField slabThickness;

    private final Project project;
    private final Geometry geometry;

    public SlabGeometry() {
        this.project = Project.getInstance();
        this.geometry = project.getGeometry();
    }

    @FXML
    public void initialize() {
        if (geometry != null) {
            Shape shape = geometry.getShape();
            if (shape instanceof SlabStrip) {
                SlabStrip slabStrip = (SlabStrip) shape;
                slabThickness.setText(Integer.toString(slabStrip.getDepth()));
            } else {
                showAlertBox("Invalid slab geometry", AlertKind.ERROR);
            }
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        if (!slabThickness.getText().equals("")) {
            SlabStrip slabStrip = new SlabStrip(Integer.parseInt(slabThickness.getText()));
            Geometry geometry = new Geometry(slabStrip);
            project.setGeometry(geometry);
            App.setRoot("primary");
        } else {
            showAlertBox("Please define slab thickness.", AlertKind.INFO);
        }
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }
}
