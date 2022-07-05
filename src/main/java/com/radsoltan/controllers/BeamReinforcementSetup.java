package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.CssStyleClasses;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.Project;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
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
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
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
    @FXML
    public ComboBox<Integer> shearLinkDiameter;
    @FXML
    public ComboBox<Integer> shearLinkLegs;
    @FXML
    public PositiveIntegerField shearLinksSpacing;
    @FXML
    public PositiveIntegerField shearLinkYieldStrength;

    private int numberOfTopRows;
    private int numberOfBottomRows;
    private final Project project;
    private final Reinforcement beamReinforcement;
    private final ObservableList<Integer> diameters;
    private final ObservableList<Integer> mainBarNumbers;
    private final ObservableList<Integer> additionalBarNumbers;
    private final ObservableList<Integer> shearLinkDiametersList;
    private final ObservableList<Integer> shearLinkLegsList;
    private final ArrayList<String> rowLabels;

    /**
     * Constructor. It gets project instance and using the instance it gets the reinforcement.
     * It also creates lists for reinforcement diameters and lists with number of bars.
     */
    public BeamReinforcementSetup() {
        project = Project.getInstance();

//        beamReinforcement = project.getReinforcement();

        beamReinforcement = new BeamReinforcement(
                List.of(List.of(32, 10, 12, 10, 32), List.of(25, 8, 25), List.of(20, 20), List.of(10, 10)),
                List.of(50, 40, 50),
                List.of(List.of(16, 8, 8, 8, 16), List.of(10, 8, 10), List.of(12, 12)),
                List.of(40, 40),
                new ShearLinks(500, 8, 200, 3)
        );

        // Creating lists for bar numbers
        List<Integer> barNumberList = new ArrayList<>();
        IntStream.iterate(Constants.BEAM_ROW_MAIN_BAR_MIN_COUNT, count -> count <= Constants.BEAM_ROW_BAR_MAX_COUNT, count -> count + 1)
                .forEach(barNumberList::add);
        mainBarNumbers = FXCollections.observableList(barNumberList);

        List<Integer> additionalBarNumberList = new ArrayList<>(barNumberList);
        // Additional rebar can start from 1
        additionalBarNumberList.add(0, 1);
        additionalBarNumbers = FXCollections.observableArrayList(additionalBarNumberList);

        // Creating list for bar diameters
        diameters = FXCollections.observableList(Constants.BAR_DIAMETERS);

        // List for reinforcement rows labels
        rowLabels = Constants.ORDINAL_LABELS;

        // Lists for shear link diameters and legs
        shearLinkDiametersList = FXCollections.observableList(Constants.BAR_DIAMETERS);
        shearLinkLegsList = FXCollections.observableList(Constants.SHEAR_LEGS);
    }

    /**
     * Initialize method that is run after calling the constructor. If no beam reinforcement was set up it creates one empty bottom row and one empty top row.
     * If beam reinforcement is found in project instance, it creates required number of reinforcement rows and initializes the fields.
     */
    @FXML
    public void initialize() {
        // Filling drop down lists for shear links
        shearLinkDiameter.getItems().addAll(shearLinkDiametersList);
        shearLinkLegs.getItems().addAll(shearLinkLegsList);

        if (beamReinforcement == null) {
            // If no reinforcement set up before, create one top and one bottom row
            addReinforcementRow(topReinforcementVBox, topVerticalSpacingVBox, topReinforcementVerticalSpacingsTitle, 0);
            addReinforcementRow(bottomReinforcementVBox, bottomVerticalSpacingVBox, bottomReinforcementVerticalSpacingsTitle, 0);
            numberOfTopRows = 1;
            numberOfBottomRows = 1;
        } else if (this.beamReinforcement instanceof BeamReinforcement) {
            BeamReinforcement beamReinforcement = (BeamReinforcement) this.beamReinforcement;
            numberOfTopRows = beamReinforcement.getTopDiameters().size();
            numberOfBottomRows = beamReinforcement.getBottomDiameters().size();

            List<List<Integer>> topDiameters = beamReinforcement.getTopDiameters();
            List<Integer> topVerticalSpacings = beamReinforcement.getTopVerticalSpacings();
            List<List<Integer>> bottomDiameters = beamReinforcement.getBottomDiameters();
            List<Integer> bottomVerticalSpacings = beamReinforcement.getBottomVerticalSpacings();
            ShearLinks shearLinks = beamReinforcement.getShearLinks();

            // Initializing top reinforcement
            IntStream.range(0, numberOfTopRows).forEach(i -> {
                addReinforcementRow(topReinforcementVBox, topVerticalSpacingVBox, topReinforcementVerticalSpacingsTitle, i);
                initializeReinforcementRowFields(topReinforcementVBox, topVerticalSpacingVBox, i, topDiameters, topVerticalSpacings);
            });

            // Initializing bottom reinforcement
            IntStream.range(0, numberOfBottomRows).forEach(i -> {
                addReinforcementRow(bottomReinforcementVBox, bottomVerticalSpacingVBox, bottomReinforcementVerticalSpacingsTitle, i);
                initializeReinforcementRowFields(bottomReinforcementVBox, bottomVerticalSpacingVBox, i, bottomDiameters, bottomVerticalSpacings);
            });

            // Initializing shear links
            shearLinkDiameter.setValue(shearLinks.getDiameter());
            shearLinkLegs.setValue(shearLinks.getLegs());
            shearLinksSpacing.setText(Integer.toString(shearLinks.getSpacing()));
            shearLinkYieldStrength.setText(Integer.toString(shearLinks.getYieldStrength()));
        } else {
            // Show error if invalid reinforcement
            showAlertBox(UIText.INVALID_BEAM_REINFORCEMENT, AlertKind.ERROR);
        }
        Platform.runLater(() -> container.requestFocus());
    }

    /**
     * @param actionEvent
     * @throws IOException
     */
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessages = getValidationMessagesForEmptyFields();

        System.out.println(validationMessages);
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
        Label label = new Label("x");
        // Creating combo boxes
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> barNumberComboBox = new ComboBox<>(mainBarNumbers);
        barNumberComboBox.getStyleClass().add(CssStyleClasses.BEAM_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);

        // Creating buttons
        Button addButton = new Button("Add");
        addButton.getStyleClass().add(CssStyleClasses.ADD_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
        addButton.setOnAction(this::handleAddAdditionalReinforcement);
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll(CssStyleClasses.DELETE_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON, CssStyleClasses.HIDDEN);
        deleteButton.setOnAction(this::deleteAdditionalReinforcement);
        deleteButton.setManaged(false);
        HBox buttonWrapper = new HBox(addButton, deleteButton);
        buttonWrapper.getStyleClass().add(CssStyleClasses.ADDITIONAL_BEAM_REINFORCEMENT_BUTTON_WRAPPER);

        // Creating reinforcement row
        HBox row = new HBox(rowLabel, barNumberComboBox, label, diameterComboBox, buttonWrapper);
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
     * It that handles "Add" button for a reinforcement row. It adds additional reinforcement fields to the main reinforcement.
     * It uses method addAdditionalReinforcement.
     *
     * @param actionEvent Add button click event
     */
    public void handleAddAdditionalReinforcement(ActionEvent actionEvent) {
        Button addButton = (Button) actionEvent.getSource();
        HBox hBox = (HBox) addButton.getParent();
        Button deleteButton = (Button) hBox.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
        HBox row = (HBox) hBox.getParent();
        addAdditionalReinforcement(addButton, deleteButton, row);
    }

    /**
     * It adds form fields for setting up additional reinforcement. This includes:
     * - labels
     * - combo box for number of additional bars
     * - combo box for diameter of additional bars
     * It hides add button and shows remove button for additional reinforcement
     *
     * @param addButton    add button that handles adding additional reinforcement
     * @param deleteButton remove button that handles removing additional reinforcement
     * @param row          HBox that wraps rows fields which we add the additional reinforcement to
     */
    public void addAdditionalReinforcement(Button addButton, Button deleteButton, HBox row) {
        Label joiningLabel = new Label(" + ");
        joiningLabel.getStyleClass().add(CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_JOINING_LABEL);
        Label label = new Label("x");
        label.getStyleClass().add(CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_TIMES_LABEL);

        // Creating combo boxes
        ComboBox<Integer> diameterComboBox = new ComboBox<>(diameters);
        diameterComboBox.getStyleClass().add(CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_DIAMETER_COMBO_BOX);
        ComboBox<Integer> barNumberComboBox = new ComboBox<>(additionalBarNumbers);
        barNumberComboBox.getStyleClass().add(CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);

        List<Node> additionalReinforcementNodes = new ArrayList<>(List.of(joiningLabel, barNumberComboBox, label, diameterComboBox));
        row.getChildren().addAll(row.getChildren().size() - 1, additionalReinforcementNodes);

        if (deleteButton.isManaged()) {
            // Hiding add button if second additional reinforcement is added
            addButton.getStyleClass().add(CssStyleClasses.HIDDEN);
            addButton.setManaged(false);
        }
        deleteButton.getStyleClass().remove(CssStyleClasses.HIDDEN);
        deleteButton.setManaged(true);
    }

    /**
     * It handles "Delete" button for a reinforcement rows. It deletes additional reinforcement
     *
     * @param actionEvent Delete button click event
     */
    public void deleteAdditionalReinforcement(ActionEvent actionEvent) {
        // Selecting row based on the button clicked
        Button deleteButton = (Button) actionEvent.getSource();
        HBox hBox = (HBox) deleteButton.getParent();
        Button addButton = (Button) hBox.lookup("." + CssStyleClasses.ADD_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
        HBox row = (HBox) hBox.getParent();

        // Selecting nodes to be deleted
        List<Node> rowNodes = row.getChildren();
        Label joiningLabel = (Label) row.lookup("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_JOINING_LABEL);
        Label timesLabel = (Label) row.lookup("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_TIMES_LABEL);
        @SuppressWarnings("unchecked") ComboBox<Integer> barNumberComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_DIAMETER_COMBO_BOX);

        rowNodes.removeAll(new ArrayList<>(List.of(joiningLabel, barNumberComboBox, timesLabel, diameterComboBox)));

        if (addButton.isManaged()) {
            // Hiding delete button if second additional reinforcement is added
            deleteButton.getStyleClass().add(CssStyleClasses.HIDDEN);
            deleteButton.setManaged(false);
        }
        addButton.getStyleClass().remove(CssStyleClasses.HIDDEN);
        addButton.setManaged(true);
    }

    /**
     * Method that initializes a reinforcement layer fields with provided data.
     * It requires creating the combo boxes and input fields beforehand.
     *
     * @param rowsWrapper             VBox that wraps reinforcement row fields. It represents beam face - top or bottom
     * @param verticalSpacingsWrapper VBox that wraps vertical spacing for reinforcement rows for given beam face - top/bottom
     * @param rowIndex                index of the layer to be initialized
     * @param diameters               list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings   clear vertical spacings between rows in mm
     */
    private void initializeReinforcementRowFields(VBox rowsWrapper, VBox verticalSpacingsWrapper, int rowIndex, List<List<Integer>> diameters, List<Integer> clearVerticalSpacings) {
        List<Node> rows = rowsWrapper.getChildren();
        if (rowIndex < rows.size()) {
            // Selecting a row with given index
            HBox row = (HBox) rows.get(rowIndex);

            List<Integer> barsInRow = diameters.get(rowIndex);
            List<Integer> distinctBars = barsInRow.stream().distinct().collect(Collectors.toList());

            IntStream.range(0, distinctBars.size()).forEach(i -> {
                int barDiameter = distinctBars.get(i);
                int numberOfBars = Collections.frequency(barsInRow, barDiameter);
                if (i == 0) {
                    // Initializing primary rebar, distinct bars with index 0 are primary bars
                    @SuppressWarnings("unchecked") ComboBox<Integer> numberOfBarsComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);
                    @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_REINFORCEMENT_DIAMETER_COMBO_BOX);
                    numberOfBarsComboBox.setValue(numberOfBars);
                    diameterComboBox.setValue(barDiameter);
                } else {
                    // Initializing additional rebar
                    HBox hBox = (HBox) row.lookup("." + CssStyleClasses.ADDITIONAL_BEAM_REINFORCEMENT_BUTTON_WRAPPER);
                    Button addButton = (Button) hBox.lookup("." + CssStyleClasses.ADD_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
                    Button deleteButton = (Button) hBox.lookup("." + CssStyleClasses.DELETE_ADDITIONAL_BEAM_REINFORCEMENT_BUTTON);
                    // Adding additional reinforcement fields and initializing fields
                    addAdditionalReinforcement(addButton, deleteButton, row);
                    List<Node> numberOfBarsComboBoxes = new ArrayList<>(row.lookupAll("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_BAR_NUMBER_COMBO_BOX));
                    List<Node> diameterComboBoxes = new ArrayList<>(row.lookupAll("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_DIAMETER_COMBO_BOX));

                    @SuppressWarnings("unchecked") ComboBox<Integer> numberOfBarsComboBox = (ComboBox<Integer>) numberOfBarsComboBoxes.get(i - 1);
                    @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) diameterComboBoxes.get(i - 1);

                    numberOfBarsComboBox.setValue(numberOfBars);
                    diameterComboBox.setValue(barDiameter);
                }
            });

            // Initializing vertical spacings
            if (rowIndex > 0) {
                List<Node> verticalSpacingsFields = verticalSpacingsWrapper.getChildren();
                HBox verticalSpacingHBox = (HBox) verticalSpacingsFields.get(rowIndex - 1);
                PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.BEAM_VERTICAL_SPACING_FIELD);
                verticalSpacingField.setText(Integer.toString(clearVerticalSpacings.get(rowIndex - 1)));
            }
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


    private List<String> getValidationMessagesIfRowFieldsAreEmpty(VBox rowsWrapper, VBox verticalSpacingsWrapper, int rowIndex, String rowsLocation) {
        List<String> validationMessages = new ArrayList<>();

        String rowLocation = rowsLocation.toLowerCase();
        // Selecting layer fields
        HBox row = (HBox) rowsWrapper.getChildren().get(rowIndex);
        @SuppressWarnings("unchecked") ComboBox<Integer> numberOfBarsComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_REINFORCEMENT_BAR_NUMBER_COMBO_BOX);
        @SuppressWarnings("unchecked") ComboBox<Integer> diameterComboBox = (ComboBox<Integer>) row.lookup("." + CssStyleClasses.BEAM_REINFORCEMENT_DIAMETER_COMBO_BOX);

        List<Node> numberOfAdditionalBarsComboBoxes = new ArrayList<>(row.lookupAll("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_BAR_NUMBER_COMBO_BOX));
        List<Node> additionalDiameterComboBoxes = new ArrayList<>(row.lookupAll("." + CssStyleClasses.BEAM_ADDITIONAL_REINFORCEMENT_DIAMETER_COMBO_BOX));

        // Checking if number of primary bars is set up
        if (numberOfBarsComboBox.getValue() == null) {
            validationMessages.add(String.format("Please select number of bars for %s %s row", rowLabels.get(rowIndex), rowLocation));
        }
        // Checking if primary diameter is set up
        if (diameterComboBox.getValue() == null) {
            validationMessages.add(String.format("Please select bar diameter for %s %s row.", rowLabels.get(rowIndex), rowLocation));
        }
        // Checking additional reinforcement
        numberOfAdditionalBarsComboBoxes.forEach(node -> {
            @SuppressWarnings("unchecked") ComboBox<Integer> comboBox = (ComboBox<Integer>) node;
            if (comboBox.getValue() == null) {
                validationMessages.add(String.format("Please select number of additional bars for %s %s row", rowLabels.get(rowIndex), rowLocation));
            }
        });
        additionalDiameterComboBoxes.forEach(node -> {
            @SuppressWarnings("unchecked") ComboBox<Integer> comboBox = (ComboBox<Integer>) node;
            if (comboBox.getValue() == null) {
                validationMessages.add(String.format("Please select diameter of additional bars for %s %s row", rowLabels.get(rowIndex), rowLocation));
            }
        });
        // Checking if vertical spacing is set up between this and previous layer
        if (rowIndex > 0) {
            HBox verticalSpacingHBox = (HBox) verticalSpacingsWrapper.getChildren().get(rowIndex - 1);
            PositiveIntegerField verticalSpacingField = (PositiveIntegerField) verticalSpacingHBox.lookup("." + CssStyleClasses.BEAM_VERTICAL_SPACING_FIELD);
            if (verticalSpacingField.getText().equals("")) {
                validationMessages.add(String.format("Please enter vertical spacing between %s and %s %s row.", rowLabels.get(rowIndex - 1), rowLabels.get(rowIndex), rowLocation));
            }
        }

        return validationMessages;
    }

    /**
     * Method is used to check if layer fields are set up - not empty.
     * It also checks if vertical spacing between considered layer and a previous layer is set up - if applicable.
     *
     * @return List of validation messages
     */
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        List<String> validationMessages = new ArrayList<>();
        for (int i = 0; i < numberOfTopRows; i++) {
            validationMessages.addAll(getValidationMessagesIfRowFieldsAreEmpty(topReinforcementVBox, topVerticalSpacingVBox, i, "top"));
        }
        for (int j = 0; j < numberOfBottomRows; j++) {
            validationMessages.addAll(getValidationMessagesIfRowFieldsAreEmpty(bottomReinforcementVBox, bottomVerticalSpacingVBox, j, "bottom"));
        }
        return validationMessages;
    }
}
