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
        IntStream.iterate(25, spacing -> spacing <= 750, spacing -> spacing + 25).forEach(spacingsArray::add);
        spacings = FXCollections.observableList(spacingsArray);
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);
        layerLabels = new ArrayList<>(List.of("first", "second", "third", "fourth", "fifth", "sixth"));
    }

    @FXML
    public void initialize() {
        addReinforcementLayer(topLayers, 0);
//        addReinforcementLayer(topLayers, 1);
//        addReinforcementLayer(topLayers, 2);
//        addReinforcementLayer(topLayers, 3);
//        addReinforcementLayer(topLayers, 4);
//        addReinforcementLayer(topLayers, 5);
        addReinforcementLayer(bottomLayers, 0);
//        addReinforcementLayer(bottomLayers, 1);
//        addReinforcementLayer(bottomLayers, 2);
//        addReinforcementLayer(bottomLayers, 3);
//        addReinforcementLayer(bottomLayers, 4);
//        addReinforcementLayer(bottomLayers, 5);
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
        Button deleteButton = (Button) stackPane.getChildren().get(0);
        deleteButton.getStyleClass().remove("hidden");
    }

    public void deleteAdditionalReinforcement(ActionEvent actionEvent) {
        Button deleteButton = (Button) actionEvent.getSource();
        deleteButton.getStyleClass().add("hidden");
        StackPane stackPane = (StackPane) deleteButton.getParent();
        Button addButton = (Button) stackPane.getChildren().get(1);
        addButton.getStyleClass().remove("hidden");
    }

    private void addReinforcementLayer(VBox target, int layerIndex) {
        HBox layer = new HBox();
        layer.getStyleClass().add("layer");
        Label layerLabel = new Label(Utility.capitalize(layerLabels.get(layerIndex)) + " layer:");
        layerLabel.setPrefWidth(85);
        Label spacingLabel = new Label("at");
        Label unitsLabel = new Label("mm");
        ComboBox<Integer> diameterComboBox = new ComboBox<>();
        diameterComboBox.setPrefWidth(55);
        ComboBox<Integer> spacingComboBox = new ComboBox<>();
        spacingComboBox.setPrefWidth(60);
        StackPane buttonWrapper = new StackPane();
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("hidden");
        diameterComboBox.setItems(diameters);
        spacingComboBox.setItems(spacings);

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
}
