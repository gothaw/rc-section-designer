package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.CssStyleClasses;
import com.radsoltan.constants.UIText;
import com.radsoltan.util.AlertKind;
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

/**
 * Controller for the view that sets up slab reinforcement.
 * Some key notes with regards to how the slab reinforcement is set up:
 * - The app allows for setting up reinforcement for both top and bottom layers
 * - For each slab face (top/bottom), it is possible to specify up to 6 layers
 * - When specifying multiple layers, it is required to provide a clear spacing between layers
 * - For each layer it is possible to specify alternate reinforcement by pressing 'Add' button.
 * This alternate reinforcement will have the same spacing as the main reinforcement layer but it can have different diameter.
 * - The secondary reinforcement is ignored for the purpose of bending calculations.
 */
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

    /**
     * Constructor. It gets project instance and using the instance it gets the reinforcement.
     * It also creates lists for reinforcement spacings and diameters.
     */
    public SlabReinforcementSetup() {
        project = Project.getInstance();

        slabReinforcement = project.getReinforcement();

        // Creating list for bar spacings
        List<Integer> spacingsList = new ArrayList<>();
        IntStream.iterate(Constants.SLAB_MIN_BAR_SPACING, spacing -> spacing <= Constants.SLAB_MAX_BAR_SPACING, spacing -> spacing + Constants.SLAB_BAR_SPACING_STEP)
                .forEach(spacingsList::add);
        spacings = FXCollections.observableList(spacingsList);
        // Creating list for bar diameters
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);
        // List for reinforcement layer labels
        layerLabels = Constants.ORDINAL_LABELS;
    }

    /**
     * Initialize method that is run after calling the constructor. If no slab reinforcement was set up it creates an empty bottom layer and an empty top layers.
     * If slab reinforcement is found in project instance, it creates required number of reinforcement layers and initializes the fields.
     */
    @FXML
    public void initialize() {
        if (slabReinforcement == null) {
            // If no reinforcement set up before, create one top and one bottom layer
            addReinforcementLayer(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, 0);
            addReinforcementLayer(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, 0);
            numberOfTopLayers = 1;
            numberOfBottomLayers = 1;
        } else if (this.slabReinforcement instanceof SlabReinforcement) {
            // Getting slab reinforcement instance
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
            // Adding slab reinforcement layers to the view and initializing the fields with properties
            IntStream.range(0, numberOfTopLayers).forEach(i -> {
                addReinforcementLayer(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, i);
                initializeReinforcementLayerFields(topLayersVBox, topLayersVerticalSpacingVBox, i, topDiameters, additionalTopDiameters, topSpacings, topVerticalSpacing);
            });
            IntStream.range(0, numberOfBottomLayers).forEach(i -> {
                addReinforcementLayer(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, i);
                initializeReinforcementLayerFields(bottomLayersVBox, bottomLayersVerticalSpacingVBox, i, bottomDiameters, additionalBottomDiameters, bottomSpacings, bottomVerticalSpacing);
            });
        } else {
            // Show error if invalid reinforcement
            showAlertBox(UIText.INVALID_SLAB_REINFORCEMENT, AlertKind.ERROR);
        }
        Platform.runLater(() -> container.requestFocus());
    }

    /**
     * Method that handles ok button click. It checks if there are no empty fields in the form.
     * If all fields are filled with reinforcement data, it gets the data from the form fields and create a slab reinforcement object.
     * It saves the reinforcement to project instance and redirects to primary view.
     *
     * @param actionEvent Ok button click
     * @throws IOException Exception for failed or interrupted I/O operation
     */
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessagesForEmptyFields = getValidationMessagesForEmptyFields();
        if (validationMessagesForEmptyFields.isEmpty()) {
            // Get reinforcement properties from the form fields
            Map<String, List<Integer>> topReinforcement = getSlabReinforcementDataFromLayerFields(topLayersVBox, topLayersVerticalSpacingVBox, numberOfTopLayers);
            Map<String, List<Integer>> bottomReinforcement = getSlabReinforcementDataFromLayerFields(bottomLayersVBox, bottomLayersVerticalSpacingVBox, numberOfBottomLayers);

            Reinforcement slabReinforcement = new SlabReinforcement(topReinforcement.get("diameters"), topReinforcement.get("additionalDiameters"), topReinforcement.get("spacings"), topReinforcement.get("verticalSpacings"),
                    bottomReinforcement.get("diameters"), bottomReinforcement.get("additionalDiameters"), bottomReinforcement.get("spacings"), bottomReinforcement.get("verticalSpacings"));

            project.setReinforcement(slabReinforcement);
            project.resetResults();

            App.setRoot("primary");
        } else {
            showAlertBox(validationMessagesForEmptyFields.get(0), AlertKind.INFO, Constants.LARGE_ALERT_WIDTH, Constants.LARGE_ALERT_HEIGHT);
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
     * Method that takes the reinforcement data from the form fields and saves it to a HashMap.
     * It takes the data for all layers for either top or bottom slab face.
     * The HashMap includes following keys:
     * - diameters, diameters of the main reinforcement bars in subsequent layers
     * - additionalDiameters, diameters of the additional main reinforcement bars that are between main reinforcement.
     * If not set up, the diameter is zero.
     * - spacings, spacings between main reinforcement bar centres
     * - verticalSpacings, clear vertical spacing between reinforcement layers
     *
     * @param layersWrapper           VBox that wraps reinforcement layer fields. It represents slab face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement layers for given slab face - top/bottom
     * @param numberOfLayers          Number of layers for given slab face
     * @return HashMap with reinforcement data for a slab face
     */
    private Map<String, List<Integer>> getSlabReinforcementDataFromLayerFields(VBox layersWrapper, VBox verticalSpacingsWrapper, int numberOfLayers) {

        List<Integer> diameters = new ArrayList<>();
        List<Integer> additionalDiameters = new ArrayList<>();
        List<Integer> spacings = new ArrayList<>();
        List<Integer> verticalSpacings = new ArrayList<>();

        for (int i = 0; i < numberOfLayers; i++) {
            // Looping through each reinforcement layer inside layerWrapper
            HBox layer = (HBox) layersWrapper.getChildren().get(i);
            // Selecting form fields - combo boxes
            @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
            // Getting values from combo boxes and setting them in lists
            diameters.add(diameterComboBox.getValue());
            spacings.add(spacingComboBox.getValue());
            if (additionalDiameterComboBox != null) {
                additionalDiameters.add(additionalDiameterComboBox.getValue());
            } else {
                additionalDiameters.add(0);
            }
            if (i > 0) {
                // If not first layer, get the the vertical spacing value and set in the list
                HBox verticalSpacingHBox = (HBox) verticalSpacingsWrapper.getChildren().get(i - 1);
                PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
                verticalSpacings.add(Integer.parseInt(verticalSpacingField.getText()));
            }
        }

        return new HashMap<>(
                Map.of(
                        "diameters", diameters,
                        "additionalDiameters", additionalDiameters,
                        "spacings", spacings,
                        "verticalSpacings", verticalSpacings
                )
        );
    }

    /**
     * Method that adds a reinforcement layer to given slab face (layersWrapper). It adds:
     * - combo box for bar diameter
     * - combo box for main bar spacing
     * - "Add" button that handles adding additional reinforcement between main reinforcement
     * - "Delete" button that handles removing additional reinforcement
     * - layer labels for example "3rd layer:"
     * - vertical spacing input field
     *
     * @param layersWrapper           VBox that wraps reinforcement layer fields. It represents slab face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement layers for given slab face - top/bottom
     * @param verticalSpacingsTitle   VBox with the label for the vertical spacings column
     * @param layerIndex              Index of the layer to be added
     */
    private void addReinforcementLayer(VBox layersWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle, int layerIndex) {
        // Creating text labels
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_LAYER_LABEL);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        // Creating combo boxes
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> spacingComboBox = new ComboBox<>(spacings);
        spacingComboBox.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        spacingComboBox.setOnAction(this::setAdditionalReinforcementSpacingLabel);

        // Creating buttons
        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        addButton.setOnAction(this::handleAddAdditionalReinforcement);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(this::deleteAdditionalReinforcement);
        deleteButton.getStyleClass().addAll(CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON, CssStyleClasses.HIDDEN);
        StackPane buttonWrapper = new StackPane(addButton, deleteButton);
        buttonWrapper.getStyleClass().add(CssStyleClasses.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);

        // Creating reinforcement layer
        HBox layer = new HBox(layerLabel, diameterComboBox, spacingLabel, spacingComboBox, unitsLabel, buttonWrapper);
        layer.getStyleClass().add(CssStyleClasses.SLAB_REINFORCEMENT_LAYER);

        if (layerIndex > 0) {
            // If not first layer, create an input field for vertical spacing between layers
            PositiveIntegerField verticalSpacingInputField = new PositiveIntegerField();
            verticalSpacingInputField.getStyleClass().add(CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
            Label unitLabel = new Label("mm");
            HBox verticalSpacingHBox = new HBox(verticalSpacingInputField, unitLabel);
            verticalSpacingHBox.getStyleClass().add(CssStyleClasses.SLAB_VERTICAL_SPACING_WRAPPER);
            verticalSpacingsWrapper.getChildren().add(verticalSpacingHBox);
        }

        if (layerIndex == 1) {
            // If second layer show the title for vertical spacings column
            verticalSpacingsTitle.getStyleClass().remove(CssStyleClasses.HIDDEN);
        }

        // Adding reinforcement layer
        layersWrapper.getChildren().add(layer);
    }

    /**
     * Method that removes the last reinforcement layer from a given slab face (layersWrapper).
     *
     * @param layersWrapper           VBox that wraps reinforcement layer fields. It represents slab face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement layers for given slab face - top/bottom
     * @param verticalSpacingsTitle   VBox with the label for the vertical spacings column
     */
    private void deleteReinforcementLayer(VBox layersWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle) {
        List<Node> layers = layersWrapper.getChildren();
        // Removing the last layer
        layers.remove(layers.size() - 1);
        List<Node> verticalSpacings = verticalSpacingsWrapper.getChildren();
        // Removing vertical spacing field
        verticalSpacings.remove(verticalSpacings.size() - 1);
        if (layers.size() == 1) {
            // Hiding title for vertical spacings if only one layer
            verticalSpacingsTitle.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
    }

    /**
     * Method that initializes a reinforcement layer fields with provided data.
     * It requires creating the combo boxes and input fields beforehand.
     *
     * @param layersWrapper           VBox that wraps reinforcement layer fields. It represents slab face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement layers for given slab face - top/bottom
     * @param layerIndex              index of the layer to be initialized
     * @param diameters               diameters of the main reinforcement bars in subsequent layers
     * @param additionalDiameters     diameters of the additional main reinforcement bars that are between main reinforcement.
     *                                If not set up, the diameter is zero.
     * @param spacings                spacings between main reinforcement bar centres
     * @param verticalSpacings        clear vertical spacing between reinforcement layers
     */
    private void initializeReinforcementLayerFields(VBox layersWrapper, VBox verticalSpacingsWrapper,
                                                    int layerIndex, List<Integer> diameters, List<Integer> additionalDiameters,
                                                    List<Integer> spacings, List<Integer> verticalSpacings) {
        List<Node> layers = layersWrapper.getChildren();
        if (layerIndex < layers.size()) {
            // Selecting a layer with given index
            HBox layer = (HBox) layers.get(layerIndex);
            // Selecting combo boxes and initializing main reinforcement
            @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
            @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
            diameterComboBox.setValue(diameters.get(layerIndex));
            spacingComboBox.setValue(spacings.get(layerIndex));

            // Initializing additional reinforcement
            if (additionalDiameters.get(layerIndex) != 0) {
                // Getting add and delete buttons
                StackPane stackPane = (StackPane) layer.lookup("." + CssStyleClasses.ADDITIONAL_SLAB_REINFORCEMENT_BUTTON_WRAPPER);
                Button addButton = (Button) stackPane.lookup("." + CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
                Button deleteButton = (Button) stackPane.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
                // Adding additional reinforcement fields and initializing fields
                addAdditionalReinforcement(addButton, deleteButton, layer);
                @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
                additionalDiameterComboBox.setValue(additionalDiameters.get(layerIndex));
            }
            // Initializing vertical spacings
            if (layerIndex > 0) {
                List<Node> verticalSpacingsFields = verticalSpacingsWrapper.getChildren();
                HBox verticalSpacingHBox = (HBox) verticalSpacingsFields.get(layerIndex - 1);
                PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
                verticalSpacingField.setText(Integer.toString(verticalSpacings.get(layerIndex - 1)));
            }
        }
    }

    /**
     * It that handles "Add" button for a reinforcement layer. It adds additional reinforcement fields to the main reinforcement.
     * It uses method addAdditionalReinforcement.
     *
     * @param actionEvent Add button click event
     */
    public void handleAddAdditionalReinforcement(ActionEvent actionEvent) {
        Button addButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) addButton.getParent();
        Button deleteButton = (Button) stackPane.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();
        addAdditionalReinforcement(addButton, deleteButton, layer);
    }

    /**
     * It adds form fields for setting up additional reinforcement. This includes:
     * - labels
     * - combo box for additional reinforcement reinforcement
     * It hides add button and shows remove button for additional reinforcement
     *
     * @param addButton    add button that handles adding additional reinforcement
     * @param deleteButton remove button that handles removing additional reinforcement
     * @param layer        HBox that wraps layer fields which we add the additional reinforcement to
     */
    public void addAdditionalReinforcement(Button addButton, Button deleteButton, HBox layer) {
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

    /**
     * It gets text for the label of additional reinforcement. This is the same as the main reinforcement spacing.
     * If main reinforcement spacing is not set up it returns an empty string.
     *
     * @param layer HBox that wraps the layer fields
     * @return text for additional reinforcement spacing label
     */
    private String getAdditionalReinforcementSpacingLabel(HBox layer) {
        @SuppressWarnings("unchecked") ComboBox<Integer> spacing = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        return (spacing.getValue() != null) ? String.format("at %d mm", spacing.getValue()) : "";
    }

    /**
     * It handles "Delete" button for a reinforcement layer. It deletes additional reinforcement
     *
     * @param actionEvent Delete button click event
     */
    public void deleteAdditionalReinforcement(ActionEvent actionEvent) {
        // Selecting layer based on the button clicked
        Button deleteButton = (Button) actionEvent.getSource();
        StackPane stackPane = (StackPane) deleteButton.getParent();
        Button addButton = (Button) stackPane.lookup("." + CssStyleClasses.ADD_ADDITIONAL_SLAB_REINFORCEMENT_BUTTON);
        HBox layer = (HBox) stackPane.getParent();

        // Selecting nodes to be deleted
        List<Node> layerNodes = layer.getChildren();
        Label joiningLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_JOINING_LABEL);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        Label spacingLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);

        layerNodes.removeAll(new ArrayList<>(List.of(joiningLabel, diameterComboBox, spacingLabel)));

        // Hiding delete button and showing add button
        deleteButton.getStyleClass().add(CssStyleClasses.HIDDEN);
        addButton.getStyleClass().remove(CssStyleClasses.HIDDEN);
    }

    /**
     * It handles change event for the main reinforcement spacing combo box.
     * It takes a new value of spacing and updates additional reinforcement spacing label.
     *
     * @param actionEvent Main reinforcement spacing combo box value change event
     */
    public void setAdditionalReinforcementSpacingLabel(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        HBox layer = (HBox) node.getParent();
        // Selecting additional reinforcement label
        Label additionalReinforcementLabel = (Label) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_SPACING_LABEL);
        if (additionalReinforcementLabel != null) {
            // Updating label
            additionalReinforcementLabel.setText(getAdditionalReinforcementSpacingLabel(layer));
        }
    }

    /**
     * It handles "Delete" button click event for the top face of the slab.
     * It removes the innermost reinforcement layer. It invokes deleteReinforcementLayer function.
     *
     * @param actionEvent Delete button click event
     */
    public void deleteLayerFromTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopLayers > 1) {
            deleteReinforcementLayer(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle);
            numberOfTopLayers--;
        }
    }

    /**
     * It handles "Add" button click event for the top face of the slab.
     * It invokes addReinforcementLayer function.
     *
     * @param actionEvent Add button click event
     */
    public void addLayerToTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(topLayersVBox, topLayersVerticalSpacingVBox, topLayersVerticalSpacingsTitle, numberOfTopLayers);
            numberOfTopLayers++;
        }
    }

    /**
     * It handles "Delete" button click event for the bottom face of the slab.
     * It removes the innermost reinforcement layer. It invokes deleteReinforcementLayer function.
     *
     * @param actionEvent Delete button click event
     */
    public void deleteLayerFromBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomLayers > 1) {
            deleteReinforcementLayer(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle);
            numberOfBottomLayers--;
        }
    }

    /**
     * It handles "Add" button click event for the bottom face of the slab.
     * It invokes addReinforcementLayer function.
     *
     * @param actionEvent Add button click event
     */
    public void addLayerToBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(bottomLayersVBox, bottomLayersVerticalSpacingVBox, bottomLayersVerticalSpacingsTitle, numberOfBottomLayers);
            numberOfBottomLayers++;
        }
    }

    /**
     * Method is used to check if layer fields are set up - not empty.
     * It also checks if vertical spacing between considered layer and a previous layer is set up - if applicable.
     *
     * @param layersWrapper           VBox that wraps reinforcement layer fields. It represents slab face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement layers for given slab face - top/bottom
     * @param layerIndex              index of the layer to be validated
     * @param layersLocation          text that describes which layer is being checked - top or bottom
     * @return List of validation messages
     */
    private List<String> getValidationMessagesIfLayerFieldsAreEmpty(VBox layersWrapper, VBox verticalSpacingsWrapper, int layerIndex, String layersLocation) {
        List<String> validationMessages = new ArrayList<>();
        String layerLocation = layersLocation.toLowerCase();
        // Selecting layer fields
        HBox layer = (HBox) layersWrapper.getChildren().get(layerIndex);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_DIAMETER_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> spacingComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_REINFORCEMENT_SPACING_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> additionalDiameterComboBox = (ComboBox<Integer>) layer.lookup("." + CssStyleClasses.SLAB_ADDITIONAL_REINFORCEMENT_DIAMETER);
        // Checking if layer fields are set up
        if (diameterComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter bar diameter for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        if (spacingComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter bar spacing for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        if (additionalDiameterComboBox != null && additionalDiameterComboBox.getValue() == null) {
            validationMessages.add(String.format("Please enter alternate bar diameter for %s %s layer.", layerLabels.get(layerIndex), layerLocation));
        }
        // Checking if vertical spacing is set up between this and previous layer
        if (layerIndex > 0) {
            HBox verticalSpacingHBox = (HBox) verticalSpacingsWrapper.getChildren().get(layerIndex - 1);
            PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.SLAB_VERTICAL_SPACING_FIELD);
            if (verticalSpacingField.getText().equals("")) {
                validationMessages.add(String.format("Please enter vertical spacing between %s and %s %s layer.", layerLabels.get(layerIndex - 1), layerLabels.get(layerIndex), layerLocation));
            }
        }
        return validationMessages;
    }

    /**
     * Method loops through all layers for the top and bottom face and checks if the form fields are set up - not empty.
     * It also checks if all vertical spacings fields are filled in. It uses getValidationMessagesIfLayerFieldsAreEmpty method.
     *
     * @return List of validation messages
     */
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
