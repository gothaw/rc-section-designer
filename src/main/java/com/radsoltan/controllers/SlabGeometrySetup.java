package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Shape;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.util.Messages;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

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
            Shape shape = geometry.getShape();
            if (shape instanceof SlabStrip) {
                SlabStrip slabStrip = (SlabStrip) shape;
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
     * // TODO: 12/09/2021 Update documentation
     */
    private void draw() {
        GraphicsContext gc = slabImage.getGraphicsContext2D();
        double canvasWidth = slabImage.getWidth();
        double canvasHeight = slabImage.getHeight();
        double slabEndArchDepth = 15;
        double slabWidth = 0.6 * canvasWidth;
        double slabDepth = 0.3 * canvasHeight;
        double slabLeftEdgeX = 0.5 * canvasWidth - 0.5 * slabWidth;
        double slabTopEdgeY = 0.5 * canvasHeight - 0.5 * slabDepth;
        double slabRightEdgeX = slabLeftEdgeX + slabWidth;
        double slabBottomEdgeY = slabTopEdgeY + slabDepth;

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);

        gc.beginPath();
        // Top Edge
        gc.moveTo(slabLeftEdgeX, slabTopEdgeY);
        gc.lineTo(slabRightEdgeX, slabTopEdgeY);
        // Right Edge
        gc.quadraticCurveTo(slabRightEdgeX - slabEndArchDepth, slabTopEdgeY + 0.25 * slabDepth, slabRightEdgeX, slabTopEdgeY + 0.5 * slabDepth);
        gc.quadraticCurveTo(slabRightEdgeX + slabEndArchDepth, slabTopEdgeY + 0.75 * slabDepth, slabRightEdgeX, slabBottomEdgeY);
        // Bottom Edge
        gc.lineTo(slabLeftEdgeX, slabBottomEdgeY);
        // Left Edge
        gc.quadraticCurveTo(slabLeftEdgeX + slabEndArchDepth, slabBottomEdgeY - 0.25 * slabDepth, slabLeftEdgeX, slabBottomEdgeY - 0.5 * slabDepth);
        gc.quadraticCurveTo(slabLeftEdgeX - slabEndArchDepth, slabBottomEdgeY - 0.75 * slabDepth, slabLeftEdgeX, slabTopEdgeY);

        gc.stroke();
        gc.fill();
        gc.closePath();
    }

    /**
     * Checks if slab thickness field is empty.
     * If this is the case a message is added to the list of validation messages.
     *
     * @return list of validation messages
     */
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        if (slabThickness.getText().equals("")) {
            return new ArrayList<>(List.of(Messages.SETUP_SLAB_THICKNESS));
        }
        return new ArrayList<>();
    }
}
