package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.CssStyleClasses;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.Project;
import com.radsoltan.model.ValidateBeam;
import com.radsoltan.model.ValidateSlab;
import com.radsoltan.model.geometry.*;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
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
    public Canvas elementImage;
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

    private static final double SLAB_IMAGE_HORIZONTAL_RATIO = 0.75;
    private static final double SLAB_IMAGE_DIMENSION_LINES_SCALE = 0.5;
    private static final int MAX_SLAB_THICKNESS_WHEN_DRAWING = 500;

    private static final double BEAM_IMAGE_MAX_HORIZONTAL_RATIO = 0.5;
    private static final double BEAM_IMAGE_MAX_VERTICAL_RATIO = 0.75;
    private static final double BEAM_IMAGE_RATIO_REDUCTION_STEP = 0.05;

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
        // Drawing main image
        this.drawElementImage();

        generateResultsArea();

        Platform.runLater(() -> {
            addEventHandlersForTopMenu();
            addEventListenersForInputFields();
            container.requestFocus();
        });
    }

    /**
     * Method adds event handlers for top menu. This handles creating new project, opening file, saving.
     * It also handles about and help menu items. The events are added to main stage of the app.
     */
    private void addEventHandlersForTopMenu() {
        App.getStage().addEventHandler(newFileEvent, event -> this.resetProject());
        App.getStage().addEventHandler(saveFileEvent, this::saveProjectToFile);
        App.getStage().addEventHandler(saveAsFileEvent, this::saveProjectToFile);
        App.getStage().addEventHandler(openFileEvent, this::openProjectFromFile);
    }

    /**
     * It handles saving project to a file. It requires passing a FileEvent as a parameter.
     * If that is the case, it invokes static method on ProjectFile class to save the project to the file.
     *
     * @param event save as file event
     */
    private void saveProjectToFile(Event event) {
        try {
            File file = ((FileEvent) event).getFile();

            if (file == null) {
                throw new IOException(UIText.SOMETHING_WENT_WRONG);
            }

            // Setting project properties from input fields before saving
            setProjectPropertiesFromInputFields();
            ProjectFile.save(file, project);
        } catch (IOException e) {
            showAlertBox(UIText.SOMETHING_WENT_WRONG, AlertKind.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * It handles opening project from a file. It requires passing a FileEvent as a parameter.
     * If that is the case, it invokes static method on ProjectFile class to open the file and sets up project instance fields.
     * It also redirects to primary view.
     *
     * @param event open file event
     */
    private void openProjectFromFile(Event event) {
        try {
            File file = ((FileEvent) event).getFile();

            if (file == null) {
                throw new IOException(UIText.SOMETHING_WENT_WRONG);
            }

            Project projectFromFile = ProjectFile.open(file);

            // Setting project details
            project.setName(projectFromFile.getName());
            project.setId(projectFromFile.getId());
            project.setDescription(projectFromFile.getDescription());
            project.setAuthor(projectFromFile.getAuthor());
            // Setting analysis forces
            project.setUlsMoment(projectFromFile.getUlsMoment());
            project.setSlsMoment(projectFromFile.getSlsMoment());
            project.setUlsShear(projectFromFile.getUlsShear());
            // Setting element type
            project.setElementType(projectFromFile.getElementType());
            // Setting geometry
            project.setGeometry(projectFromFile.getGeometry());
            // Setting reinforcement
            project.setReinforcement(projectFromFile.getReinforcement());
            // Setting design parameters
            project.setDesignParameters(projectFromFile.getDesignParameters());
            // Setting concrete
            project.setConcrete(projectFromFile.getConcrete());
            // Setting Results
            // Flexure
            project.setFlexureCapacity(projectFromFile.getFlexureCapacity());
            project.setFlexureCapacityCheckMessage(projectFromFile.getFlexureCapacityCheckMessage());
            project.setFlexureResultsAdditionalMessage(projectFromFile.getFlexureResultsAdditionalMessage());
            project.setFlexureError(projectFromFile.getIsFlexureError());
            // Shear
            project.setShearCapacity(projectFromFile.getShearCapacity());
            project.setShearCapacityCheckMessage(projectFromFile.getShearCapacityCheckMessage());
            project.setShearResultsAdditionalMessage(projectFromFile.getShearResultsAdditionalMessage());
            project.setShearError(projectFromFile.getIsShearError());
            // Cracking
            project.setCrackWidth(projectFromFile.getCrackWidth());
            project.setCrackWidthLimit(projectFromFile.getCrackWidthLimit());
            project.setCrackingCheckMessage(projectFromFile.getCrackingCheckMessage());
            project.setCrackingResultsAdditionalMessage(projectFromFile.getCrackingResultsAdditionalMessage());
            project.setCrackingError(projectFromFile.getIsCrackingError());

            // Redirecting to primary - this handles initializing primary view
            App.setRoot("primary");

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            showAlertBox(UIText.SOMETHING_WENT_WRONG, AlertKind.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Method that event listeners for forces input fields. These clear results area if user changes analysis forces.
     */
    private void addEventListenersForInputFields() {
        UlsMoment.textProperty().addListener((observable, oldValue, newValue) -> clearResultsArea());
        SlsMoment.textProperty().addListener((observable, oldValue, newValue) -> clearResultsArea());
        UlsShear.textProperty().addListener((observable, oldValue, newValue) -> clearResultsArea());
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
                setProjectPropertiesFromInputFields();
                clearResultsArea();
                List<String> elementValidationMessages = getValidationMessagesBasedOnElementType(project.getElementType());
                // Checking if project properties are valid
                if (elementValidationMessages.isEmpty()) {
                    try {
                        project.calculate();
                    } catch (IllegalArgumentException e) {
                        showAlertBox(e.getMessage(), AlertKind.ERROR, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
                    }
                    // Generating results area
                    generateResultsArea();
                } else {
                    showAlertBox(elementValidationMessages.get(0), AlertKind.ERROR, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
                }
            } else {
                showAlertBox(validationMessages.get(0), AlertKind.INFO);
            }
        } else {
            showAlertBox(UIText.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
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
            setProjectPropertiesFromInputFields();
            App.setRoot("design-parameters");
        } else {
            showAlertBox(UIText.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
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
            setProjectPropertiesFromInputFields();
            switch (project.getElementType().toLowerCase()) {
                case Constants.ELEMENT_TYPE_SLAB:
                    App.setRoot("slab-reinforcement");
                    break;
                case Constants.ELEMENT_TYPE_BEAM:
                    App.setRoot("beam-reinforcement");
                    break;
                default:
                    showAlertBox(UIText.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(UIText.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
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
            setProjectPropertiesFromInputFields();
            switch (project.getElementType().toLowerCase()) {
                case Constants.ELEMENT_TYPE_SLAB:
                    App.setRoot("slab-geometry");
                    break;
                case Constants.ELEMENT_TYPE_BEAM:
                    App.setRoot("beam-geometry");
                    break;
                default:
                    showAlertBox(UIText.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } else {
            showAlertBox(UIText.SETUP_ELEMENT_TYPE, AlertKind.ERROR);
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
            setMomentsUnit(UIText.UNIT_MOMENT_SLAB);
        } else if (elementType.equals(Constants.ELEMENT_TYPE_BEAM)) {
            UlsShear.setText("");
            UlsShearWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
            setMomentsUnit(UIText.UNIT_MOMENT_BEAM);
        }
        if (project.getElementType() != null && !project.getElementType().equals(elementType)) {
            // Resetting when switching between element types
            resetProject(false, false);
        }
        project.setElementType(elementType);
    }


    /**
     * Resets project properties. It also clears results area and structural element image on canvas.
     */
    private void resetProject() {
        resetProject(true, true);
    }

    /**
     * Resets project properties. It also clears results area and structural element image on canvas.
     *
     * @param shouldResetProjectDetails if true it resets project details
     * @param shouldResetElementType    if true it resets element type
     */
    private void resetProject(boolean shouldResetProjectDetails, boolean shouldResetElementType) {
        if (shouldResetProjectDetails) {
            // Resetting project details
            project.setName(null);
            project.setId(null);
            project.setDescription(null);
            project.setAuthor(null);
            projectName.setText("");
            projectNumber.setText("");
            projectDescription.setText("");
            projectAuthor.setText("");
        }

        // Resetting analysis results
        project.setUlsMoment(null);
        project.setSlsMoment(null);
        project.setUlsShear(null);
        UlsMoment.setText("");
        SlsMoment.setText("");
        UlsShear.setText("");

        // Resetting element type
        if (shouldResetElementType) {
            project.setElementType(null);
            elementTypeChoiceBox.hide();
        }

        // Resetting main Project properties
        project.setGeometry(null);
        project.setReinforcement(null);
        project.setDesignParameters(null);
        project.setConcrete(null);
        geometrySection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
        geometryText.setText(UIText.ENTER_GEOMETRY);
        reinforcementSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);
        reinforcementText.setText(UIText.ENTER_REINFORCEMENT);
        designParametersSection.getStyleClass().add(CssStyleClasses.NOT_DEFINED);

        // Clearing Image
        GraphicsContext graphicsContext = elementImage.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, elementImage.getWidth(), elementImage.getHeight());

        // Resetting Results Area
        clearResultsArea();
        project.resetResults();
    }

    /**
     * Sets project properties. It takes values from the input fields and sets them in project instance properties.
     * It sets fields from:
     * - project details
     * - analysis results
     * - element type
     */
    private void setProjectPropertiesFromInputFields() {
        project.setName(projectName.getText());
        project.setId(projectNumber.getText());
        project.setDescription(projectDescription.getText());
        project.setAuthor(projectAuthor.getText());
        project.setUlsMoment(UlsMoment.getText());
        project.setSlsMoment(SlsMoment.getText());
        project.setUlsShear(UlsShear.getText());
        if (elementTypeChoiceBox.getValue() != null) {
            project.setElementType(elementTypeChoiceBox.getValue().toLowerCase());
        }
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
     * It creates a wrapper VBox that includes a title and an inner VBox with results for a single capacity check (shear/bending/cracking etc.).
     * The inner VBox includes:
     * - capacity note with a pass and fail text, for example: '12 kNm < 15 kNm Pass'
     * - additional message, which can include info why design is failing
     *
     * @param designValue       Design value of the analysis force/moment
     * @param maxValue          Design capacity
     * @param title             Title for the analysis result area, for example, 'Flexure', 'Shear' etc.
     * @param capacityMessage   Message that that includes comparison of designValue and maxValue. for instance: '12 kNm < 15 kNm'
     * @param additionalMessage Additional message for the user if section fails. For example: 'Increase reinforcement or redesign section.'
     * @param isError           Indicates if error has been encountered during calculations
     * @return wrapper VBox
     */
    private VBox generateResultsForSingleCheck(double designValue, double maxValue, String title, String capacityMessage, String additionalMessage, boolean isError) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add(CssStyleClasses.SUBHEADING);
        Label capacityLabel = new Label(capacityMessage);
        Label passFailLabel = new Label((designValue <= maxValue && !isError) ? UIText.PASS : UIText.FAIL);
        passFailLabel.getStyleClass().add((designValue <= maxValue && !isError) ? CssStyleClasses.PASS : CssStyleClasses.FAIL);
        HBox utilizationNoteWrapper = new HBox(capacityLabel, passFailLabel);
        utilizationNoteWrapper.getStyleClass().add(CssStyleClasses.UTILIZATION_WRAPPER);
        Label additionalMessageLabel = new Label(additionalMessage);
        additionalMessageLabel.getStyleClass().add(CssStyleClasses.RESULTS_MESSAGE);
        VBox results = new VBox(utilizationNoteWrapper, additionalMessageLabel);
        results.getStyleClass().add(CssStyleClasses.RESULTS);
        return new VBox(titleLabel, results);
    }

    /**
     * Generates results area for flexure, shear and bending. It invokes the method that generates results for a single capacity check.
     */
    private void generateResultsArea() {
        if (project.getFlexureCapacityCheckMessage() != null || project.getShearCapacityCheckMessage() != null || project.getCrackingCheckMessage() != null) {
            designResultsWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
        }

        if (project.getFlexureCapacityCheckMessage() != null) {
            VBox flexureResults = generateResultsForSingleCheck(Math.abs(Double.parseDouble(project.getUlsMoment())), project.getFlexureCapacity(), UIText.FLEXURE, project.getFlexureCapacityCheckMessage(), project.getFlexureResultsAdditionalMessage(), project.getIsFlexureError());
            flexureResultsWrapper.getChildren().add(flexureResults);
        }
        if (project.getShearCapacityCheckMessage() != null) {
            VBox shearResults = generateResultsForSingleCheck(Math.abs(Double.parseDouble(project.getUlsShear())), project.getShearCapacity(), UIText.SHEAR, project.getShearCapacityCheckMessage(), project.getShearResultsAdditionalMessage(), project.getIsShearError());
            shearResultsWrapper.getChildren().add(shearResults);
        }
        if (project.getCrackingCheckMessage() != null) {
            VBox crackingResults = generateResultsForSingleCheck(project.getCrackWidth(), project.getCrackWidthLimit(), UIText.CRACKING, project.getCrackingCheckMessage(), project.getCrackingResultsAdditionalMessage(), project.getIsCrackingError());
            crackingResultsWrapper.getChildren().add(crackingResults);
        }
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
                elementValidationMessages.add(UIText.INVALID_ELEMENT_TYPE);
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
     * Generates the element image based on provided geometry and reinforcement.
     * It adds labels describing geometry and reinforcement.
     */
    private void drawElementImage() {
        if (project.getGeometry() == null) {
            return;
        }

        try {
            switch (project.getElementType().toLowerCase()) {
                case Constants.ELEMENT_TYPE_SLAB:
                    drawSlabImage();
                    break;
                case Constants.ELEMENT_TYPE_BEAM:
                    drawBeamImage();
                    break;
                default:
                    showAlertBox(UIText.INVALID_ELEMENT_TYPE, AlertKind.ERROR);
            }
        } catch (IllegalArgumentException e) {
            // Showing warning if geometry or reinforcement were instantiated using wrong constructor - no graphics context etc.
            showAlertBox(e.getMessage(), AlertKind.WARNING);
        }
    }

    /**
     * Draws slab image along with dimension lines. It also invokes method to draw slab reinforcement.
     * Slab image is drawn relatively to the canvas size using the ratios defined in constants.
     * It scales the image by using scale from getSlabImageScale method.
     */
    private void drawSlabImage() {
        GraphicsContext graphicsContext = elementImage.getGraphicsContext2D();
        double canvasWidth = elementImage.getWidth();
        double canvasHeight = elementImage.getHeight();
        int slabImageWidth = (int) (SLAB_IMAGE_HORIZONTAL_RATIO * canvasWidth);
        int slabThickness = project.getGeometry().getDepth();
        double slabImageScale = getSlabImageScale(slabImageWidth, slabThickness);
        int slabImageHeight = (int) (slabImageScale * slabThickness);

        double slabLeftEdgeX = 0.5 * canvasWidth - 0.5 * slabImageWidth;
        double slabTopEdgeY = 0.5 * canvasHeight - 0.5 * slabImageHeight;
        double slabBottomEdgeY = slabTopEdgeY + slabImageHeight;
        int slabEndArchDepth = (int) (getScaleForEndArchDepth(slabThickness) * SlabStrip.SMALL_END_ARCH_DEPTH);

        SlabStrip slabStrip = new SlabStrip(
                slabImageWidth,
                slabImageHeight,
                graphicsContext,
                Color.BLACK,
                Color.LIGHTGRAY,
                slabLeftEdgeX,
                slabTopEdgeY,
                slabEndArchDepth
        );
        slabStrip.draw();

        // Draw Vertical Dimension Line
        VerticalDimensionLine verticalDimensionLine = new VerticalDimensionLine(
                Integer.toString(project.getGeometry().getDepth()),
                Color.BLACK,
                graphicsContext,
                slabTopEdgeY,
                slabBottomEdgeY,
                slabLeftEdgeX,
                -DimensionLine.DEFAULT_SMALL_OFFSET,
                SLAB_IMAGE_DIMENSION_LINES_SCALE,
                true
        );
        verticalDimensionLine.draw();

        DesignParameters designParameters = project.getDesignParameters();
        boolean isReinforcementSetup = project.getReinforcement() != null && designParameters != null;

        if (isReinforcementSetup) {
            // Draw slab reinforcement
            drawSlabReinforcement(slabStrip, designParameters, slabImageScale);
        }
    }

    /**
     * Draws slab reinforcement. It adds reinforcement description as text on canvas.
     * Slab reinforcement is scaled using the scale from getSlabImageScale method.
     *
     * @param slabStrip        slab strip object instantiated using constructor with graphics context
     * @param slabImageScale   slab image scale
     * @param designParameters design parameters object
     */
    private void drawSlabReinforcement(SlabStrip slabStrip, DesignParameters designParameters, double slabImageScale) {
        SlabReinforcement slabReinforcement = (SlabReinforcement) project.getReinforcement();
        GraphicsContext graphicsContext = elementImage.getGraphicsContext2D();

        SlabReinforcement slabReinforcementToDraw = new SlabReinforcement(
                slabReinforcement.getTopDiameters(),
                slabReinforcement.getAdditionalTopDiameters(),
                slabReinforcement.getTopSpacings(),
                slabReinforcement.getTopVerticalSpacings(),
                slabReinforcement.getBottomDiameters(),
                slabReinforcement.getAdditionalBottomDiameters(),
                slabReinforcement.getBottomSpacings(),
                slabReinforcement.getBottomVerticalSpacings(),
                designParameters,
                slabStrip,
                graphicsContext,
                Color.BLACK,
                slabImageScale
        );

        slabReinforcementToDraw.draw();
    }

    /**
     * It gets the scale of the slab image. The scale depends on MAX_SLAB_THICKNESS_WHEN_DRAWING.
     * If this is less then limiting value, the scale is constant and the slab image represents 1m wide strip and slabImageHeight changes accordingly.
     * Otherwise, the slabImageHeight stays constant when increasing slab depth, scale decreases and the slab strip width increases and is greater than 1m.
     *
     * @param slabImageWidth slab image width
     * @param slabThickness  actual slab thickness in mm
     * @return scale of the slab image
     */
    private double getSlabImageScale(double slabImageWidth, int slabThickness) {
        if (slabThickness <= MAX_SLAB_THICKNESS_WHEN_DRAWING) {
            return slabImageWidth / 1000;
        } else {
            double maxSlabImageHeight = slabImageWidth * MAX_SLAB_THICKNESS_WHEN_DRAWING / 1000;

            double slabWidth = slabImageWidth * slabThickness / maxSlabImageHeight;

            return slabImageWidth / slabWidth;
        }
    }

    /**
     * Calculates the scale for the end arch depth. This is a ratio of MAX_SLAB_THICKNESS_WHEN_DRAWING to the slab thickness.
     *
     * @param slabThickness slab thickness in mm
     * @return scale for the end arch depth
     */
    private double getScaleForEndArchDepth(int slabThickness) {
        return Math.min((double) MAX_SLAB_THICKNESS_WHEN_DRAWING / slabThickness, 1.0);
    }

    /**
     * Draws beam image.
     */
    private void drawBeamImage() {
        GraphicsContext graphicsContext = elementImage.getGraphicsContext2D();
        double canvasWidth = elementImage.getWidth();
        double canvasHeight = elementImage.getHeight();
        int beamWidth = project.getGeometry().getWidth();
        int beamDepth = project.getGeometry().getDepth();

        double beamImageScale = getBeamImageScale(beamWidth, canvasWidth, beamDepth, canvasHeight);

        int beamImageWidth = (int) (beamImageScale * beamWidth);
        int beamImageHeight = (int) (beamImageScale * beamDepth);

        double beamLeftEdgeX = 0.5 * canvasWidth - 0.5 * beamImageWidth;
        double beamRightEdgeX = beamLeftEdgeX + beamImageWidth;
        double beamTopEdgeY = 0.5 * canvasHeight - 0.5 * beamImageHeight;
        double beamBottomEdgeY = beamTopEdgeY + beamImageHeight;

        Rectangle rectangle = new Rectangle(
                beamImageWidth,
                beamImageHeight,
                graphicsContext,
                Color.BLACK,
                Color.LIGHTGRAY,
                beamLeftEdgeX,
                beamTopEdgeY
        );

        rectangle.draw();

        // Draw Vertical Dimension Line
        VerticalDimensionLine verticalDimensionLine = new VerticalDimensionLine(
                Integer.toString(project.getGeometry().getDepth()),
                Color.BLACK,
                graphicsContext,
                beamTopEdgeY,
                beamBottomEdgeY,
                beamLeftEdgeX,
                -DimensionLine.DEFAULT_SMALL_OFFSET,
                SLAB_IMAGE_DIMENSION_LINES_SCALE,
                true
        );
        verticalDimensionLine.draw();

        // Draw Horizontal Dimension Line
        HorizontalDimensionLine horizontalDimensionLine = new HorizontalDimensionLine(
                Integer.toString(project.getGeometry().getWidth()),
                Color.BLACK,
                graphicsContext,
                beamLeftEdgeX,
                beamRightEdgeX,
                beamBottomEdgeY,
                DimensionLine.DEFAULT_SMALL_OFFSET,
                SLAB_IMAGE_DIMENSION_LINES_SCALE
        );
        horizontalDimensionLine.draw();

//
//        DesignParameters designParameters = project.getDesignParameters();
//        boolean isReinforcementSetup = project.getReinforcement() != null && designParameters != null;
//
//        if (isReinforcementSetup) {
//            // Draw slab reinforcement
//            drawSlabReinforcement(slabStrip, designParameters, slabImageScale);
//        }
    }

    /**
     * Draws beam reinforcement.
     */
    private void drawBeamReinforcement() {
        System.out.println("Drawing beam reinforcement.");
    }

    private double getBeamImageScale(int beamWidth, double canvasWidth, int beamDepth, double canvasHeight) {
        double scale;

        switch (Integer.signum(beamDepth - beamWidth)) {
            case 1:
                double beamImageVerticalRatio = BEAM_IMAGE_MAX_VERTICAL_RATIO;
                do {
                    scale = beamImageVerticalRatio * canvasHeight / beamDepth;
                    beamImageVerticalRatio = beamImageVerticalRatio - BEAM_IMAGE_RATIO_REDUCTION_STEP;
                } while (scale * beamWidth <= BEAM_IMAGE_MAX_HORIZONTAL_RATIO);
                break;
            case -1:
                double beamImageHorizontalRatio = BEAM_IMAGE_MAX_HORIZONTAL_RATIO;
                do {
                    scale = beamImageHorizontalRatio * canvasWidth / beamWidth;
                    beamImageHorizontalRatio = beamImageHorizontalRatio - BEAM_IMAGE_RATIO_REDUCTION_STEP;
                } while (scale * beamDepth <= BEAM_IMAGE_MAX_VERTICAL_RATIO);
                break;
            default:
                scale = BEAM_IMAGE_MAX_HORIZONTAL_RATIO * canvasWidth / beamWidth;
                break;
        }

        return scale;
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
            validationMessages.add(UIText.SETUP_ANALYSIS_FORCES);
        }
        if (project.getGeometry() == null) {
            validationMessages.add(UIText.SETUP_GEOMETRY);
        }
        if (project.getReinforcement() == null) {
            validationMessages.add(UIText.SETUP_REINFORCEMENT);
        }
        if (project.getDesignParameters() == null) {
            validationMessages.add(UIText.SETUP_DESIGN_PARAMETERS);
        }
        return validationMessages;
    }
}
