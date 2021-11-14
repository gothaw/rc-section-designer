package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.components.PositiveIntegerField;
import com.radsoltan.model.Project;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.HorizontalDimensionLine;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

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
        double dimensionLineOffset = 50;
        double dimensionLineExtension = 15;
        double dimensionLineScale = 5;

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);

        // Drawing Slab
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

        // TODO: 09/10/2021 Create a class for dimension line (vertical and horizontal) that would implement drawable
        // Horizontal Dimension Line
//
//        // Horizontal line
//        gc.beginPath();
//        gc.moveTo(slabLeftEdgeX - dimensionLineExtension, slabTopEdgeY - dimensionLineOffset);
//        gc.lineTo(slabRightEdgeX + dimensionLineExtension, slabTopEdgeY - dimensionLineOffset);
//
//        // Vertical End Line Left
//        gc.moveTo(slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset + 20);
//        gc.lineTo(slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset - 20);
//
//        // Vertical End Line Right
//        gc.moveTo(slabRightEdgeX, slabTopEdgeY - dimensionLineOffset + 20);
//        gc.lineTo(slabRightEdgeX, slabTopEdgeY - dimensionLineOffset - 20);
//        gc.stroke();
//        gc.closePath();
//
//        // Left Tick
//        gc.beginPath();
//        rotate(gc, 45, slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset);
//        gc.translate(-2, -18);
//        gc.rect(slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset, 4, 36);
//        gc.setFill(Color.BLACK);
//        gc.fill();
//        // Clean up
//        gc.translate(2, 18);
//        rotate(gc, 0, slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset);
//        gc.closePath();
//
//        // Right Tick
//        gc.beginPath();
//        rotate(gc, 45, slabRightEdgeX, slabTopEdgeY - dimensionLineOffset);
//        gc.translate(-2, -18);
//        gc.rect(slabRightEdgeX, slabTopEdgeY - dimensionLineOffset, 4, 36);
//        gc.setFill(Color.BLACK);
//        gc.fill();
//        // Clean up
//        gc.translate(2, 18);
//        rotate(gc, 0, slabRightEdgeX, slabTopEdgeY - dimensionLineOffset);
//        gc.closePath();
//
//        // Text
//        gc.beginPath();
//        Font font = new Font("Source Sans Pro", 26);
//        gc.setFont(font);
//        gc.setTextAlign(TextAlignment.CENTER);
//        gc.fillText("1000", 0.5 * (slabLeftEdgeX + slabRightEdgeX), slabTopEdgeY - dimensionLineOffset - 10);
//        gc.closePath();

        HorizontalDimensionLine horizontalDimensionLine = new HorizontalDimensionLine("1000", Color.BLACK, gc, slabLeftEdgeX, slabRightEdgeX, slabTopEdgeY, -dimensionLineOffset, 1.0);
        horizontalDimensionLine.draw();
    }

    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
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
