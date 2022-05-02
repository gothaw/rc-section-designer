package com.radsoltan.util;

import javafx.event.Event;
import javafx.event.EventType;

import java.io.File;

/**
 * Custom event used to pass file object.
 */
public class FileEvent extends Event {
    private final File file;

    /**
     * Constructor.
     *
     * @param file      file to pass as a parameter to other views
     * @param eventType type of the event
     */
    public FileEvent(File file, EventType<? extends Event> eventType) {
        super(eventType);
        this.file = file;
    }

    /**
     * Getter for the file field.
     *
     * @return file object
     */
    public File getFile() {
        return file;
    }
}
