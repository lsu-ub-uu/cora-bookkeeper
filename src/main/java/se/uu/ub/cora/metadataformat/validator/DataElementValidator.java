package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;

public interface DataElementValidator {

	ValidationAnswer validateData(DataElement dataElement);

}
