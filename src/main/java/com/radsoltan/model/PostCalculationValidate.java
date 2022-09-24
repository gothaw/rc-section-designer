package com.radsoltan.model;

import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;

import java.util.ArrayList;
import java.util.List;

public class PostCalculationValidate {

    private final List<String> validationMessages;

    /**
     *
     * @param slabReinforcement
     */
    public PostCalculationValidate(SlabReinforcement slabReinforcement) {
        validationMessages = new ArrayList<>();

        validationMessages.add("Slab Warning 1");
        validationMessages.add("Slab Warning 2");
    }

    /**
     *
     * @param beamReinforcement
     * @param isDoublyReinforced
     */
    public PostCalculationValidate(BeamReinforcement beamReinforcement, boolean isDoublyReinforced) {
        validationMessages = new ArrayList<>();

        validationMessages.add("Beam Warning 1");
        validationMessages.add("Beam Warning 2");
    }

    /**
     *
     * @return
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
