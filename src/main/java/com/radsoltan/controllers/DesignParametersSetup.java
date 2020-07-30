package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.NumericalTextField;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Concrete;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.Project;
import com.radsoltan.util.Constants;
import com.radsoltan.util.CssStyleClasses;
import com.radsoltan.util.Messages;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    public CheckBox isRecommendedRedistributionRatio;
    @FXML
    public CheckBox includeCrackingCalculations;

    private final Project project;
    private final DesignParameters designParameters;
    private final Concrete concrete;
    private final String elementType;
    private final ObservableList<Integer> nominalCovers;
    private final ObservableList<String> concreteClasses;

    public DesignParametersSetup() {
        project = Project.getInstance();
//        designParameters = project.getDesignParameters();
        designParameters = new DesignParameters(50, 0, 25, 500, 22, 1.2, 1.0, 0.9, true, true);
        concrete = Concrete.C32_40;
//        concrete = project.getConcrete();
//        elementType = project.getElementType();
        elementType = "slab";

        List<Integer> nominalCoverList = new ArrayList<>();
        IntStream.iterate(Constants.MIN_NOMINAL_COVER, cover -> cover <= Constants.MAX_NOMINAL_COVER, cover -> cover + Constants.NOMINAL_COVER_STEP)
                .forEach(nominalCoverList::add);

        List<String> concreteClassesList = new ArrayList<>();
        Stream.of(Concrete.values()).forEach(concreteClass -> concreteClassesList.add(concreteClass.toString()));

        nominalCovers = FXCollections.observableList(nominalCoverList);
        concreteClasses = FXCollections.observableArrayList(concreteClassesList);
    }

    @FXML
    public void initialize() {
        nominalCoverTop.getItems().addAll(nominalCovers);
        nominalCoverBottom.getItems().addAll(nominalCovers);
        nominalCoverSides.getItems().addAll(nominalCovers);
        concreteClass.getItems().addAll(concreteClasses);
        gammaC.getItems().addAll(Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_C_ACCIDENTAL);
        gammaS.getItems().addAll(Constants.GAMMA_S_PERSISTENT_TRANSIENT, Constants.GAMMA_S_ACCIDENTAL);
        if(elementType.equals("slab")){
            nominalCoverSides.setValue(0);
            nominalCoverSides.getParent().getStyleClass().add(CssStyleClasses.HIDDEN);
        }
        if (designParameters == null) {
            gammaC.setValue(Constants.GAMMA_C_PERSISTENT_TRANSIENT);
            gammaS.setValue(Constants.GAMMA_S_PERSISTENT_TRANSIENT);
            aggregateSize.setText(Integer.toString(Constants.DEFAULT_AGGREGATE_SIZE));
            redistributionRatio.setText(Double.toString(Constants.DEFAULT_REDISTRIBUTION_RATIO));
            isRecommendedRedistributionRatio.selectedProperty().setValue(true);
        } else {
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
        }
        if (concrete != null) {
            concreteClass.setValue(concrete.toString());
        }
        redistributionRatio.setDisable(isRecommendedRedistributionRatio.selectedProperty().get());

        Platform.runLater(() -> container.requestFocus());
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void applyChanges(ActionEvent actionEvent) {
        System.out.println("Apply changes");
    }

    public void setRecommendedRedistributionRatio(ActionEvent actionEvent) {
        redistributionRatio.setDisable(isRecommendedRedistributionRatio.selectedProperty().get());
        redistributionRatio.setText(Double.toString(Constants.DEFAULT_REDISTRIBUTION_RATIO));
    }

    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        return null;
    }

    private String getValidationMessageForRedistributionRatio() {
        double redistributionRatioValue = Double.parseDouble(redistributionRatio.getText());
        return redistributionRatioValue > 1.0 || redistributionRatioValue < 0.7 ? Messages.INVALID_REDISTRIBUTION_RATIO : "";
    }
}
