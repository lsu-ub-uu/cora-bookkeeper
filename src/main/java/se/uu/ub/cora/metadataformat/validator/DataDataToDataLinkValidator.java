package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataDataToDataLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private DataGroup dataToDataLink;

	public DataDataToDataLinkValidator(DataToDataLink dataLink) {
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		dataToDataLink = (DataGroup) dataElement;
		validateId();
		validateNoExtraChildren();
		return validationAnswer;
	}

	private void validateId() {
		if (childIdMissingOrEmpty()) {
			validationAnswer.addErrorMessage("DataToDataLink with nameInData:"
					+ dataToDataLink.getNameInData() + " must have an nonempty id as child.");
		}
	}

	private boolean childIdMissingOrEmpty() {
		return childIdIsMissing() || childIdIsEmpty();
	}

	private boolean childIdIsEmpty() {
		return dataToDataLink.getFirstAtomicValueWithNameInData("id").isEmpty();
	}

	private boolean childIdIsMissing() {
		return !dataToDataLink.containsChildWithNameInData("id");
	}

	private void validateNoExtraChildren() {
		if (linkHasOneChild()) {
			validationAnswer.addErrorMessage("DataToDataLink with nameInData:"
					+ dataToDataLink.getNameInData() + " can only have id as child.");
		}
	}

	private boolean linkHasOneChild() {
		return dataToDataLink.getChildren().size() != 1;
	}
}
