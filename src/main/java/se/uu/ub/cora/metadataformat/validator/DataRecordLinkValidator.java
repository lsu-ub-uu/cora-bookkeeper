package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataRecordLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private DataRecordLink data;
	private DataToDataLink dataLink;

	public DataRecordLinkValidator(DataToDataLink dataLink) {
		this.dataLink = dataLink;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		data = (DataRecordLink) dataElement;
		validateNoEmptyValues();
		validateTargetRecordType();
		return validationAnswer;
	}

	private void validateNoEmptyValues() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink must have a nonempty nameInData");
		}
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordType as child.");
		}
		if (recordIdIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
		}
	}

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + data.getNameInData();
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

	private void validateTargetRecordType() {
		if (incomingRecordTypeNotSameAsSpecifiedInMetadata()) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an recordType:" + dataLink.getTargetRecordType());
		}
	}

	private boolean incomingRecordTypeNotSameAsSpecifiedInMetadata() {
		return !data.getRecordType().equals(dataLink.getTargetRecordType());
	}

}
