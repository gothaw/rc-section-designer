package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.*;
import com.radsoltan.util.AlertKind;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for setting up beam geometry view.
 * This includes instantiating Geometry and Rectangle objects.
 * <p>
 * Note: T Section and L section geometry setup to be implemented in this controller.
 */
public class BeamGeometrySetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public StackPane beamImageWrapper;
    @FXML
    public Canvas beamImage;
    @FXML
    public PositiveIntegerField beamWidth;
    @FXML
    public PositiveIntegerField beamDepth;

    private final Project project;
    private final Geometry geometry;

    private static final double BEAM_IMAGE_HORIZONTAL_RATIO = 0.2;
    private static final double BEAM_IMAGE_VERTICAL_RATIO = 0.7;
    private static final double BEAM_IMAGE_DIMENSION_LINES_SCALE = 0.75;

    /**
     * Constructor. It gets project instance and using the instance it gets the geometry.
     */
    public BeamGeometrySetup() {
        project = Project.getInstance();
        this.geometry = project.getGeometry();
    }

    /**
     * Initialize method that is run after calling the constructor.
     * If bea, geometry was instantiated before, it takes beam dimensions and sets them to relevant input fields.
     * It also invokes draw method that draws a typical beam cross section.
     */
    @FXML
    public void initialize() {
        if (geometry != null) {
            Section section = geometry.getSection();
            if (section instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) section;

                beamWidth.setText(Integer.toString(rectangle.getWidth()));
                beamDepth.setText(Integer.toString(rectangle.getDepth()));
            } else {
                showAlertBox(UIText.INVALID_BEAM_GEOMETRY, AlertKind.ERROR);
            }
        }
        this.draw();
        Platform.runLater(() -> container.requestFocus());
    }

    /**
     * Method that handles ok button click. It requires beam dimensions to be set (not empty) before redirecting to the main controller.
     * If non empty, it creates Rectangle object and Geometry objects. It then saves Geometry object in the project instance.
     *
     * @param actionEvent Ok button click event
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessages = getValidationMessagesForEmptyFields();
        if (validationMessages.isEmpty()) {
            Rectangle rectangle = new Rectangle(Integer.parseInt(beamWidth.getText()), Integer.parseInt(beamDepth.getText()));
            Geometry geometry = new Geometry(rectangle);

            project.setGeometry(geometry);
            project.resetResults();

            App.setRoot("primary");
        } else {
            showAlertBox(validationMessages.get(0), AlertKind.INFO);
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
     * Draws a typical beam image along with dimension lines.
     * Beam image is drawn relatively to the canvas size using ratios defined in constants.
     */
    private void draw() {
        GraphicsContext graphicsContext = beamImage.getGraphicsContext2D();
        double canvasWidth = beamImage.getWidth();
        double canvasHeight = beamImage.getHeight();
        int beamWidth = (int) (BEAM_IMAGE_HORIZONTAL_RATIO * canvasWidth);
        int beamDepth = (int) (BEAM_IMAGE_VERTICAL_RATIO * canvasHeight);
        double beamLeftEdgeX = 0.5 * canvasWidth - 0.5 * beamWidth;
        double beamTopEdgeY = 0.5 * canvasHeight - 0.5 * beamDepth;
        double beamRightEdgeX = beamLeftEdgeX + beamWidth;
        double beamBottomEdgeY = beamTopEdgeY + beamDepth;

        Rectangle rectangle = new Rectangle(
                beamWidth,
                beamDepth,
                graphicsContext,
                Color.BLACK,
                Color.LIGHTGRAY,
                beamLeftEdgeX,
                beamTopEdgeY
        );

        try {
            rectangle.draw();

//            // Draw Horizontal Dimension Line
//            HorizontalDimensionLine horizontalDimensionLine = new HorizontalDimensionLine(
//                    "1000",
//                    Color.BLACK,
//                    graphicsContext,
//                    slabLeftEdgeX,
//                    slabRightEdgeX,
//                    slabTopEdgeY,
//                    -DimensionLine.DEFAULT_OFFSET,
//                    SLAB_IMAGE_DIMENSION_LINES_SCALE
//            );
//            horizontalDimensionLine.draw();
//
//            // Draw Vertical Dimension Line
//            VerticalDimensionLine verticalDimensionLine = new VerticalDimensionLine(
//                    "t",
//                    Color.BLACK,
//                    graphicsContext,
//                    slabTopEdgeY,
//                    slabBottomEdgeY,
//                    slabLeftEdgeX,
//                    -DimensionLine.DEFAULT_OFFSET,
//                    SLAB_IMAGE_DIMENSION_LINES_SCALE,
//                    false
//            );
//            verticalDimensionLine.draw();
        } catch (IllegalArgumentException e) {
            // Showing warning if slab strip is instantiated using wrong constructor - no graphics context etc.
            showAlertBox(e.getMessage(), AlertKind.WARNING);
        }
    }

    /**
     * Checks if any of the beam dimension fields is empty.
     * If this is the case a message is added to the list of validation messages.
     *
     * @return List of validation messages
     */
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        List<String> validationMessages = new ArrayList<>();
        if (beamWidth.getText().equals("")) {
            validationMessages.add(UIText.SETUP_BEAM_WIDTH);
        }
        if (beamDepth.getText().equals("")) {
            validationMessages.add(UIText.SETUP_BEAM_DEPTH);
        }
        return validationMessages;
    }
}
