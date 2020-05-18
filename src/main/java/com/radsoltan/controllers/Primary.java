package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.*;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.geometry.TShape;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Primary extends Controller {
    @FXML
    private TextField numberField;
    @FXML
    private ChoiceBox structureType;
    private StringProperty text;
    private TextFormatter<Double> numerical;
    private Project project;
    @FXML
    private VBox container;

    public Primary() {
        DesignParameters designParameters = new DesignParameters();
        Rectangle rectangle = new Rectangle(200, 500);
        TShape tShape = new TShape(300, 500, 150, 1000);
        SlabStrip slabStrip = new SlabStrip(300);
        Geometry geometry = new Geometry(tShape);
        Beam beam = new Beam(50, 40, 20, geometry, Concrete.C30_37, designParameters);
        project = new Project(beam);
        geometry.checksIfFlangeTakesCompressionForce();
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }


    public void test(MouseEvent mouseEvent) throws InterruptedException {
        Concrete concrete = Concrete.C12_15;
        showAlertBox(concrete.toString(), AlertKind.ERROR, 300, 60);
    }

}
