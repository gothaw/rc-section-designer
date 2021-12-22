package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Concrete;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to main view of the application. This includes handling logic for:
 * - setting element type
 * - setting project information
 * - specifying analysis forces
 * - redirecting to design parameters, geometry and reinforcement controllers
 * - running project calculations
 */
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
    @FXML
    private HBox UlsMomentWrapper;
    @FXML
    private NumericalTextField SlsMoment;
    @FXML
    private HBox SlsMomentWrapper;
    @FXML
    private NumericalTextField UlsShear;
    @FXML
    private HBox UlsShearWrapper;
    @FXML
    private ChoiceBox<String> elementTypeChoiceBox;

    private final Project project;

    /**
     * Constructor. Gets project instance.
     */
    public Primary() {
        project = Project.getInstance();
    }

    /**
     * Initialize method that is run after calling the constructor.
     * It sets up input fields for project details and analysis forces to values set up in the project instance.
     * It also sets description and styling for geometry, reinforcement and design parameters sections.
     */
    @FXML
    public void initialize() {
        // Setting project details
        projectName.setText(project.getName());
        if (project.getId() != null) {
            projectNumber.setText(project.getId());
        }
        projectDescription.setText(project.getDescription());
        projectAuthor.setText(project.getAuthor());

        UlsMomentWrapper = (HBox) UlsMoment.getParent();
        SlsMomentWrapper = (HBox) SlsMoment.getParent();
        UlsShearWrapper = (HBox) UlsShear.getParent();

        // Setting element type
        elementTypeChoiceBox.setValue(Utility.capitalize(project.getElementType()));
        // Setting analysis forces
        if (project.getUlsMoment() != null) {
            UlsMoment.setText(project.getUlsMoment());
        }
        if (project.getSlsMoment() != null) {
            SlsMoment.setText(project.getSlsMoment());
        }
        if (project.getUlsShear() != null) {
            UlsShear.setText(project.getUlsShear());
        }
        // Setting geometry, reinforcement and design parameters sections
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

    /**
     * Method that handles calculate button click.
     * <p>
     * Following things are checked before calculations are run:
     * - element type needs to be set up
     * - all necessary project properties must by set up (geometry, reinforcement, forces etc.) this is carried out using
     * getValidationMessagesForEmptyFields method.
     * - project properties must be valid. This is carried out by calling getValidationMessagesBasedOnElementType method
     * If any of these conditions are not met, an alert box is shown to the user
     * <p>
     * Project is calculated and then results for flexure, shear and cracking are shown in results area.
     *
     * @param actionEvent Calculate button click event.
     */
    public void calculate(ActionEvent actionEvent) {
        // Checking if element type is set up
        if (elementTypeChoiceBox.getValue() != null) {
            List<String> validationMessages = getValidationMessagesForEmptyFields();
            // Checking if project properties are set
            if (validationMessages.isEmpty()) {
                setProjectProperties();
                clearResultsArea();
                List<String> elementValidationMessages = getValidationMessagesBasedOnElementType(project.getElementType());
                // Checking if project properties are valid
                if (elementValidationMessages.isEmpty()) {
                    try {
                        project.calculate();
                        designResultsWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
                    } catch (IllegalArgumentException e) {
                        showAlertBox(e.getMessage(), AlertKind.ERROR, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
                    }
                    if (project.getFlexureCapacityCheckMessage() != null) {
                        VBox flexureResults = generateResultsArea(Math.abs(Double.parseDouble(project.getUlsMoment())), project.getFlexureCapacity(), Messages.FLEXURE, project.getFlexureCapacityCheckMessage(), project.getFlexureResultsAdditionalMessage());
                        flexureResultsWrapper.getChildren().add(flexureResults);
                    }
                    if (project.getShearCapacityCheckMessage() != null) {
                        VBox shearResults = generateResultsArea(Math.abs(Double.parseDouble(project.getUlsShear())), project.getShearCapacity(), Messages.SHEAR, project.getShearCapacityCheckMessage(), project.getShearResultsAdditionalMessage());
                        shearResultsWrapper.getChildren().add(shearResults);
                    }
                    if (project.getCrackingCheckMessage() != null) {
                        // TODO: 13/08/2020 Implement cracking calcs
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

    /**
     * Method that handles edit button for Design Parameters.
     * It redirects to controller that handles setting up design parameters.
     *
     * @param actionEvent Edit button click event.
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void setDesignParameters(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            // Saving project properties before redirect
            setProjectProperties();
            App.setRoot("design-parameters");
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    /**
     * Method that handles edit button for Reinforcement.
     * It redirects to controller that handles setting up reinforcement. This depends on element type i.e. slab or beam.
     *
     * @param actionEvent Edit button click event.
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void setReinforcement(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            // Saving project properties before redirect
            setProjectProperties();
            switch (project.getElementType().toLowerCase()) {
                case Constants.ELEMENT_TYPE_SLAB:
                    App.setRoot("slab-reinforcement");
                    break;
                case Constants.ELEMENT_TYPE_BEAM:
                    App.setRoot("beam-reinforcement");
                    break;
                default:
                    showAlertBox(Messages.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    /**
     * Method that handles edit button for Geometry.
     * It redirects to controller that handles setting up geometry. This depends on element type i.e. slab or beam.
     *
     * @param actionEvent Edit button click event.
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void setGeometry(ActionEvent actionEvent) throws IOException {
        if (project.getElementType() != null) {
            // Saving project properties before redirect
            setProjectProperties();
            switch (project.getElementType().toLowerCase()) {
                case Constants.ELEMENT_TYPE_SLAB:
                    App.setRoot("slab-geometry");
                    break;
                case Constants.ELEMENT_TYPE_BEAM:
                    App.setRoot("beam-geometry");
                    break;
                default:
                    showAlertBox(Messages.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(Messages.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
        }
    }

    /**
     * Method that handles drop down list for the element type.
     * It sets the element type, handles what units should be shown next analysis forces and whether or not shear input field for shear should be shown.
     * When switching between element types, method resets some of the project properties. This includes geometry, reinforcement, design parameters.
     *
     * @param actionEvent Dropdown list click event
     */
    public void setElementTypeChoiceBox(ActionEvent actionEvent) {
        String elementType = elementTypeChoiceBox.getValue().toLowerCase();
        if (elementType.equals(Constants.ELEMENT_TYPE_SLAB)) {
            UlsShear.setText("0");
            project.setUlsShear(null);
            if (UlsShearWrapper.getStyleClass().toString().isEmpty()) {
                UlsShearWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
            }
            setMomentsUnit(Messages.UNIT_MOMENT_SLAB);
        } else if (elementType.equals(Constants.ELEMENT_TYPE_BEAM)) {
            UlsShear.setText("");
            UlsShearWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
            setMomentsUnit(Messages.UNIT_MOMENT_BEAM);
        }
        if (project.getElementType() != null && !project.getElementType().equals(elementType)) {
            // Resetting when switching between element types
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

    /**
     * Sets project properties. It takes values from the input fields and sets them in project instance properties.
     */
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

    /**
     * Sets units for moment labels next to ULS and QLS
     *
     * @param unit Units for moment. Either 'kNm' or 'kNm/m'
     */
    private void setMomentsUnit(String unit) {
        Label UlsMomentUnits = (Label) UlsMomentWrapper.lookup("." + CssStyleClasses.UNIT_LABEL);
        UlsMomentUnits.setText(unit);
        Label SlsMomentUnits = (Label) SlsMomentWrapper.lookup("." + CssStyleClasses.UNIT_LABEL);
        SlsMomentUnits.setText(unit);
    }

    /**
     * It creates a wrapper VBox that includes a title and an inner VBox with results.
     * The inner VBox includes:
     * - capacity note with a pass and fail text, for example: '12 kNm < 15 kNm Pass'
     * - additional message, which can include info why design is failing
     *
     * @param designValue       Design value of the analysis force/moment
     * @param maxValue          Design capacity
     * @param title             Title for the analysis result area, for example, 'Flexure', 'Shear' etc.
     * @param capacityMessage   Message that that includes comparison of designValue and maxValue. for instance: '12 kNm < 15 kNm'
     * @param additionalMessage Additional message for the user if section fails. For example: 'Increase reinforcement or redesign section.'
     * @return wrapper VBox
     */
    private VBox generateResultsArea(double designValue, double maxValue, String title, String capacityMessage, String additionalMessage) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add(CssStyleClasses.SUBHEADING);
        Label capacityLabel = new Label(capacityMessage);
        Label passFailLabel = new Label((designValue <= maxValue) ? Messages.PASS : Messages.FAIL);
        passFailLabel.getStyleClass().add((designValue <= maxValue) ? CssStyleClasses.PASS : CssStyleClasses.FAIL);
        HBox utilizationNoteWrapper = new HBox(capacityLabel, passFailLabel);
        utilizationNoteWrapper.getStyleClass().add(CssStyleClasses.UTILIZATION_WRAPPER);
        Label additionalMessageLabel = new Label(additionalMessage);
        additionalMessageLabel.getStyleClass().add(CssStyleClasses.RESULTS_MESSAGE);
        VBox results = new VBox(utilizationNoteWrapper, additionalMessageLabel);
        results.getStyleClass().add(CssStyleClasses.RESULTS);
        return new VBox(titleLabel, results);
    }

    /**
     * Gets validation messages for element type. This is carried out by creating an instance of ValidateSlab or ValidateBeam.
     * These include validation messages for geometry, reinforcement spacing or design parameters.
     *
     * @param elementType structure element type. This can be either 'beam' or 'slab'
     * @return List of validation messages based on element type
     */
    private List<String> getValidationMessagesBasedOnElementType(String elementType) {
        List<String> elementValidationMessages = new ArrayList<>();
        switch (elementType) {
            case Constants.ELEMENT_TYPE_SLAB:
                if (project.getReinforcement() instanceof SlabReinforcement) {
                    ValidateSlab validateSlab = new ValidateSlab(project.getGeometry().getDepth(), (SlabReinforcement) project.getReinforcement(), project.getDesignParameters());
                    elementValidationMessages.addAll(validateSlab.getValidationMessages());
                }
                break;
            case Constants.ELEMENT_TYPE_BEAM:
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

    /**
     * Method that clears and hides the results area.
     */
    private void clearResultsArea() {
        if (!designResultsWrapper.getStyleClass().contains(CssStyleClasses.HIDDEN)) {
            designResultsWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
        flexureResultsWrapper.getChildren().clear();
        shearResultsWrapper.getChildren().clear();
        crackingResultsWrapper.getChildren().clear();
    }

    /**
     * Gets a list of validation messages if analysis forces, geometry, reinforcement and design parameters are not set up.
     *
     * @return List of validation messages for empty fields
     */
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
