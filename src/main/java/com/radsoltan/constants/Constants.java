package com.radsoltan.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that includes constants used in the project.
 */
public class Constants {
    public static final String ELEMENT_TYPE_BEAM = "beam";
    public static final String ELEMENT_TYPE_SLAB = "slab";
    public static final String BEAM_TOP_FACE = "beam top face";
    public static final String BEAM_BOTTOM_FACE = "beam bottom face";
    public static final String SLAB_TOP_FACE = "slab top face";
    public static final String SLAB_BOTTOM_FACE = "slab bottom face";
    public static final String PROJECT_FILE = "Project file";
    public static final String PROJECT_FILE_EXTENSION = "*.rcd";
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    public static final int MIN_WINDOW_WIDTH = 900;
    public static final int MAX_WINDOW_WIDTH = 1050;
    public static final int MIN_WINDOW_HEIGHT = 300;
    public static final int MAX_WINDOW_HEIGHT = 820;
    public static final int DEFAULT_ALERT_WIDTH = 250;
    public static final int DEFAULT_ALERT_HEIGHT = 80;
    public static final int LARGE_ALERT_WIDTH = 320;
    public static final int LARGE_ALERT_HEIGHT = 105;
    public static final ArrayList<Integer> BAR_DIAMETERS = new ArrayList<>(List.of(6, 8, 10, 12, 16, 20, 25, 32, 40));
    public static final ArrayList<Integer> SHEAR_LEGS = new ArrayList<>(List.of(2, 3, 4, 5, 6, 7, 8, 9, 10));
    public static final int SLAB_MIN_BAR_SPACING = 50;
    public static final int SLAB_MAX_BAR_SPACING = 750;
    public static final int SLAB_BAR_SPACING_STEP = 25;
    public static final int BEAM_ROW_MAIN_BAR_MIN_COUNT = 2;
    public static final int BEAM_ROW_BAR_MAX_COUNT = 99;
    public static final int MAX_NOMINAL_COVER = 100;
    public static final int MIN_NOMINAL_COVER = 20;
    public static final double GAMMA_C_PERSISTENT_TRANSIENT = 1.5;
    public static final double GAMMA_C_ACCIDENTAL = 1.2;
    public static final double GAMMA_S_PERSISTENT_TRANSIENT = 1.15;
    public static final double GAMMA_S_ACCIDENTAL = 1.0;
    public static final int DEFAULT_AGGREGATE_SIZE = 20;
    public static final double DEFAULT_REDISTRIBUTION_RATIO = 0.85;
    public static final int NOMINAL_COVER_STEP = 5;
    public static final int MAX_NUMBER_OF_LAYERS = 6;
    public static final int MAX_NUMBER_OF_ROWS = 5;
    public static final ArrayList<String> ORDINAL_LABELS = new ArrayList<>(List.of("1st", "2nd", "3rd", "4th", "5th", "6th"));
}
