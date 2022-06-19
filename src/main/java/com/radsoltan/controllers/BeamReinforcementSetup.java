package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.CssStyleClasses;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.util.AlertKind;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

// TODO: 17/07/2021 Class is not finished. See todos below for what these do at the moment.
public class BeamReinforcementSetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public VBox topReinforcementVerticalSpacingsTitle;
    @FXML
    public VBox topReinforcementVBox;
    @FXML
    public VBox topVerticalSpacingVBox;
    @FXML
    public Button addTopRowButton;
    @FXML
    public Button deleteTopRowButton;
    @FXML
    public VBox bottomReinforcementVerticalSpacingsTitle;
    @FXML
    public VBox bottomReinforcementVBox;
    @FXML
    public VBox bottomVerticalSpacingVBox;
    @FXML
    public Button addBottomRowButton;
    @FXML
    public Button deleteBottomRowButton;

    private int numberOfTopRows;
    private int numberOfBottomRows;
    private final Project project;
    private final Reinforcement beamReinforcement;
    private final ObservableList<Integer> diameters;
    private final ObservableList<Integer> barNumbers;
    private final ArrayList<String> rowLabels;

    /**
     * Constructor. It gets project instance and using the instance it gets the reinforcement.
     * It also creates lists for reinforcement diameters and list with number of bars.
     */
    public BeamReinforcementSetup() {
        project = Project.getInstance();

        beamReinforcement = project.getReinforcement();

        // Creating list for bar numbers
        List<Integer> barNumberList = new ArrayList<>();
        IntStream.iterate(1, count -> count <= Constants.BEAM_ROW_BAR_MAX_COUNT, count -> count + 1)
                .forEach(barNumberList::add);
        barNumbers = FXCollections.observableList(barNumberList);

        // Creating list for bar diameters
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);

        // List for reinforcement rows labels
        rowLabels = Constants.ORDINAL_LABELS;
    }

    @FXML
    public void initialize() {
        if (beamReinforcement == null) {
            // If no reinforcement set up before, create one top and one bottom row
            addReinforcementRow(topReinforcementVBox, topVerticalSpacingVBox, topReinforcementVerticalSpacingsTitle, 0);
            addReinforcementRow(bottomReinforcementVBox, bottomVerticalSpacingVBox, bottomReinforcementVerticalSpacingsTitle, 0);
            numberOfTopRows = 1;
            numberOfBottomRows = 1;
        } else if (this.beamReinforcement instanceof BeamReinforcement) {

        } else {
            // Show error if invalid reinforcement
            showAlertBox(UIText.INVALID_BEAM_REINFORCEMENT, AlertKind.ERROR);
        }
        Platform.runLater(() -> container.requestFocus());
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    /**
     * Method that adds a reinforcement row to given beam face (rowsWrapper). It adds:
     * - combo box for number of bars
     * - combo box for bar diameter
     * - "Add" button that handles adding additional bars
     * - "Delete" button that handles removing additional bars
     * - row labels for example "3rd row:"
     * - vertical spacing input field
     *
     * @param rowsWrapper             VBox that wraps reinforcement row fields. It represents beam face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement rows for given beam face - top/bottom
     * @param verticalSpacingsTitle   VBox with the label for the vertical spacings column
     * @param rowIndex                Index of the row to be added
     */
    private void addReinforcementRow(VBox rowsWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle, int rowIndex) {
        // Creating text labels
        Label rowLabel = new Label(Utility.capitalize(rowLabels.get(rowIndex)) + " row:");
        rowLabel.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_ROW_LABEL);
        Label rowMiddleLabel = new Label("x");
        // Creating combo boxes
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> barNumberComboBox = new ComboBox<>(barNumbers);
        barNumberComboBox.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);

        // Creating buttons
        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyleClasses.ADD_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll(CssStyleClasses.DELETE_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON, CssStyleClasses.HIDDEN);
        StackPane buttonWrapper = new StackPane(addButton, deleteButton);
        buttonWrapper.getStyleClass().add(CssStyleClasses.ADDITIONAL_BEAM_REINFORCEMENT_BUTTON_WRAPPER);

        // Creating reinforcement row
        HBox row = new HBox(rowLabel, barNumberComboBox, rowMiddleLabel, diameterComboBox, buttonWrapper);
        row.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_ROW);

        if (rowIndex > 0) {
            // If not first row, create an input field for vertical spacing between rows
            PositiveIntegerField verticalSpacingInputField = new PositiveIntegerField();
            verticalSpacingInputField.getStyleClass().add(CssStyleClasses.BEAM_VERTICAL_SPACING_FIELD);
            Label unitLabel = new Label("mm");
            HBox verticalSpacingHBox = new HBox(verticalSpacingInputField, unitLabel);
            verticalSpacingHBox.getStyleClass().add(CssStyleClasses.BEAM_VERTICAL_SPACING_WRAPPER);
            verticalSpacingsWrapper.getChildren().add(verticalSpacingHBox);
        }

        if (rowIndex == 1) {
            // If second row show the title for vertical spacings column
            verticalSpacingsTitle.getStyleClass().remove(CssStyleClasses.HIDDEN);
        }

        // Adding reinforcement row
        rowsWrapper.getChildren().add(row);
    }

    /**
     * Method that removes the last reinforcement row from a given beam face (rowsWrapper).
     *
     * @param rowsWrapper             VBox that wraps reinforcement row fields. It represents beam face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement rows for given beam face - top/bottom
     * @param verticalSpacingsTitle   VBox with the label for the vertical spacings column
     */
    private void deleteReinforcementRow(VBox rowsWrapper, VBox verticalSpacingsWrapper, VBox verticalSpacingsTitle) {
        List<Node> rows = rowsWrapper.getChildren();
        // Removing the last row
        rows.remove(rows.size() - 1);
        List<Node> verticalSpacings = verticalSpacingsWrapper.getChildren();
        // Removing vertical spacing field
        verticalSpacings.remove(verticalSpacings.size() - 1);
        if (rows.size() == 1) {
            // Hiding title for vertical spacings if only one row
            verticalSpacingsTitle.getStyleClass().add(CssStyleClasses.HIDDEN);
        }
    }

    /**
     * It handles "Add" button click event for the top face of the beam.
     * It adds reinforcement row top reinforcement. It invokes addReinforcementRow function.
     *
     * @param actionEvent Add button click event
     */
    public void addRowToTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopRows < Constants.MAX_NUMBER_OF_ROWS) {
            addReinforcementRow(topReinforcementVBox, topVerticalSpacingVBox, topReinforcementVerticalSpacingsTitle, numberOfTopRows);
            numberOfTopRows++;
        }
    }

    /**
     * It handles "Delete" button click event for the top face of the beam.
     * It removes the bottommost reinforcement row. It invokes deleteReinforcementRow function.
     *
     * @param actionEvent Delete button click event
     */
    public void deleteRowFromTopReinforcement(ActionEvent actionEvent) {
        if (numberOfTopRows > 1) {
            deleteReinforcementRow(topReinforcementVBox, topVerticalSpacingVBox, topReinforcementVerticalSpacingsTitle);
            numberOfTopRows--;
        }
    }

    /**
     * It handles "Add" button click event for the bottom face of the beam.
     * It adds reinforcement row bottom reinforcement. It invokes addReinforcementRow function.
     *
     * @param actionEvent Add button click event
     */
    public void addRowToBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomRows < Constants.MAX_NUMBER_OF_ROWS) {
            addReinforcementRow(bottomReinforcementVBox, bottomVerticalSpacingVBox, bottomReinforcementVerticalSpacingsTitle, numberOfBottomRows);
            numberOfBottomRows++;
        }
    }

    /**
     * It handles "Delete" button click event for the bottom face of the beam.
     * It removes the topmost reinforcement row. It invokes deleteReinforcementRow function.
     *
     * @param actionEvent Delete button click event
     */
    public void deleteRowFromBottomReinforcement(ActionEvent actionEvent) {
        if (numberOfBottomRows > 1) {
            deleteReinforcementRow(bottomReinforcementVBox, bottomVerticalSpacingVBox, bottomReinforcementVerticalSpacingsTitle);
            numberOfBottomRows--;
        }
    }

    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        return null;
    }
}
