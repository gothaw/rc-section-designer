package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.ValidateBeam;
import com.radsoltan.model.ValidateSlab;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import com.radsoltan.util.CssStyleClasses;
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
import java.util.ArrayList;
import java.util.List;

public class Primary extends Controller {

    @FXML
    public VBox designResultsWrapper;
    @FXML
    public VBox flexureResultsWrapper;
    @FXML
    public VBox shearResultsWrapper;
    @FXML
    public VBox crackingResultsWrapper;
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
    private HBox UlsMomentWrapper;
    @FXML
    private NumericalTextField SlsMoment;
    private HBox SlsMomentWrapper;
    @FXML
    private NumericalTextField UlsShear;
    private HBox UlsShearWrapper;
    @FXML
    private ChoiceBox<String> elementTypeChoiceBox;

    private final Project project;

    public Primary() {
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

        UlsMomentWrapper = (HBox) UlsMoment.getParent();
        SlsMomentWrapper = (HBox) SlsMoment.getParent();
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
            geometrySection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
        } else {
            geometryText.setText(project.getGeometry().getDescription());
        }
        if (project.getReinforcement() == null) {
            reinforcementSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
        } else {
            reinforcementText.setText(project.getReinforcement().getDescription());
        }
        if (project.getDesignParameters() == null) {
            designParametersSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void calculate(ActionEvent actionEvent) {
        if (elementTypeChoiceBox.getValue() != null) {
            List<String> validationMessages = getValidationMessagesForEmptyFields();
            if (validationMessages.isEmpty()) {
                setProjectProperties();
                clearResultsArea();
                List<String> elementValidationMessages = getValidationMessagesBasedOnElementType(project.getElementType());
                if (elementValidationMessages.isEmpty()) {
                    try {
                        project.calculate();
                        designResultsWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
                    } catch (IllegalArgumentException e) {
                        showAlertBox(e.getMessage(), AlertKind.ERROR, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
                    }
                    if (project.getFlexureCapacityCheckMessage() != null) {
                        VBox flexureResults = generateResultsArea(Math.abs(Double.parseDouble(project.getUlsMoment())), project.getFlexureCapacity(), "Flexure", project.getFlexureCapacityCheckMessage(), project.getFlexureResultsAdditionalMessage());
                        flexureResultsWrapper.getChildren().add(flexureResults);
                    }
                    if (project.getShearCapacityCheckMessage() != null) {
                        VBox shearResults = generateResultsArea(Math.abs(Double.parseDouble(project.getUlsShear())), project.getShearCapacity(), "Shear", project.getShearCapacityCheckMessage(), project.getShearResultsAdditionalMessage());
                        shearResultsWrapper.getChildren().add(shearResults);
                    }
                    if (project.getCrackingCheckMessage() != null) {
                        // TODO: 13/08/2020 Implement
                    }
                } else {
                    showAlertBox(elementValidationMessages.get(0), AlertKind.ERROR, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
                }
            } else {
                showAlertBox(validationMessages.get(0), AlertKind.INFO);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setDesignParameters(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            setProjectProperties();
            App.setRoot("design-parameters");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setReinforcement(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            setProjectProperties();
            switch (project.getElementType().toLowerCase()) {
                case "slab":
                    App.setRoot("slab-reinforcement");
                    break;
                case "beam":
                    App.setRoot("beam-reinforcement");
                    break;
                default:
                    showAlertBox(Messages.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setGeometry(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            setProjectProperties();
            switch (project.getElementType().toLowerCase()) {
                case "slab":
                    App.setRoot("slab-geometry");
                    break;
                case "beam":
                    App.setRoot("beam-geometry");
                    break;
                default:
                    showAlertBox(Messages.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setElementTypeChoiceBox(ActionEvent actionEvent) {
        String elementType = elementTypeChoiceBox.getValue().toLowerCase();
        if (elementType.equals("slab")) {
            UlsShear.setText("0");
            project.setUlsShear(null);
            if (UlsShearWrapper.getStyleClass().toString().isEmpty()) {
                UlsShearWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
            }
            setMomentsUnit("kNm/m");
        } else if (elementType.equals("beam")) {
            UlsShear.setText("");
            UlsShearWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
            setMomentsUnit("kNm");
        }
        if (project.getElementType() != null && !project.getElementType().equals(elementType)) {
            project.setGeometry(null);
            project.setReinforcement(null);
            project.setDesignParameters(null);
            project.setConcrete(null);
            geometrySection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
            geometryText.setText(Messages.ENTER_GEOMETRY);
            reinforcementSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
            reinforcementText.setText(Messages.ENTER_REINFORCEMENT);
            designParametersSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
            clearResultsArea();
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
        Label UlsMomentUnits = (Label) UlsMomentWrapper.lookup("." + CssStyleClasses.UNIT_LABEL);
        UlsMomentUnits.setText(unit);
        Label SlsMomentUnits = (Label) SlsMomentWrapper.lookup("." + CssStyleClasses.UNIT_LABEL);
        SlsMomentUnits.setText(unit);
    }

    private VBox generateResultsArea(double designValue, double maxValue, String title, String capacityMessage, String additionalMessage) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add(CssStyleClasses.SUBHEADING);
        Label capacityLabel = new Label(capacityMessage);
        Label passFailLabel = new Label((designValue <= maxValue) ? "Pass" : "Fail");
        passFailLabel.getStyleClass().add((designValue <= maxValue) ? CssStyleClasses.PASS : CssStyleClasses.FAIL);
        HBox utilizationNoteWrapper = new HBox(capacityLabel, passFailLabel);
        utilizationNoteWrapper.getStyleClass().add(CssStyleClasses.UTILIZATION_WRAPPER);
        Label additionalMessageLabel = new Label(additionalMessage);
        additionalMessageLabel.getStyleClass().add(CssStyleClasses.RESULTS_MESSAGE);
        VBox results = new VBox(utilizationNoteWrapper, additionalMessageLabel);
        results.getStyleClass().add(CssStyleClasses.RESULTS);
        return new VBox(titleLabel, results);
    }


    private List<String> getValidationMessagesBasedOnElementType(String elementType) {
        List<String> elementValidationMessages = new ArrayList<>();
        switch (elementType) {
            case "slab":
                if (project.getReinforcement() instanceof SlabReinforcement) {
                    ValidateSlab validateSlab = new ValidateSlab(project.getGeometry().getDepth(), (SlabReinforcement) project.getReinforcement(), project.getDesignParameters());
                    elementValidationMessages.addAll(validateSlab.getValidationMessages());
                }
                break;
            case "beam":
                if (project.getReinforcement() instanceof BeamReinforcement) {
                    ValidateBeam validateBeam = new ValidateBeam(project.getGeometry(), (BeamReinforcement) project.getReinforcement(), project.getDesignParameters());
                    elementValidationMessages.addAll(validateBeam.getValidationMessages());
                }
                break;
            default:
                elementValidationMessages.add(Messages.INVALID_ELEMENT_TYPE);
        }
        return elementValidationMessages;
    }

    private void clearResultsArea() {
        if (!designResultsWrapper.getStyleClass().contains(CssStyleClasses.HIDDEN)) {
            designResultsWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
        flexureResultsWrapper.getChildren().clear();
        shearResultsWrapper.getChildren().clear();
        crackingResultsWrapper.getChildren().clear();
    }


    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        List<String> validationMessages = new ArrayList<>();
        if (UlsMoment.getText().isEmpty() || SlsMoment.getText().isEmpty() || UlsShear.getText().isEmpty()) {
            validationMessages.add(Messages.SETUP_ANALYSIS_FORCES);
        }
        if (project.getGeometry() == null) {
            validationMessages.add(Messages.SETUP_GEOMETRY);
        }
        if (project.getReinforcement() == null) {
            validationMessages.add(Messages.SETUP_REINFORCEMENT);
        }
        if (project.getDesignParameters() == null) {
            validationMessages.add(Messages.SETUP_DESIGN_PARAMETERS);
        }
        return validationMessages;
    }
}
