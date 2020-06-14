package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.util.Messages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Primary extends Controller {

    @FXML
    public ChoiceBox elementType;
    @FXML
    public NumericalTextField UlsMoment;
    @FXML
    public NumericalTextField SlsMoment;
    @FXML
    public NumericalTextField shear;
    @FXML
    private VBox container;

    public Primary() {
        /*int nominalCover = 25;
        int transverseBar = 8;

        double UlsMoment = -100;
        double QslsMoment = 50;
        double shear = 60;

        Concrete concrete = Concrete.C32_40;

        List<List<Integer>> reinforcementTop = new ArrayList<>(List.of(List.of(25, 40, 32, 16, 32, 40, 25), List.of(10, 12, 10), List.of(10, 12, 10), List.of(6, 6, 6)));
        List<Integer> verticalSpacingTop = new ArrayList<>(List.of(20, 80, 100));
        List<List<Integer>> reinforcementBottom = new ArrayList<>(List.of(List.of(40, 40, 40, 40)));
        List<Integer> verticalSpacingBottom = new ArrayList<>();
        ShearLinks links = new ShearLinks(450, 10, 150, 2);
        BeamReinforcement reinforcement = new BeamReinforcement(500, reinforcementTop, verticalSpacingTop, reinforcementBottom, verticalSpacingBottom, links, 4, true);

        List<List<Integer>> rebarSimple = new ArrayList<>(List.of(List.of(32, 32, 32), List.of(25, 25), List.of(10, 10), List.of(6, 6)));
        List<Integer> verticalSpacing = new ArrayList<>(List.of(60, 80, 100));
        BeamReinforcement reinforcementSimple = new BeamReinforcement(500, rebarSimple, verticalSpacing, rebarSimple, verticalSpacing, links);

        TShape tShape = new TShape(300, 600, 1200, 200);
        Rectangle rectangle = new Rectangle(400, 800);
        Geometry geometry = new Geometry(tShape);

        DesignParameters designParameters = new DesignParameters(25, 20, 25, 1.4, 1.15, 0.85, true);

        Beam beam = new Beam(UlsMoment, shear, QslsMoment, geometry, concrete, reinforcementSimple, designParameters);
        beam.calculateBendingCapacity();

        List<List<Double>> areaOfReinforcement = reinforcement.getAreaOfReinforcementBars(reinforcementTop);
        System.out.println(reinforcementSimple.getCentroidOfTopReinforcement(designParameters.getNominalCoverTop()));
        System.out.println(reinforcementSimple.getCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom()));

        System.out.println(geometry.getShape().getSecondMomentOfArea());

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
        System.out.println(slabReinforcement.getDistanceFromCentreOfEachLayerToEdge(topReinforcement, null, vSpacingTop, 25));*/
    }

    public void calculate(ActionEvent actionEvent) {
        if (elementType.getValue() != null) {
            System.out.println("Calculate");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setDesignParameters(ActionEvent actionEvent) {
        if (elementType.getValue() != null) {
            System.out.println("Set design parameters");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setReinforcement(ActionEvent actionEvent) {
        if (elementType.getValue() != null) {
            System.out.println("Set reinforcement");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setGeometry(ActionEvent actionEvent) throws IOException {
        if (elementType.getValue() != null) {
            App.setRoot("geometry");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setElementType(ActionEvent actionEvent) {
        if(elementType.getValue().toString().toLowerCase().equals("slab")) {
            container.getStyleClass().add("slab");
            container.getStyleClass().remove("beam");
            shear.setText("");
        } else if (elementType.getValue().toString().toLowerCase().equals("beam")) {
            container.getStyleClass().add("beam");
            container.getStyleClass().remove("slab");
        }
    }
}
