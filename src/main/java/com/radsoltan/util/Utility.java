package com.radsoltan.util;

public class Utility {

    /**
     * Utility method that takes a string and capitalizes the first letter.
     * @param string String to be capitalized
     * @return String
     */
    public static String capitalize(String string) {
        if (string != null){
            return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
        }
        return null;
    }
}
