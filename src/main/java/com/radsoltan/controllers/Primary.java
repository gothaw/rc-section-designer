package com.radsoltan.controllers;

import com.radsoltan.model.*;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.TShape;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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
        int nominalCover = 25;
        int transverseBar = 8;

        /* Forces */
        double UlsMoment = -100;
        double QslsMoment = 50;
        double shear = 60;

        /* Concrete */
        Concrete concrete = Concrete.C32_40;

        /* Reinforcement */
        List<List<Integer>> reinforcementTop = new ArrayList<>(List.of(List.of(25, 40, 32, 16, 32, 40, 25), List.of(10, 12, 10), List.of(10, 12, 10), List.of(6, 6, 6)));
        List<Integer> verticalSpacingTop = new ArrayList<>(List.of(20, 80, 100));
        List<List<Integer>> reinforcementBottom = new ArrayList<>(List.of(List.of(40, 40, 40, 40)));
        List<Integer> verticalSpacingBottom = new ArrayList<>();
        ShearLinks links = new ShearLinks(450, 10, 150, 2);
        BeamReinforcement reinforcement = new BeamReinforcement(500, reinforcementTop, verticalSpacingTop, reinforcementBottom, verticalSpacingBottom, links, 4, true);

        /* Reinforcement Simple */
        List<List<Integer>> rebarSimple = new ArrayList<>(List.of(List.of(32, 32, 32), List.of(25, 25), List.of(10, 10), List.of(6, 6)));
        List<Integer> verticalSpacing = new ArrayList<>(List.of(60, 80, 100));
        BeamReinforcement reinforcementSimple = new BeamReinforcement(500, rebarSimple, verticalSpacing, rebarSimple, verticalSpacing, links);

        /* Shape */
        TShape tShape = new TShape(300, 600, 1200, 200);
        Rectangle rectangle = new Rectangle(400, 800);
        Geometry geometry = new Geometry(tShape);

        /* Design Parameters */
        DesignParameters designParameters = new DesignParameters(25, 20, 25, 1.4, 1.15, 0.85, true);

        Beam beam = new Beam(UlsMoment, shear, QslsMoment, geometry, concrete, reinforcementSimple, designParameters);
        beam.calculateBendingCapacity();

        List<List<Double>> areaOfReinforcement = reinforcement.getAreaOfReinforcementBars(reinforcementTop);
        System.out.println(reinforcementSimple.getCentroidOfTopReinforcement(designParameters.getNominalCoverTop()));
        System.out.println(reinforcementSimple.getCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom()));

        System.out.println(geometry.getShape().getSecondMomentOfArea());

        /* Slab Example */

        List<Integer> topReinforcement = List.of(20, 16, 10);
        List<Integer> additionalTopReinforcement = List.of(20, 12);
        List<Integer> spacingTop = List.of(400, 400, 200);
        List<Integer> vSpacingTop = List.of(50, 50);

        List<Integer> bottomReinforcement = List.of(20, 8, 8);
        List<Integer> additionalBottomReinforcement = List.of(20, 12);
        List<Integer> spacingBottom = List.of(300, 300, 150);
        List<Integer> vSpacingBottom = List.of(50, 50);

        SlabReinforcement slabReinforcement = new SlabReinforcement(500,
                topReinforcement, additionalTopReinforcement, spacingTop, vSpacingTop,
                bottomReinforcement, additionalBottomReinforcement, spacingBottom, vSpacingBottom, 10);

        System.out.println(slabReinforcement.getAreaOfReinforcementLayers(topReinforcement, additionalTopReinforcement, spacingTop));
        System.out.println(slabReinforcement.getDistanceFromCentreOfEachLayerToEdge(topReinforcement, null, vSpacingTop, 25));
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
