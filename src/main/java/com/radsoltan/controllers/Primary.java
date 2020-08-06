package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
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
            if(validationMessages.isEmpty()) {
                setProjectProperties();
                System.out.println("Ok.");
            } else {
                showAlertBox(validationMessages.get(0), AlertKind.INFO);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setDesignParameters(ActionEvent actionEvent) throws IOException {
        if (elementTypeChoiceBox.getValue() != null) {
            setProjectProperties();
            App.setRoot("design-parameters");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    public void setReinforcement(ActionEvent actionEvent) throws IOException {
        if (elementTypeChoiceBox.getValue() != null) {
            setProjectProperties();
            switch (elementTypeChoiceBox.getValue().toLowerCase()) {
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
        if (elementTypeChoiceBox.getValue() != null) {
            setProjectProperties();
            switch (elementTypeChoiceBox.getValue().toLowerCase()) {
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
