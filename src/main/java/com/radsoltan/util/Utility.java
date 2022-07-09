package com.radsoltan.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utility {

    /**
     * Utility method that takes a string and capitalizes the first letter.
     *
     * @param string String to be capitalized
     * @return capitalized string
     */
    public static String capitalize(String string) {
        if (string != null) {
            return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
        }
        return null;
    }

    /**
     * Method that finds all occurrences of an integer in a list of integers.
     *
     * @param list   list to look through
     * @param number number to search for
     * @return List of indexes
     */
    public static List<Integer> indexOfMultiple(List<Integer> list, int number) {
        return IntStream.range(0, list.size()).boxed().filter(i -> number == list.get(i)).collect(Collectors.toList());
    }
}
