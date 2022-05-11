package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Concrete;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.Project;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.CssStyleClasses;
import com.radsoltan.constants.UIText;

import com.radsoltan.util.AlertKind;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Controller for Design Parameters view. The view is used to set up all the necessary parameters that are used in ULS, SLS calculations.
 * This includes:
 * - nominal cover
 * - concrete (class, aggregate size)
 * - steel (yield strength)
 * - partial factors of safety for steel and concrete
 * - redistribution ratio
 * - option to exclude cracking calculations
 * - limiting crack width
 * The controller also does validation if any of the fields are empty when user presses okay button.
 * It allows for creating designParameters and concrete objects in project instance.
 */
public class DesignParametersSetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public ComboBox<Integer> nominalCoverTop;
    @FXML
    public ComboBox<Integer> nominalCoverBottom;
    @FXML
    public ComboBox<Integer> nominalCoverSides;
    @FXML
    public ComboBox<String> concreteClass;
    @FXML
    public PositiveIntegerField aggregateSize;
    @FXML
    public PositiveIntegerField yieldStrength;
    @FXML
    public ComboBox<Double> gammaS;
    @FXML
    public ComboBox<Double> gammaC;
    @FXML
    public NumericalTextField redistributionRatio;
    @FXML
    public NumericalTextField maxCrackWidth;
    @FXML
    public CheckBox isRecommendedRedistributionRatio;
    @FXML
    public CheckBox includeCrackingCalculations;
    @FXML
    public HBox maxCrackWidthWrapper;

    private final Project project;
    private final DesignParameters designParameters;
    private final Concrete concrete;
    private final String elementType;
    private final ObservableList<Integer> nominalCovers;
    private final ObservableList<String> concreteClasses;

    /**
     * Constructor. It gets project instance and based on the instance it gets specific properties related to design parameters.
     * It also creates lists for nominal covers and concrete classes.
     */
    public DesignParametersSetup() {
        project = Project.getInstance();
        designParameters = project.getDesignParameters();
        concrete = project.getConcrete();
        elementType = project.getElementType();

        // List of nominal covers populated using IntStream
        List<Integer> nominalCoverList = new ArrayList<>();
        IntStream.iterate(Constants.MIN_NOMINAL_COVER, cover -> cover <= Constants.MAX_NOMINAL_COVER, cover -> cover + Constants.NOMINAL_COVER_STEP)
                .forEach(nominalCoverList::add);

        // List of concrete classes created using stream of enum values for Concrete enum
        // These include '_' as separator instead of '/'
        List<String> concreteClassesList = new ArrayList<>();
        Stream.of(Concrete.values()).forEach(concreteClass -> concreteClassesList.add(concreteClass.toString()));

        nominalCovers = FXCollections.observableList(nominalCoverList);
        concreteClasses = FXCollections.observableArrayList(concreteClassesList);
    }

    /**
     * Initialize method that is run after calling the constructor. It sets up drop down lists.
     * If designParameters and concrete objects were instantiated before it takes their properties and set them in the fields.
     * Otherwise, it sets the input fields to default values.
     */
    @FXML
    public void initialize() {
        // Filling drop down lists
        nominalCoverTop.getItems().addAll(nominalCovers);
        nominalCoverBottom.getItems().addAll(nominalCovers);
        nominalCoverSides.getItems().addAll(nominalCovers);
        concreteClass.getItems().addAll(concreteClasses);
        gammaC.getItems().addAll(Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_C_ACCIDENTAL);
        gammaS.getItems().addAll(Constants.GAMMA_S_PERSISTENT_TRANSIENT, Constants.GAMMA_S_ACCIDENTAL);
        if (elementType.equals(Constants.ELEMENT_TYPE_SLAB)) {
            // If slab ignore nominal cover for sides
            nominalCoverSides.setValue(0);
            nominalCoverSides.getParent().getStyleClass().add(CssStyleClasses.HIDDEN);
        }
        if (designParameters == null) {
            // If design parameters were not set, show default values
            gammaC.setValue(Constants.GAMMA_C_PERSISTENT_TRANSIENT);
            gammaS.setValue(Constants.GAMMA_S_PERSISTENT_TRANSIENT);
            aggregateSize.setText(Integer.toString(Constants.DEFAULT_AGGREGATE_SIZE));
            redistributionRatio.setText(Double.toString(Constants.DEFAULT_REDISTRIBUTION_RATIO));
            isRecommendedRedistributionRatio.selectedProperty().setValue(true);
            maxCrackWidthWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
        } else {
            // If design parameters set, show values that are stored in the instance
            nominalCoverTop.setValue(designParameters.getNominalCoverTop());
            nominalCoverBottom.setValue(designParameters.getNominalCoverBottom());
            nominalCoverSides.setValue(designParameters.getNominalCoverSides());
            aggregateSize.setText(Integer.toString(designParameters.getAggregateSize()));
            yieldStrength.setText(Integer.toString(designParameters.getYieldStrength()));
            gammaC.setValue(designParameters.getPartialFactorOfSafetyForConcrete());
            gammaS.setValue(designParameters.getPartialFactorOfSafetyForSteel());
            redistributionRatio.setText(Double.toString(designParameters.getRedistributionRatio()));
            isRecommendedRedistributionRatio.selectedProperty().setValue(designParameters.isRecommendedRatio());
            includeCrackingCalculations.selectedProperty().setValue(designParameters.isIncludeCrackingCalculations());
            if (designParameters.isIncludeCrackingCalculations()) {
                maxCrackWidth.setText(Double.toString(designParameters.getCrackWidthLimit()));
            } else {
                maxCrackWidthWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
            }
        }
        if (concrete != null) {
            concreteClass.setValue(concrete.toString());
        }
        redistributionRatio.setDisable(isRecommendedRedistributionRatio.selectedProperty().get());

        // Focusing window instead of first component
        Platform.runLater(() -> container.requestFocus());
    }

    /**
     * Method that handles ok button click. It requires all values to be set (not empty) before redirecting to primary controller.
     * In addition to empty field validation, it includes validation of redistribution ratio value.
     * Values from fields are saved to designParameters instance.
     *
     * @param actionEvent Ok button click event.
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        // Validating for empty fields
        List<String> validationMessages = getValidationMessagesForEmptyFields();
        // Validating redistribution ratio
        String validationMessageForRedistributionRatio = getValidationMessageForRedistributionRatio();
        // Validating max crack width
        String validationMessageForMaxCrackWidth = getValidationMessageForMaxCrackWidths();
        if (!validationMessageForRedistributionRatio.isEmpty()) {
            validationMessages.add(validationMessageForRedistributionRatio);
        }
        if (!validationMessageForMaxCrackWidth.isEmpty()) {
            validationMessages.add(validationMessageForMaxCrackWidth);
        }
        if (validationMessages.isEmpty()) {
            // Creating designParameters and concrete objects based on values provided
            int nominalCoverTop = this.nominalCoverTop.getValue();
            int nominalCoverSides = this.nominalCoverSides.getValue();
            int nominalCoverBottom = this.nominalCoverBottom.getValue();
            int yieldStrength = Integer.parseInt(this.yieldStrength.getText());
            int aggregateSize = Integer.parseInt(this.aggregateSize.getText());
            double gammaC = this.gammaC.getValue();
            double gammaS = this.gammaS.getValue();
            double redistributionRatio = Double.parseDouble(this.redistributionRatio.getText());
            boolean isRecommendedRedistributionRatio = this.isRecommendedRedistributionRatio.isSelected();
            boolean includeCrackingCalculations = this.includeCrackingCalculations.isSelected();
            double maxCrackWidth = includeCrackingCalculations ? Double.parseDouble(this.maxCrackWidth.getText()) : 0;

            DesignParameters designParameters = new DesignParameters(
                    nominalCoverTop,
                    nominalCoverSides,
                    nominalCoverBottom,
                    yieldStrength,
                    aggregateSize,
                    gammaC,
                    gammaS,
                    redistributionRatio,
                    isRecommendedRedistributionRatio,
                    includeCrackingCalculations,
                    maxCrackWidth
            );

            String concreteClass = this.concreteClass.getValue();
            Concrete concrete = Concrete.valueOf(concreteClass.replace('/', '_'));

            project.setDesignParameters(designParameters);
            project.setConcrete(concrete);
            project.resetResults();

            App.setRoot("primary");
        } else {
            // Show validation message
            showAlertBox(validationMessages.get(0), AlertKind.INFO, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
        }
    }

    /**
     * Method that handles cancel button click. It redirects to primary controller by using setRoot method.
     *
     * @param actionEvent Cancel button click event.
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    /**
     * Handler for redistribution ratio checkbox. It disables and enables redistribution ratio input field.
     * It also resets the value in the input field to default value.
     *
     * @param actionEvent Redistribution ratio checkbox click
     */
    public void setRecommendedRedistributionRatio(ActionEvent actionEvent) {
        redistributionRatio.setDisable(isRecommendedRedistributionRatio.selectedProperty().get());
        redistributionRatio.setText(Double.toString(Constants.DEFAULT_REDISTRIBUTION_RATIO));
    }

    /**
     * Handler for include cracking calculation checkbox.
     *
     * @param actionEvent Enable cracking calculation checkbox click
     */
    public void enableIncludeCrackingCalculation(ActionEvent actionEvent) {
        if (this.includeCrackingCalculations.isSelected()) {
            maxCrackWidthWrapper.getStyleClass().remove(CssStyleClasses.HIDDEN);
        } else {
            maxCrackWidthWrapper.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
    }

    /**
     * Checks if the fields in view are empty or don't have any value set.
     * If this is the case a message is added to the list of validation messages.
     * This does not include redistribution ratio and max crack width input fields.
     *
     * @return List of validation messages
     */
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        List<String> validationMessages = new ArrayList<>();
        if (nominalCoverTop.getValue() == null || nominalCoverBottom.getValue() == null || nominalCoverSides.getValue() == null) {
            validationMessages.add(UIText.SETUP_NOMINAL_COVER);
        }
        if (concreteClass.getValue() == null) {
            validationMessages.add(UIText.SETUP_CONCRETE_CLASS);
        }
        if (aggregateSize.getText().isEmpty()) {
            validationMessages.add(UIText.SETUP_AGGREGATE);
        }
        if (yieldStrength.getText().isEmpty()) {
            validationMessages.add(UIText.SETUP_YIELD_STRENGTH);
        }
        if (gammaS.getValue() == null || gammaC.getValue() == null) {
            validationMessages.add(UIText.SETUP_PARTIAL_FACTORS);
        }
        return validationMessages;
    }

    /**
     * Checks the redistribution ratio input field if it has correct value. According to EC2, this should be between 0.7 and 1.0.
     * If the value is within limits an empty string is returned. Otherwise, a validation message as a string is returned from the method.
     *
     * @return validation message for the redistribution ratio
     */
    private String getValidationMessageForRedistributionRatio() {
        if (redistributionRatio.getText().isEmpty()) {
            return UIText.INVALID_REDISTRIBUTION_RATIO;
        }
        double redistributionRatioValue = Double.parseDouble(redistributionRatio.getText());
        return redistributionRatioValue > 1.0 || redistributionRatioValue < 0.7 ? UIText.INVALID_REDISTRIBUTION_RATIO : "";
    }

    /**
     * Checks if value for max crack width is correct. The assumption is that this should be greater than 0.05 and less 0.5 mm.
     * If the value is within limits and empty string is return. Otherwise, a validation message is returned.
     *
     * @return validation message for max crack width
     */
    private String getValidationMessageForMaxCrackWidths() {
        if (!this.includeCrackingCalculations.isSelected()) {
            return "";
        }
        if (maxCrackWidth.getText().isEmpty()) {
            return UIText.INVALID_MAX_CRACK_WIDTH;
        }
        double maxCrackWidthValue = Double.parseDouble(maxCrackWidth.getText());
        return maxCrackWidthValue < 0.05 || maxCrackWidthValue > 0.5
                ? UIText.INVALID_MAX_CRACK_WIDTH
                : "";
    }
}
