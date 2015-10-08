package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;

public class DataDataToDataLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private DataGroup data;

	public DataDataToDataLinkValidator() {
		// will take a DataToDataLink when implementing support for path
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		data = (DataGroup) dataElement;
		validateId();
		validateNoExtraChildren();
		return validationAnswer;
	}

	private void validateId() {
		if (childIdMissingOrEmpty()) {
			validationAnswer.addErrorMessage("DataToDataLink with nameInData:"
					+ data.getNameInData() + " must have an nonempty targetRecordId as child.");
		}
	}

	private boolean childIdMissingOrEmpty() {
		return childIdIsMissing() || childIdIsEmpty();
	}

	private boolean childIdIsEmpty() {
		return data.getFirstAtomicValueWithNameInData("targetRecordId").isEmpty();
	}

	private boolean childIdIsMissing() {
		return !data.containsChildWithNameInData("targetRecordId");
	}

	private void validateNoExtraChildren() {
		if (linkHasOneChild()) {
			validationAnswer.addErrorMessage("DataToDataLink with nameInData:"
					+ data.getNameInData() + " can only have targetRecordId as child.");
		}
	}

	private boolean linkHasOneChild() {
		return data.getChildren().size() != 1;
	}
}
