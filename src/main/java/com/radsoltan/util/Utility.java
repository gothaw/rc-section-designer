package com.radsoltan.util;

import java.util.List;

public class Utility {
    public static String capitalize(String string) {
        if (string != null){
            return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
        }
        return null;
    }
}
