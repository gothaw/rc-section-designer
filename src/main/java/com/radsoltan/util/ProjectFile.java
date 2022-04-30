package com.radsoltan.util;

import com.radsoltan.model.Project;

import java.io.*;

/**
 * Helper class used to save project to a file and read a project instance from a file.
 */
public class ProjectFile {

    /**
     * Saves project to a file. It uses FileOutputStream that is used to instantiate ObjectOutputStream.
     *
     * @param file file object to save to
     * @param project instance of the project
     * @throws IOException Exception for failed or interrupted I/O operation
     */
    public static void save(File file, Project project) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(project);
    }

    /**
     * Reads project from a file. It uses FileInputStream that is used to instantiate ObjectInputStream.
     *
     * @param file file to read project object from
     * @return instance of the Project read from file
     * @throws IOException Exception for failed or interrupted I/O operation
     * @throws ClassNotFoundException Exception for occurs when an application tries to load a class through its fully-qualified name and can not find its definition on the classpath
     * @throws ClassCastException Exception raised when improperly casting class from one type to another
     */
    public static Project open(File file) throws IOException, ClassNotFoundException, ClassCastException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        return (Project) object;
    }
}
