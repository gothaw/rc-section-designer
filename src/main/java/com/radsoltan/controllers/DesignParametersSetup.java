package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Concrete;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.Project;
import com.radsoltan.util.Constants;
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

public class DesignParametersSetup {

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
    public PositiveIntegerField redistributionRatio;
    @FXML
    public CheckBox isRecommendedRedistributionRatio;
    @FXML
    public CheckBox includeCrackingCalculations;

    private final Project project;
    private final DesignParameters designParameters;
    private final Concrete concrete;
    private final ObservableList<Integer> nominalCovers;
    private final ObservableList<String> concreteClasses;

    public DesignParametersSetup() {
        this.project = Project.getInstance();
        designParameters = project.getDesignParameters();
        concrete = project.getConcrete();

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
        gammaC.setValue(Constants.GAMMA_C_PERSISTENT_TRANSIENT);
        gammaS.getItems().addAll(Constants.GAMMA_S_PERSISTENT_TRANSIENT, Constants.GAMMA_S_ACCIDENTAL);
        gammaS.setValue(Constants.GAMMA_S_PERSISTENT_TRANSIENT);
        aggregateSize.setText(Integer.toString(Constants.DEFAULT_AGGREGATE_SIZE));
        Platform.runLater(() -> container.requestFocus());
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void applyChanges(ActionEvent actionEvent) {
        System.out.println("Apply changes");
    }
}
