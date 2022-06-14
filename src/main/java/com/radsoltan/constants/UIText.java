package com.radsoltan.constants;

/**
 * Utility class that includes String constants for various alerts messages, warnings and errors.
 * These are shown in the UI.
 */
public class UIText {
    public static final String APP_TITLE = "RC Section Designer";
    public static final String APP_AUTHOR = "Radoslaw Soltan";
    public static final String ALERT_NEW_ITEM = "Are you sure you want to create a new project?";
    public static final String ERROR = "Error!";
    public static final String INFO = "Info";
    public static final String WARNING = "Warning!";
    public static final String OK = "OK";
    public static final String CANCEL = "Cancel";
    public static final String CALCULATIONS_ERROR = "Calculations error";
    public static final String CRACKING = "Cracking";
    public static final String CRACKING_FAIL_MESSAGE = "Increase reinforcement or redesign section.";
    public static final String ENTER_GEOMETRY = "Enter geometry...";
    public static final String ENTER_REINFORCEMENT = "Enter reinforcement...";
    public static final String FAIL = "Fail";
    public static final String FLEXURE_FAIL_MESSAGE = "Increase reinforcement or redesign section.";
    public static final String FLEXURE = "Flexure";
    public static final String INVALID_ELEMENT_TYPE = "Invalid element type.";
    public static final String INVALID_REDISTRIBUTION_RATIO = "Redistribution ratio must be between 0.7 and 1.0.";
    public static final String INVALID_SLAB_REINFORCEMENT = "Invalid slab reinforcement.";
    public static final String INVALID_BEAM_REINFORCEMENT = "Invalid beam reinforcement.";
    public static final String INVALID_SLAB_GEOMETRY = "Invalid slab geometry.";
    public static final String INVALID_BEAM_GEOMETRY = "Invalid beam geometry.";
    public static final String INVALID_DIMENSION_LINE = "Invalid dimension line.";
    public static final String INVALID_MAX_CRACK_WIDTH = "Max crack width must be between 0.05 and 0.5.";
    public static final String INVALID_BAR_SPACING_CRACKS = "Bar spacing must not exceed 5(c + 0.5d) for cracking calculations.";
    public static final String INVALID_BENDING_CAPACITY = "Flexure calculations required to run cracking check.";
    public static final String PASS = "Pass";
    public static final String REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE = "Compressive force greater than the capacity. Redesign section.";
    public static final String REDESIGN_SECTION_DUE_TO_HIGH_SHEAR = "Shear force greater than compressive strut capacity. Redesign section.";
    public static final String SHEAR = "Shear";
    public static final String SECTION_ADEQUATE = "Section adequate.";
    public static final String SETUP_ELEMENT_TYPE = "Please select element type.";
    public static final String SETUP_NOMINAL_COVER = "Please set up nominal cover.";
    public static final String SETUP_CONCRETE_CLASS = "Please set up concrete class.";
    public static final String SETUP_AGGREGATE = "Please set up aggregate size.";
    public static final String SETUP_ANALYSIS_FORCES = "Please set up analysis forces.";
    public static final String SETUP_GEOMETRY = "Please set up geometry.";
    public static final String SETUP_REINFORCEMENT = "Please set up reinforcement.";
    public static final String SETUP_DESIGN_PARAMETERS = "Please set up design parameters.";
    public static final String SETUP_SLAB_THICKNESS = "Please set up slab thickness.";
    public static final String SETUP_BEAM_WIDTH = "Please set up beam width.";
    public static final String SETUP_BEAM_DEPTH = "Please set up beam depth.";
    public static final String SETUP_YIELD_STRENGTH = "Please set up steel yield strength.";
    public static final String SETUP_PARTIAL_FACTORS = "Please set up partial factors for steel and concrete.";
    public static final String SOMETHING_WENT_WRONG = "Sorry. Something went wrong.";
    public static final String UNIT_MOMENT_BEAM = "kNm";
    public static final String UNIT_MOMENT_SLAB = "kNm/m";
    public static final String UNIT_SHEAR = "kN";
    public static final String WRONG_CONCRETE_CLASS = "Concrete class greater than C50/60. Outside of scope of this software.";
}
