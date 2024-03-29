package com.radsoltan.model.geometry;

/**
 * Class used to represent L shaped section with asymmetrical flange at the top.
 * It inherits from T section.
 *
 * Note: This is WIP section not used currently in the app.
 */
public class LSection extends TSection {

    /**
     * Constructor. It uses T section constructor.
     *
     * @param webWidth           web width in mm
     * @param depth           total depth in mm
     * @param flangeWidth     flange width in mm
     * @param flangeThickness flange thickness in mm
     */
    public LSection(int webWidth, int depth, int flangeWidth, int flangeThickness) {
        super(webWidth, depth, flangeWidth, flangeThickness);
    }

    /**
     * Gets section description.
     *
     * @return section description
     */
    @Override
    public String getDescription() {
        return String.format("L section: %d mm downstand x %d mm web width + flange %d mm wide x %d mm thick.", getDownstandDepth(), getWebWidth(), getFlangeWidth(), getFlangeThickness());
    }

    /**
     * Draws section on canvas.
     */
    @Override
    public void draw() {}
}
