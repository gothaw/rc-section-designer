package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Shape;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.util.Messages;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlabGeometrySetup extends Controller {

    @FXML
    private VBox container;
    @FXML
    public PositiveIntegerField slabThickness;

    private final Project project;
    private final Geometry geometry;

    public SlabGeometrySetup() {
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
                showAlertBox(Messages.INVALID_SLAB_GEOMETRY, AlertKind.ERROR);
            }
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessages = getValidationMessagesForEmptyFields();
        if (validationMessages.isEmpty()) {
            SlabStrip slabStrip = new SlabStrip(Integer.parseInt(slabThickness.getText()));
            Geometry geometry = new Geometry(slabStrip);
            project.setGeometry(geometry);
            App.setRoot("primary");
        } else {
            showAlertBox(validationMessages.get(0), AlertKind.INFO);
        }
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        if (slabThickness.getText().equals("")) {
            return new ArrayList<>(List.of("Please define slab thickness."));
        }
        return new ArrayList<>();
    }
}
