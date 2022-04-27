package com.radsoltan.util;

import com.radsoltan.model.Project;

import java.io.File;

/**
 * Helper class used to save project to a file and open it.
 */
public class ProjectFile {
    public static void save(File file, Project project) {
        System.out.println("Saving project");
    }

    public static void open(File file, Project project) {
        System.out.println("Opening project");
    }
}
