package com.radsoltan.model;

import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.model.reinforcement.SlabReinforcement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to create a list of post calculation validation messages that checks the results and the geometry after calculations have been carried out.
 * This includes:
 * Beam
 * - checking spacing against max shear link spacing (0.75d)
 * - checking main reinforcement area against max area allowed (4% of Ac)
 * - a warning if doubly reinforced section
 * Slab
 * - checking main reinforcement area against max area allowed (4% of Ac)
 */
public class PostCalculationValidate {

    private final List<String> validationMessages;

    /**
     * Constructor.
     * Sets post calculation validation messages for slab i.e. checking main reinforcement against max allowed.
     *
     * @param slab slab object
     */
    public PostCalculationValidate(Slab slab) {
        validationMessages = new ArrayList<>();

        SlabReinforcement slabReinforcement = slab.getReinforcement();
        double maxReinforcement = slab.getMaximumReinforcement();
        double areaOfTopReinforcement = slabReinforcement.getTotalAreaOfTopReinforcement();
        double areaOfBottomReinforcement = slabReinforcement.getTotalAreaOfBottomReinforcement();

        if (areaOfTopReinforcement > maxReinforcement) {
            validationMessages.add("Area of top reinforcement greater than maximum allowed!");
        }
        if (areaOfBottomReinforcement > maxReinforcement) {
            validationMessages.add("Area of bottom reinforcement greater than maximum allowed!");
        }
    }

    /**
     * Constructor.
     * Sets post calculation validation messages for beam:
     * - checking spacing against max shear link spacing (0.75d)
     * - checking main reinforcement area against max area allowed (4% of Ac)
     * - a warning if doubly reinforced section
     *
     * @param beam beam object
     */
    public PostCalculationValidate(Beam beam) {
        validationMessages = new ArrayList<>();

        BeamReinforcement beamReinforcement = beam.getReinforcement();
        double maxReinforcement = beam.getMaximumReinforcement();
        double areaOfTopReinforcement = beamReinforcement.getTotalAreaOfTopReinforcement();
        double areaOfBottomReinforcement = beamReinforcement.getTotalAreaOfBottomReinforcement();
        ShearLinks shearLinks = beamReinforcement.getShearLinks();
        double spacing = shearLinks.getSpacing();
        double maxSpacing = beam.getMaximumLinksSpacing();

        if (beam.getRequiredCompressionReinforcement() > 0) {
            validationMessages.add("Compression reinforcement needed. Doubly reinforced section!");
        }
        if (areaOfTopReinforcement > maxReinforcement) {
            validationMessages.add("Area of top reinforcement greater than maximum allowed!");
        }
        if (areaOfBottomReinforcement > maxReinforcement) {
            validationMessages.add("Area of bottom reinforcement greater than maximum allowed!");
        }
        if (spacing > maxSpacing) {
            validationMessages.add("Shear link spacing greater than maximum allowed (0.75d)!");
        }
    }

    /**
     * Getter for validation messages.
     *
     * @return List of validation messages
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
