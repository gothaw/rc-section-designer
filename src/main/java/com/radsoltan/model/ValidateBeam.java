package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import java.util.List;

public class ValidateBeam implements Validation {

    private List<String> validationMessages;

    public ValidateBeam(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        // TODO: 09/08/2020 Future functionality to validate beam reinforcement and geometry
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
