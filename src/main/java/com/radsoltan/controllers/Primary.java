package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.*;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.geometry.TShape;
import com.radsoltan.model.reinforcement.BeamReinforcement;
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

import java.util.ArrayList;
import java.util.List;

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
        List<Integer> reinforcementTop = new ArrayList<>(List.of(40, 32, 40, 25));
        List<Integer> reinforcementBottom = new ArrayList<>(List.of(40, 20, 20, 40));
        BeamReinforcement reinforcement = new BeamReinforcement(reinforcementTop, reinforcementBottom);
        System.out.println(reinforcement.calculateCentroidOfTopReinforcement(20));
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
