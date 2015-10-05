package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;

public interface DataValidator {

	ValidationAnswer validateData(String metadataId, DataElement dataGroup);
}
