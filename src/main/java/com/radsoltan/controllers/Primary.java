package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.util.Messages;
import com.radsoltan.util.Utility;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Primary extends Controller {

    @FXML
    private VBox container;
    @FXML
    private VBox geometrySection;
    @FXML
    public Label geometryText;
    @FXML
    public VBox reinforcementSection;
    @FXML
    public Label reinforcementText;
    @FXML
    public AnchorPane designParametersSection;
    @FXML
    private PositiveIntegerField projectNumber;
    @FXML
    private TextField projectName;
    @FXML
    private TextField projectDescription;
    @FXML
    private TextField projectAuthor;
    @FXML
    private VBox forcesSection;
    @FXML
    private NumericalTextField UlsMoment;
    @FXML
    private NumericalTextField SlsMoment;
    @FXML
    private NumericalTextField UlsShear;
    private HBox UlsShearWrapper;
    @FXML
    private ChoiceBox<String> elementTypeChoiceBox;

    private final Project project;

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
        project = Project.getInstance();
    }

    @FXML
    public void initialize() {
        projectName.setText(project.getName());
        if (project.getId() != null) {
            projectNumber.setText(project.getId());
        }
        projectDescription.setText(project.getDescription());
        projectAuthor.setText(project.getAuthor());
        UlsShearWrapper = (HBox) UlsShear.getParent();
        elementTypeChoiceBox.setValue(Utility.capitalize(project.getElementType()));
        if (project.getUlsMoment() != null) {
            UlsMoment.setText(project.getUlsMoment());
        }
        if (project.getSlsMoment() != null) {
            SlsMoment.setText(project.getSlsMoment());
        }
        if (project.getUlsShear() != null) {
            UlsShear.setText(project.getUlsShear());
        }
        if (project.getGeometry() == null) {
            geometrySection.getStyleClass().add("not-defined");
        }
        if (project.getReinforcement() == null) {
            reinforcementSection.getStyleClass().add("not-defined");
        }
        if (project.getDesignParameters() == null) {
            designParametersSection.getStyleClass().add("not-defined");
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void calculate(ActionEvent actionEvent) {
        if (elementTypeChoiceBox.getValue() != null) {

            setProjectProperties();
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setDesignParameters(ActionEvent actionEvent) {
        if (elementTypeChoiceBox.getValue() != null) {
            System.out.println("Set design parameters");
            setProjectProperties();
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setReinforcement(ActionEvent actionEvent) {
        if (elementTypeChoiceBox.getValue() != null) {
            System.out.println("Set reinforcement");
            setProjectProperties();
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setGeometry(ActionEvent actionEvent) throws IOException {
        if (elementTypeChoiceBox.getValue() != null) {
            setProjectProperties();
            switch (elementTypeChoiceBox.getValue().toLowerCase()) {
                case "slab":
                    App.setRoot("geometry-slab");
                    break;
                case "beam":
                    App.setRoot("geometry-beam");
                    break;
                default:
                    showAlertBox("Wrong element type.", AlertKind.ERROR);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setElementTypeChoiceBox(ActionEvent actionEvent) {
        String elementType = elementTypeChoiceBox.getValue().toLowerCase();
        if (elementType.equals("slab")) {
            UlsShear.setText("");
            project.setUlsShear(null);
            if (UlsShearWrapper.getStyleClass().toString().isEmpty()) {
                UlsShearWrapper.getStyleClass().add("hidden");
            }
            setMomentsUnit("kNm/m");
        } else if (elementType.equals("beam")) {
            UlsShearWrapper.getStyleClass().remove("hidden");
            setMomentsUnit("kNm");
        }
        if (project.getElementType() != null && !project.getElementType().equals(elementType)) {
            project.setGeometry(null);
            project.setReinforcement(null);
            project.setDesignParameters(null);
            geometrySection.getStyleClass().add("not-defined");
            reinforcementSection.getStyleClass().add("not-defined");
            designParametersSection.getStyleClass().add("not-defined");
        }
        project.setElementType(elementType);
    }

    private void setProjectProperties() {
        project.setName(projectName.getText());
        project.setId(projectNumber.getText());
        project.setDescription(projectDescription.getText());
        project.setAuthor(projectAuthor.getText());
        project.setUlsMoment(UlsMoment.getText());
        project.setSlsMoment(SlsMoment.getText());
        project.setUlsShear(UlsShear.getText());
        project.setElementType(elementTypeChoiceBox.getValue().toLowerCase());
    }

    private void setMomentsUnit(String unit) {
        HBox UlsMomentWrapper = (HBox) forcesSection.getChildren().get(0);
        Label UlsMomentUnits = (Label) UlsMomentWrapper.getChildren().get(2);
        UlsMomentUnits.setText(unit);
        HBox SlsMomentWrapper = (HBox) forcesSection.getChildren().get(1);
        Label SlsMomentUnits = (Label) SlsMomentWrapper.getChildren().get(2);
        SlsMomentUnits.setText(unit);
    }
}
