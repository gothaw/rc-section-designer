package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import com.radsoltan.util.Utility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Array;
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
    public VBox bottomLayers;
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
        layerLabels = Constants.LAYERS_ORDINAL_NUMBERS;
    }

    @FXML
    public void initialize() {
        if (slabReinforcement == null) {
            addReinforcementLayer(topLayers, 0);
            addReinforcementLayer(bottomLayers, 0);
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

    public void addAdditionalReinforcement(ActionEvent actionEvent) {
        Button addButton = (Button) actionEvent.getSource();
        addButton.getStyleClass().add("hidden");
        StackPane stackPane = (StackPane) addButton.getParent();
        HBox layer = (HBox) stackPane.getParent();
        Label joiningLabel = new Label(" + ");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.setPrefWidth(55);

        List<Node> layerItems = layer.getChildren();

        layerItems.add(layerItems.size() - 1, joiningLabel);
        layerItems.add(layerItems.size() - 1, diameterComboBox);



        Button deleteButton = (Button) stackPane.lookup(".delete-additional-reinforcement");
        deleteButton.getStyleClass().remove("hidden");
    }

    public void deleteAdditionalReinforcement(ActionEvent actionEvent) {
        Button deleteButton = (Button) actionEvent.getSource();
        deleteButton.getStyleClass().add("hidden");
        StackPane stackPane = (StackPane) deleteButton.getParent();
        Button addButton = (Button) stackPane.lookup(".add-additional-reinforcement");
        addButton.getStyleClass().remove("hidden");
    }

    private void addReinforcementLayer(VBox target, int layerIndex) {
        HBox layer = new HBox();
        layer.getStyleClass().add("layer");
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.setPrefWidth(85);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.setPrefWidth(55);
        ComboBox<Integer> spacingComboBox = new ComboBox<>(spacings);
        spacingComboBox.setPrefWidth(60);
        StackPane buttonWrapper = new StackPane();
        Button addButton = new Button("Add");
        addButton.getStyleClass().add("add-additional-reinforcement");
        addButton.setOnAction(this::addAdditionalReinforcement);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(this::deleteAdditionalReinforcement);
        deleteButton.getStyleClass().addAll("delete-additional-reinforcement", "hidden");

        buttonWrapper.getChildren().add(addButton);
        buttonWrapper.getChildren().add(deleteButton);

        List<Node> layerNodes = layer.getChildren();
        layerNodes.add(layerLabel);
        layerNodes.add(diameterComboBox);
        layerNodes.add(spacingLabel);
        layerNodes.add(spacingComboBox);
        layerNodes.add(unitsLabel);
        layerNodes.add(buttonWrapper);

        target.getChildren().add(layer);
    }

    private void deleteReinforcementLayer(VBox target) {
        List<Node> layers = target.getChildren();
        layers.remove(layers.size() - 1);
    }

    public void deleteTopLayer(ActionEvent actionEvent) {
        if (numberOfTopLayers > 1) {
            deleteReinforcementLayer(topLayers);
            numberOfTopLayers--;
        }
    }

    public void addTopLayer(ActionEvent actionEvent) {
        if (numberOfTopLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(topLayers, numberOfTopLayers);
            numberOfTopLayers++;
        }
    }

    public void deleteBottomLayer(ActionEvent actionEvent) {
        if (numberOfBottomLayers > 1) {
            deleteReinforcementLayer(bottomLayers);
            numberOfBottomLayers--;
        }
    }

    public void addBottomLayer(ActionEvent actionEvent) {
        if (numberOfBottomLayers < Constants.MAX_NUMBER_OF_LAYERS) {
            addReinforcementLayer(bottomLayers, numberOfBottomLayers);
            numberOfBottomLayers++;
        }
    }
}
