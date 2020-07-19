package com.radsoltan.util;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String APP_TITLE = "RC Section Designer";
    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 670;
    public static final int MIN_WINDOW_WIDTH = 680;
    public static final int MAX_WINDOW_WIDTH = 750;
    public static final int MIN_WINDOW_HEIGHT = 300;
    public static final int MAX_WINDOW_HEIGHT = 710;
    public static final int DEFAULT_ALERT_WIDTH = 250;
    public static final int DEFAULT_ALERT_HEIGHT = 80;
    public static final int LARGE_ALERT_WIDTH = 320;
    public static final int LARGE_ALERT_HEIGHT = 105;
    public static final ArrayList<Integer> BAR_DIAMETERS = new ArrayList<>(List.of(6, 8, 10, 12, 16, 20, 25, 32, 40));
    public static final int SLAB_MIN_BAR_SPACING = 50;
    public static final int SLAB_MAX_BAR_SPACING = 750;
    public static final int SLAB_BAR_SPACING_STEP = 25;
    public static final int MAX_NUMBER_OF_LAYERS = 6;
    public static final ArrayList<String> LAYERS_ORDINAL_LABELS = new ArrayList<>(List.of("1st", "2nd", "3rd", "4th", "5th", "6th"));
}
