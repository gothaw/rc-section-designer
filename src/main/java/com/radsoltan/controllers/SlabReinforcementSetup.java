package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
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
import java.util.Set;
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
//        slabReinforcement = project.getReinforcement();

        /* Dummy object */
        List<Integer> topReinforcement = List.of(20, 16, 10, 10);
        List<Integer> additionalTopReinforcement = List.of(20, 12);
        List<Integer> spacingTop = List.of(400, 300, 200, 200);
        List<Integer> vSpacingTop = List.of(50, 50, 25);
        List<Integer> bottomReinforcement = List.of(20, 8, 8);
        List<Integer> additionalBottomReinforcement = List.of(20, 12);
        List<Integer> spacingBottom = List.of(300, 300, 150);
        List<Integer> vSpacingBottom = List.of(50, 50);
        slabReinforcement = new SlabReinforcement(500,
                topReinforcement, additionalTopReinforcement, spacingTop, vSpacingTop,
                bottomReinforcement, additionalBottomReinforcement, spacingBottom, vSpacingBottom);

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
        } else {
            SlabReinforcement slabReinforcement = (SlabReinforcement) this.slabReinforcement;
            numberOfTopLayers = slabReinforcement.getTopReinforcement().size();
            numberOfBottomLayers = slabReinforcement.getBottomReinforcement().size();
            for (int i = 0; i < numberOfTopLayers; i++) {
                addReinforcementLayer(topLayers, topLayersVerticalSpacing, i);
                initializeReinforcementLayer(topLayers, topLayersVerticalSpacing, i, slabReinforcement);
            }
            for (int j = 0; j < numberOfBottomLayers; j++) {
                addReinforcementLayer(bottomLayers, bottomLayersVerticalSpacing, j);
                initializeReinforcementLayer(bottomLayers, bottomLayersVerticalSpacing, j, slabReinforcement);
            }
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void applyChanges(ActionEvent actionEvent) {

    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    private void addReinforcementLayer(VBox layerWrapper, VBox verticalSpacingsWrapper, int layerIndex) {
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_LAYER_LABEL);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> spacingComboBox = new ComboBox<>(spacings);
        spacingComboBox.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        spacingComboBox.setOnAction(this::setAdditionalReinforcementSpacingLabel);

        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyles.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        addButton.setOnAction(this::addAdditionalReinforcement);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(this::deleteAdditionalReinforcement);
        deleteButton.getStyleClass().addAll(CssStyles.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON, CssStyles.HIDDEN);
        StackPane buttonWrapper = new StackPane(addButton, deleteButton);
        buttonWrapper.getStyleClass().add(CssStyles.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);

        HBox layer = new HBox(layerLabel, diameterComboBox, spacingLabel, spacingComboBox, unitsLabel, buttonWrapper);
        layer.getStyleClass().add(CssStyles.SLAB_REINFORCEMENT_LAYER);

        if (layerIndex != 0) {
            PositiveIntegerField verticalSpacingInputField = new PositiveIntegerField();
            Label unitLabel = new Label("mm");
            HBox verticalSpacing = new HBox(verticalSpacingInputField, unitLabel);
            verticalSpacing.getStyleClass().add(CssStyles.SLAB_VERTICAL_SPACING_WRAPPER);
            verticalSpacingsWrapper.getChildren().add(verticalSpacing);
        }

        layerWrapper.getChildren().add(layer);
    }

    private void initializeReinforcementLayer(VBox layersWrapper, VBox verticalSpacingsWrapper, int layerIndex, SlabReinforcement slabReinforcement) {
        List<Node> layers = layersWrapper.getChildren();
        if (layerIndex < layers.size()) {
            HBox layer = (HBox) layers.get(layerIndex);
            @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyles.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyles.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
            StackPane stackPane = (StackPane) layer.lookup("." + CssStyles.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);
            Button addButton = (Button) stackPane.lookup("." + CssStyles.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
            Button deleteButton = (Button) stackPane.lookup("." + CssStyles.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
            if (layersWrapper.equals(topLayers)) {
                diameterComboBox.setValue(slabReinforcement.getTopReinforcement().get(layerIndex));
                spacingComboBox.setValue(slabReinforcement.getTopReinforcementSpacing().get(layerIndex));
                if (layerIndex < slabReinforcement.getAdditionalTopReinforcement().size()) {
                    addAdditionalReinforcement(addButton, deleteButton, layer);
                    @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
                    additionalDiameterComboBox.setValue(slabReinforcement.getAdditionalTopReinforcement().get(layerIndex));
                }
            } else if (layersWrapper.equals(bottomLayers)) {
                diameterComboBox.setValue(slabReinforcement.getBottomReinforcement().get(layerIndex));
                spacingComboBox.setValue(slabReinforcement.getBottomReinforcementSpacing().get(layerIndex));
                if (layerIndex < slabReinforcement.getAdditionalBottomReinforcement().size()) {
                    addAdditionalReinforcement(addButton, deleteButton, layer);
                    @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
                    additionalDiameterComboBox.setValue(slabReinforcement.getBottomReinforcementSpacing().get(layerIndex));
                }
            }
        }
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
        addAdditionalReinforcement(addButton, deleteButton, layer);
    }

    public void addAdditionalReinforcement(Button addButton, Button deleteButton, HBox layer) {
        Label joiningLabel = new Label(" + ");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        Label spacingLabel = new Label(getAdditionalReinforcementSpacingLabel(layer));
        spacingLabel.getStyleClass().add(CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);

        List<Node> additionalReinforcementNodes = new ArrayList<>(List.of(joiningLabel, diameterComboBox, spacingLabel));
        layer.getChildren().addAll(layer.getChildren().size() - 1, additionalReinforcementNodes);

        addButton.getStyleClass().add(CssStyles.HIDDEN);
        deleteButton.getStyleClass().remove(CssStyles.HIDDEN);
    }

    private String getAdditionalReinforcementSpacingLabel(HBox layer) {
        @SuppressWarnings("unchecked") ComboBox<Integer> spacing = (ComboBox<Integer>) layer.lookup("." + CssStyles.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        return (spacing.getValue() != null) ? String.format("at %d mm", spacing.getValue()) : "";
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
        Node node = (Node) actionEvent.getSource();
        HBox layer = (HBox) node.getParent();
        Label additionalReinforcementLabel = (Label) layer.lookup("." + CssStyles.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);
        if (additionalReinforcementLabel != null) {
            additionalReinforcementLabel.setText(getAdditionalReinforcementSpacingLabel(layer));
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
