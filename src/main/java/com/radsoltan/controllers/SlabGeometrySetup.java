package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.*;
import com.radsoltan.util.Messages;

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
 * Controller for setting up geometry view.
 * This mostly includes setting up slab thickness.
 */
public class SlabGeometrySetup extends Controller {

    @FXML
    public Canvas slabImage;
    public StackPane slabImageWrapper;
    @FXML
    private VBox container;
    @FXML
    public PositiveIntegerField slabThickness;

    private final Project project;
    private final Geometry geometry;

    private static final double SLAB_IMAGE_HORIZONTAL_RATIO = 0.6;
    private static final double SLAB_IMAGE_VERTICAL_RATIO = 0.3;
    private static final double SLAB_IMAGE_DIMENSION_LINES_SCALE = 0.75;

    /**
     * Constructor. It gets project instance and using the instance it gets the geometry.
     */
    public SlabGeometrySetup() {
        this.project = Project.getInstance();
        this.geometry = project.getGeometry();
    }

    /**
     * Initialize method that is run after calling the constructor.
     * If slab geometry was instantiated before, it takes the slab thickness and set them in the input field.
     * It also invokes draw method that draws a typical slab cross section.
     */
    @FXML
    public void initialize() {
        if (geometry != null) {
            Section section = geometry.getSection();
            if (section instanceof SlabStrip) {
                SlabStrip slabStrip = (SlabStrip) section;
                slabThickness.setText(Integer.toString(slabStrip.getDepth()));
            } else {
                showAlertBox(Messages.INVALID_SLAB_GEOMETRY, AlertKind.ERROR);
            }
        }
        this.draw();
        Platform.runLater(() -> container.requestFocus());
    }

    /**
     * Method that handles ok button click. It requires slab thickness to be set (not empty) before redirecting to the main controller.
     * If non empty, it creates SlabStrip object with given thickness.
     * It then creates Geometry object and saves it in the project instance.
     *
     * @param actionEvent Ok button click event
     * @throws IOException Exception for failed or interrupted I/O operation.
     */
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        List<String> validationMessages = getValidationMessagesForEmptyFields();
        if (validationMessages.isEmpty()) {
            SlabStrip slabStrip = new SlabStrip(Integer.parseInt(slabThickness.getText()));
            Geometry geometry = new Geometry(slabStrip);

            project.setGeometry(geometry);

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
     * Draws slab image along with dimension lines.
     * Slab image is drawn relatively to the canvas size using the ratios defined in constants.
     */
    private void draw() {
        GraphicsContext graphicsContext = slabImage.getGraphicsContext2D();
        double canvasWidth = slabImage.getWidth();
        double canvasHeight = slabImage.getHeight();
        int slabWidth = (int) (SLAB_IMAGE_HORIZONTAL_RATIO * canvasWidth);
        int slabThickness = (int) (SLAB_IMAGE_VERTICAL_RATIO * canvasHeight);
        double slabLeftEdgeX = 0.5 * canvasWidth - 0.5 * slabWidth;
        double slabTopEdgeY = 0.5 * canvasHeight - 0.5 * slabThickness;
        double slabRightEdgeX = slabLeftEdgeX + slabWidth;
        double slabBottomEdgeY = slabTopEdgeY + slabThickness;

        SlabStrip slabStrip = new SlabStrip(
                slabWidth,
                slabThickness,
                graphicsContext,
                Color.BLACK,
                Color.LIGHTGRAY,
                slabLeftEdgeX,
                slabTopEdgeY
        );

        slabStrip.draw();

        // Draw Horizontal Dimension Line
        HorizontalDimensionLine horizontalDimensionLine = new HorizontalDimensionLine(
                "1000",
                Color.BLACK,
                graphicsContext,
                slabLeftEdgeX,
                slabRightEdgeX,
                slabTopEdgeY,
                -DimensionLine.DEFAULT_OFFSET,
                SLAB_IMAGE_DIMENSION_LINES_SCALE
        );
        horizontalDimensionLine.draw();

        // Draw Vertical Dimension Line
        VerticalDimensionLine verticalDimensionLine = new VerticalDimensionLine(
                "t",
                Color.BLACK,
                graphicsContext,
                slabTopEdgeY,
                slabBottomEdgeY,
                slabLeftEdgeX,
                -DimensionLine.DEFAULT_OFFSET,
                SLAB_IMAGE_DIMENSION_LINES_SCALE,
                false
        );
        verticalDimensionLine.draw();
    }

    /**
     * Checks if slab thickness field is empty.
     * If this is the case a message is added to the list of validation messages.
     *
     * @return List of validation messages
     */
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        if (slabThickness.getText().equals("")) {
            return new ArrayList<>(List.of(Messages.SETUP_SLAB_THICKNESS));
        }
        return new ArrayList<>();
    }
}
