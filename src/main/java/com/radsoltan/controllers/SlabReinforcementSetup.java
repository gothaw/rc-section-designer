package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.util.Constants;
import com.radsoltan.util.CssStyles;
import com.radsoltan.util.Utility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SlabReinforcementSetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public VBox topLayers;
    @FXML
    public VBox topLayersVerticalSpacing;
    @FXML
    public VBox bottomLayers;
    @FXML
    public VBox bottomLayersVerticalSpacing;
    @FXML
    public Button deleteTopLayerButton;
    @FXML
    public Button addTopLayerButton;
    @FXML
    public Button deleteBottomLayerButton;
    @FXML
    public Button addBottomLayerButton;

    private int numberOfTopLayers;
    private int numberOfBottomLayers;
    private final Project project;
    private final Reinforcement slabReinforcement;
    private final ObservableList<Integer> spacings;
    private final ObservableList<Integer> diameters;
    private final ArrayList<String> layerLabels;

    public SlabReinforcementSetup() {
        project = Project.getInstance();
        slabReinforcement = project.getReinforcement();
        List<Integer> spacingsArray = new ArrayList<>();
        IntStream.iterate(50, spacing -> spacing <= 750, spacing -> spacing + 25).forEach(spacingsArray::add);
        spacings = FXCollections.observableList(spacingsArray);
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);
        layerLabels = Constants.LAYERS_ORDINAL_LABELS;
    }

    @FXML
    public void initialize() {
        if (slabReinforcement == null) {
            addReinforcementLayer(topLayers, topLayersVerticalSpacing, 0);
            addReinforcementLayer(bottomLayers, bottomLayersVerticalSpacing, 0);
            numberOfTopLayers = 1;
            numberOfBottomLayers = 1;
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void applyChanges(ActionEvent actionEvent) {

    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    private void addReinforcementLayer(VBox layerTarget, VBox verticalSpacingTarget, int layerIndex) {
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_LAYER_LABEL);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        ComboBox<Integer> spacingComboBox = new ComboBox<>(spacings);
        spacingComboBox.setOnAction(this::setAdditionalReinforcementSpacingLabel);

        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyles.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        addButton.setOnAction(this::addAdditionalReinforcement);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(this::deleteAdditionalReinforcement);
        deleteButton.getStyleClass().addAll(CssStyles.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON, CssStyles.HIDDEN);
        StackPane buttonWrapper = new StackPane(addButton, deleteButton);

        HBox layer = new HBox(layerLabel, diameterComboBox, spacingLabel, spacingComboBox, unitsLabel, buttonWrapper);
        layer.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_LAYER);

        if (layerIndex != 0) {
            PositiveIntegerField verticalSpacingInputField = new PositiveIntegerField();
            Label unitLabel = new Label("mm");
            HBox verticalSpacingWrapper = new HBox(verticalSpacingInputField, unitLabel);
            verticalSpacingWrapper.getStyleClass().add(CssStyles.SLAB_VERTICAL_SPACING_WRAPPER);
            verticalSpacingTarget.getChildren().add(verticalSpacingWrapper);
        }

        layerTarget.getChildren().add(layer);
    }

    private void deleteReinforcementLayer(VBox layerTarget, VBox verticalSpacingTarget) {
        List<Node> layers = layerTarget.getChildren();
        layers.remove(layers.size() - 1);
        List<Node> verticalSpacings = verticalSpacingTarget.getChildren();
        verticalSpacings.remove(verticalSpacings.size() - 1);
    }

    public void addAdditionalReinforcement(ActionEvent actionEvent) {
        Button addButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) addButton.getParent();
        Button deleteButton = (Button) stackPane.lookup("." + CssStyles.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();

        Label joiningLabel = new Label(" + ");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        Label spacingLabel = new Label();
        spacingLabel.getStyleClass().add(CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);

        List<Node> additionalReinforcementNodes = new ArrayList<>(List.of(joiningLabel, diameterComboBox, spacingLabel));

        layer.getChildren().addAll(layer.getChildren().size() - 1, additionalReinforcementNodes);
        addButton.getStyleClass().add(CssStyles.HIDDEN);
        deleteButton.getStyleClass().remove(CssStyles.HIDDEN);
    }

    public void deleteAdditionalReinforcement(ActionEvent actionEvent) {
        Button deleteButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) deleteButton.getParent();
        Button addButton = (Button) stackPane.lookup("." + CssStyles.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();

        List<Node> layerNodes = layer.getChildren();
        int layerNodesSize = layerNodes.size();
        IntStream.of(2, 3, 4).forEach(i -> layerNodes.remove(layerNodesSize - i));

        deleteButton.getStyleClass().add(CssStyles.HIDDEN);
        addButton.getStyleClass().remove(CssStyles.HIDDEN);
    }

    public void setAdditionalReinforcementSpacingLabel(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof ComboBox) {
            System.out.println(((ComboBox<Integer>) actionEvent.getSource()).getValue());
            ComboBox<Integer> comboBox = (ComboBox) actionEvent.getSource();
            HBox layer = (HBox) comboBox.getParent();
            Label additionalReinforcementLabel = (Label) layer.lookup("." + CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);
            if (additionalReinforcementLabel != null) {
                additionalReinforcementLabel.setText("at " + Integer.toString(comboBox.getValue()) + " mm");
            }
        }
    }

    public void deleteTopLayer(ActionEvent actionEvent) {
        if (numberOfTopLayers > 1) {
            deleteReinforcementLayer(topLayers, topLayersVerticalSpacing);
            numberOfTopLayers--;
        }
    }

    public void addTopLayer(ActionEvent actionEvent) {
        if (numberOfTopLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(topLayers, topLayersVerticalSpacing, numberOfTopLayers);
            numberOfTopLayers++;
        }
    }

    public void deleteBottomLayer(ActionEvent actionEvent) {
        if (numberOfBottomLayers > 1) {
            deleteReinforcementLayer(bottomLayers, bottomLayersVerticalSpacing);
            numberOfBottomLayers--;
        }
    }

    public void addBottomLayer(ActionEvent actionEvent) {
        if (numberOfBottomLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(bottomLayers, bottomLayersVerticalSpacing, numberOfBottomLayers);
            numberOfBottomLayers++;
        }
    }
}
