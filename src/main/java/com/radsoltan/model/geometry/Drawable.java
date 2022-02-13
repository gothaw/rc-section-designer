package com.radsoltan.model.geometry;

/**
 * Interface for all drawable classes. It includes a draw method to be implemented.
 */
public interface Drawable {
    /**
     * Method to draw the entity.
     */
    void draw();

    /**
     * Method that checks is object has been correctly set up to be drawn.
     * @return true if can be drawn
     */
    boolean isSetupToBeDrawn();
}
