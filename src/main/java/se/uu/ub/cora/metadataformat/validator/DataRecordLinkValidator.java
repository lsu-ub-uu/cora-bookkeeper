package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;

public class DataRecordLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private DataRecordLink data;

	public DataRecordLinkValidator() {
		// will take a DataToDataLink when implementing support for path
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		data = (DataRecordLink) dataElement;
		validateNoEmptyValues();
		return validationAnswer;
	}

	private void validateNoEmptyValues() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink with must have a nonempty nameInData");
		}
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink with nameInData:"
					+ data.getNameInData() + " must have an nonempty recordType as child.");
		}
		if (recordIdIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink with nameInData:"
					+ data.getNameInData() + " must have an nonempty recordId as child.");
		}
	}

	private boolean nameInDataIsEmpty() {
		return data.getNameInData().isEmpty();
	}

	private boolean recordTypeIsEmpty() {
		return data.getRecordType().isEmpty();
	}

	private boolean recordIdIsEmpty() {
		return data.getRecordId().isEmpty();
	}

}
