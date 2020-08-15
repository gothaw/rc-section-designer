package com.radsoltan.model;

import java.util.List;

/**
 * Interface for validation classes. To be implemented by classes that validate Slab or Beam elements.
 */
public interface Validation {

    /**
     * Method to return validation Messages for the element.
     * @return List
     */
    List<String> getValidationMessages();
}
