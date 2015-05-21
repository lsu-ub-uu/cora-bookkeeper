package epc.metadataformat.validator;

import epc.metadataformat.data.DataElement;

public interface DataValidator {

	ValidationAnswer validateData(String metadataId, DataElement dataGroup);
}
