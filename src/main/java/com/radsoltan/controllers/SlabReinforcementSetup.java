package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import com.radsoltan.util.CssStyleClasses;
import com.radsoltan.util.Messages;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SlabReinforcementSetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public VBox topLayersVBox;
    @FXML
    public VBox topLayersVerticalSpacingVBox;
    @FXML
    public VBox topLayersVerticalSpacingsTitle;
    @FXML
    public VBox bottomLayersVBox;
    @FXML
    public VBox bottomLayersVerticalSpacingVBox;
    @FXML
    public VBox bottomLayersVerticalSpacingsTitle;
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

        List<Integer> spacingsList = new ArrayList<>();
        IntStream.iterate(Constants.SLAB_MIN_BAR_SPACING, spacing -> spacing <= Constants.SLAB_MAX_BAR_SPACING, spacing -> spacing + Constants.SLAB_BAR_SPACING_STEP)
                .forEach(spacingsList::add);
        spacings = FXCollections.observableList(spacingsList);
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);
        layerLabels = Constants.LAYERS_ORDINAL_LABELS;
    }

    @FXML
    public void initialize() {
        if (slabReinforcement == null) {
            addReinforcementLayerInView(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, 0);
            addReinforcementLayerInView(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, 0);
            numberOfTopLayers = 1;
            numberOfBottomLayers = 1;
        } else if (this.slabReinforcement instanceof SlabReinforcement) {
            SlabReinforcement slabReinforcement = (SlabReinforcement) this.slabReinforcement;
            numberOfTopLayers = slabReinforcement.getTopDiameters().size();
            numberOfBottomLayers = slabReinforcement.getBottomDiameters().size();
            List<Integer> topDiameters = slabReinforcement.getTopDiameters();
            List<Integer> topSpacings = slabReinforcement.getTopSpacings();
            List<Integer> additionalTopDiameters = slabReinforcement.getAdditionalTopDiameters();
            List<Integer> topVerticalSpacing = slabReinforcement.getTopVerticalSpacings();
            List<Integer> bottomDiameters = slabReinforcement.getBottomDiameters();
            List<Integer> bottomSpacings = slabReinforcement.getBottomSpacings();
            List<Integer> additionalBottomDiameters = slabReinforcement.getAdditionalBottomDiameters();
            List<Integer> bottomVerticalSpacing = slabReinforcement.getBottomVerticalSpacings();
            IntStream.range(0, numberOfTopLayers).forEach(i -> {
                addReinforcementLayerInView(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, i);
                initializeReinforcementLayerFields(topLayersVBox, topLayersVerticalSpacingVBox, i, topDiameters, additionalTopDiameters, topSpacings, topVerticalSpacing);
            });
            IntStream.range(0, numberOfBottomLayers).forEach(i -> {
                addReinforcementLayerInView(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, i);
                initializeReinforcementLayerFields(bottomLayersVBox, bottomLayersVerticalSpacingVBox, i, bottomDiameters, additionalBottomDiameters, bottomSpacings, bottomVerticalSpacing);
            });
        } else {
            showAlertBox(Messages.INVALID_SLAB_REINFORCEMENT, AlertKind.ERROR);
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessagesForEmptyFields = getValidationMessagesForEmptyFields();
        if (validationMessagesForEmptyFields.isEmpty()) {

            Map<String, List<Integer>> topReinforcement = getSlabReinforcementDataFromLayerFields(topLayersVBox, topLayersVerticalSpacingVBox, numberOfTopLayers);
            Map<String, List<Integer>> bottomReinforcement = getSlabReinforcementDataFromLayerFields(bottomLayersVBox, bottomLayersVerticalSpacingVBox, numberOfBottomLayers);

            Reinforcement slabReinforcement = new SlabReinforcement(topReinforcement.get("diameters"), topReinforcement.get("additionalDiameters"), topReinforcement.get("spacings"), topReinforcement.get("verticalSpacings"),
                    bottomReinforcement.get("diameters"), bottomReinforcement.get("additionalDiameters"), bottomReinforcement.get("spacings"), bottomReinforcement.get("verticalSpacings"));

            project.setReinforcement(slabReinforcement);
            App.setRoot("primary");
        } else {
            showAlertBox(validationMessagesForEmptyFields.get(0), AlertKind.INFO, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
        }
    }


    private Map<String, List<Integer>> getSlabReinforcementDataFromLayerFields(VBox layerWrapper, VBox verticalSpacingsWrapper, int numberOfLayers) {

        List<Integer> diameters = new ArrayList<>();
        List<Integer> additionalDiameters = new ArrayList<>();
        List<Integer> spacings = new ArrayList<>();
        List<Integer> verticalSpacings = new ArrayList<>();

        for (int i = 0; i < numberOfLayers; i++) {
            HBox layer = (HBox) layerWrapper.getChildren().get(i);
            @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
            diameters.add(diameterComboBox.getValue());
            spacings.add(spacingComboBox.getValue());
            if (additionalDiameterComboBox != null) {
                additionalDiameters.add(additionalDiameterComboBox.getValue());
            } else {
                additionalDiameters.add(0);
            }
            if (i > 0) {
                HBox verticalSpacingHBox = (HBox) verticalSpacingsWrapper.getChildren().get(i - 1);
                PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
                verticalSpacings.add(Integer.parseInt(verticalSpacingField.getText()));
            }
        }

        return new HashMap<>(Map.of(
                "diameters", diameters,
                "additionalDiameters", additionalDiameters,
                "spacings", spacings,
                "verticalSpacings", verticalSpacings));
    }

    private void addReinforcementLayerInView(VBox layersWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle, int layerIndex) {
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_LAYER_LABEL);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> spacingComboBox = new ComboBox<>(spacings);
        spacingComboBox.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        spacingComboBox.setOnAction(this::setAdditionalReinforcementSpacingLabel);

        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        addButton.setOnAction(this::addAdditionalReinforcementInView);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(this::deleteAdditionalReinforcementFromView);
        deleteButton.getStyleClass().addAll(CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON, CssStyleClasses.HIDDEN);
        StackPane buttonWrapper = new StackPane(addButton, deleteButton);
        buttonWrapper.getStyleClass().add(CssStyleClasses.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);

        HBox layer = new HBox(layerLabel, diameterComboBox, spacingLabel, spacingComboBox, unitsLabel, buttonWrapper);
        layer.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_LAYER);

        if (layerIndex > 0) {
            PositiveIntegerField verticalSpacingInputField = new PositiveIntegerField();
            verticalSpacingInputField.getStyleClass().add(CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
            Label unitLabel = new Label("mm");
            HBox verticalSpacingHBox = new HBox(verticalSpacingInputField, unitLabel);
            verticalSpacingHBox.getStyleClass().add(CssStyleClasses.SLAB_VERTICAL_SPACING_WRAPPER);
            verticalSpacingsWrapper.getChildren().add(verticalSpacingHBox);
        }

        if (layerIndex == 1) {
            verticalSpacingsTitle.getStyleClass().remove(CssStyleClasses.HIDDEN);
        }

        layersWrapper.getChildren().add(layer);
    }

    private void deleteReinforcementLayerFromView(VBox layersWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle) {
        List<Node> layers = layersWrapper.getChildren();
        layers.remove(layers.size() - 1);
        List<Node> verticalSpacings = verticalSpacingsWrapper.getChildren();
        verticalSpacings.remove(verticalSpacings.size() - 1);
        if (layers.size() == 1) {
            verticalSpacingsTitle.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
    }

    private void initializeReinforcementLayerFields(VBox layersWrapper, VBox verticalSpacingsWrapper,
                                                    int layerIndex, List<Integer> reinforcement, List<Integer> additionalReinforcement,
                                                    List<Integer> spacing, List<Integer> verticalSpacing) {
        List<Node> layers = layersWrapper.getChildren();
        if (layerIndex < layers.size()) {
            HBox layer = (HBox) layers.get(layerIndex);
            @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
            StackPane stackPane = (StackPane) layer.lookup("." + CssStyleClasses.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);
            Button addButton = (Button) stackPane.lookup("." + CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
            Button deleteButton = (Button) stackPane.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
            diameterComboBox.setValue(reinforcement.get(layerIndex));
            spacingComboBox.setValue(spacing.get(layerIndex));
            if (additionalReinforcement.get(layerIndex) != 0) {
                addAdditionalReinforcementInView(addButton, deleteButton, layer);
                @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
                additionalDiameterComboBox.setValue(additionalReinforcement.get(layerIndex));
            }
            if (layerIndex > 0) {
                List<Node> verticalSpacings = verticalSpacingsWrapper.getChildren();
                HBox verticalSpacingHBox = (HBox) verticalSpacings.get(layerIndex - 1);
                PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
                verticalSpacingField.setText(Integer.toString(verticalSpacing.get(layerIndex - 1)));
            }
        }
    }

    public void addAdditionalReinforcementInView(ActionEvent actionEvent) {
        Button addButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) addButton.getParent();
        Button deleteButton = (Button) stackPane.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();
        addAdditionalReinforcementInView(addButton, deleteButton, layer);
    }

    public void addAdditionalReinforcementInView(Button addButton, Button deleteButton, HBox layer) {
        Label joiningLabel = new Label(" + ");
        joiningLabel.getStyleClass().add(CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_JOINING_LABEL);
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        Label spacingLabel = new Label(getAdditionalReinforcementSpacingLabel(layer));
        spacingLabel.getStyleClass().add(CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);

        List<Node> additionalReinforcementNodes = new ArrayList<>(List.of(joiningLabel, diameterComboBox, spacingLabel));
        layer.getChildren().addAll(layer.getChildren().size() - 1, additionalReinforcementNodes);

        addButton.getStyleClass().add(CssStyleClasses.HIDDEN);
        deleteButton.getStyleClass().remove(CssStyleClasses.HIDDEN);
    }

    private String getAdditionalReinforcementSpacingLabel(HBox layer) {
        @SuppressWarnings("unchecked") ComboBox<Integer> spacing = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        return (spacing.getValue() != null) ? String.format("at %d mm", spacing.getValue()) : "";
    }

    public void deleteAdditionalReinforcementFromView(ActionEvent actionEvent) {
        Button deleteButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) deleteButton.getParent();
        Button addButton = (Button) stackPane.lookup("." + CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();

        List<Node> layerNodes = layer.getChildren();
        Label joiningLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_JOINING_LABEL);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        Label spacingLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);

        layerNodes.removeAll(new ArrayList<>(List.of(joiningLabel, diameterComboBox, spacingLabel)));

        deleteButton.getStyleClass().add(CssStyleClasses.HIDDEN);
        addButton.getStyleClass().remove(CssStyleClasses.HIDDEN);
    }

    public void setAdditionalReinforcementSpacingLabel(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        HBox layer = (HBox) node.getParent();
        Label additionalReinforcementLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);
        if (additionalReinforcementLabel != null) {
            additionalReinforcementLabel.setText(getAdditionalReinforcementSpacingLabel(layer));
        }
    }

    public void deleteLayerFromTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopLayers > 1) {
            deleteReinforcementLayerFromView(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle);
            numberOfTopLayers--;
        }
    }

    public void addLayerToTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayerInView(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, numberOfTopLayers);
            numberOfTopLayers++;
        }
    }

    public void deleteLayerFromBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomLayers > 1) {
            deleteReinforcementLayerFromView(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle);
            numberOfBottomLayers--;
        }
    }

    public void addLayerToBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayerInView(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, numberOfBottomLayers);
            numberOfBottomLayers++;
        }
    }

    private List<String> getValidationMessagesIfLayerFieldsAreEmpty(VBox layersWrapper, VBox verticalSpacingsWrapper, int layerIndex, String layersLocation) {
        List<String> validationMessages = new ArrayList<>();
        String layerLocation = layersLocation.toLowerCase();
        HBox layer = (HBox) layersWrapper.getChildren().get(layerIndex);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        if (diameterComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter bar diameter for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        if (spacingComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter bar spacing for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        if (additionalDiameterComboBox != null && additionalDiameterComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter alternate bar diameter for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        if (layerIndex > 0) {
            HBox verticalSpacingHBox = (HBox) verticalSpacingsWrapper.getChildren().get(layerIndex - 1);
            PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
            if (verticalSpacingField.getText().equals("")) {
                validationMessages.add(String.format("Please enter vertical spacing between %s and %s %s layer.", layerLabels.get(layerIndex - 1), layerLabels.get(layerIndex), layerLocation));
            }
        }
        return validationMessages;
    }

    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        List<String> validationMessages = new ArrayList<>();
        for (int i = 0; i < numberOfTopLayers; i++) {
            validationMessages.addAll(getValidationMessagesIfLayerFieldsAreEmpty(topLayersVBox, topLayersVerticalSpacingVBox, i, "top"));
        }
        for (int j = 0; j < numberOfBottomLayers; j++) {
            validationMessages.addAll(getValidationMessagesIfLayerFieldsAreEmpty(bottomLayersVBox, bottomLayersVerticalSpacingVBox, j, "bottom"));
        }
        return validationMessages;
    }
}
