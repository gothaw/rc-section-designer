package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import javafx.event.ActionEvent;

import java.io.IOException;

public class BeamGeometry {

    private final Project project;

    public BeamGeometry() {
        project = Project.getInstance();
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        Rectangle rectangle = new Rectangle(200, 500);
        Geometry geometry = new Geometry(rectangle);
        project.setGeometry(geometry);
        System.out.println("Applied");
        App.setRoot("primary");
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }
}
