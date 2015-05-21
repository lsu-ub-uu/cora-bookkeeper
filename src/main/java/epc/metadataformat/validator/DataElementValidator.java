package epc.metadataformat.validator;

import epc.metadataformat.data.DataElement;

public interface DataElementValidator {

	ValidationAnswer validateData(DataElement dataElement);

}
