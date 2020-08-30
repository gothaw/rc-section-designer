package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import java.util.List;

/**
 * ValidateBeam class that implements Validation. It checks if beam geometry is valid.
 * It also checks if reinforcement spacings satisfy minimum rebar spacings required by Eurocode 2.
 */
public class ValidateBeam implements Validation {

    private List<String> validationMessages;

    public ValidateBeam(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        // TODO: 09/08/2020 Future functionality to validate beam reinforcement and geometry
    }

    /**
     * Getter for validation messages.
     * @return validationMessages
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
