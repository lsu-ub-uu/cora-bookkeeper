/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.*;

public class DataRecordLinkValidator implements DataElementValidator {

	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private ValidationAnswer validationAnswer;
	private MetadataHolder metadataHolder;
	private DataGroup dataRecordLink;
	private RecordLink recordLink;

	public DataRecordLinkValidator(MetadataHolder metadataHolder, RecordLink recordLink) {
		this.metadataHolder = metadataHolder;
		this.recordLink = recordLink;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		dataRecordLink = (DataGroup) dataElement;
		validateNameInData();
		validateRecordType();
		validateRecordId();
		validateNoLinkedPath();
		validateLinkedRepeatId();
		return validationAnswer;
	}

	private void validateNameInData() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink must have a nonempty nameInData");
		}
	}

	private boolean nameInDataIsEmpty() {
		return dataRecordLink.getNameInData().isEmpty();
	}

	private void validateRecordType() {
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordType as child.");
		}
		else if (incomingRecordTypeNotSameAsOrChildOfTypeSpecifiedInMetadata()) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an recordType:" + recordLink.getLinkedRecordType());
		}
	}

	private boolean recordTypeIsEmpty() {
		return !dataRecordLink.containsChildWithNameInData(LINKED_RECORD_TYPE)
				|| dataRecordLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE).isEmpty();
	}

	private boolean incomingRecordTypeNotSameAsOrChildOfTypeSpecifiedInMetadata() {
		String linkedRecordType = dataRecordLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		if(linkedRecordType.equals(recordLink.getLinkedRecordType())){
			return false;
		}

		return !recordTypeChildOfRecordTypeSpecifiedInMetadata(linkedRecordType);
	}

	private boolean recordTypeChildOfRecordTypeSpecifiedInMetadata(String linkedRecordType) {
		MetadataElement metadataElementRecordType = metadataHolder.getMetadataElement(linkedRecordType);
		if(metadataElementRecordType != null){
			MetadataGroup recordType = (MetadataGroup)metadataElementRecordType;
			if(linkedRecordTypeIsChildOfRecordTypeInMetadata(recordType)){
				return true;
			}
		}
		return false;
	}

	private boolean linkedRecordTypeIsChildOfRecordTypeInMetadata(MetadataGroup recordType) {
		return recordType.getRefParentId() != null
				&& recordType.getRefParentId().equals(recordLink.getLinkedRecordType());
	}

	private void validateRecordId() {
		if (recordIdIsMissing()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
		}else {
			validateRecordIdValue();
		}
	}

	private void validateRecordIdValue() {
		if (finalValueIsDefinedInMetadata()) {
			validateDataValueIsFinalValue();
		}else {
			validateTextVariableValueByMetadataIdAndNameInData("linkedRecordIdTextVar", LINKED_RECORD_ID);
		}
	}

	private void validateTextVariableValueByMetadataIdAndNameInData(String metadataId, String nameInData) {
		DataTextVariableValidator dataValidator = createDataValidator(metadataId);
		DataElement linkedRecordIdData = dataRecordLink.getFirstChildWithNameInData(nameInData);
		validateTextVariableData(dataValidator, linkedRecordIdData);
	}

	private void validateTextVariableData(DataTextVariableValidator dataValidator, DataElement textVariableData) {
		ValidationAnswer va = dataValidator.validateData(textVariableData);

		if (va.dataIsInvalid()) {
            validationAnswer.addErrorMessages(va.getErrorMessages());
        }
	}

	private DataTextVariableValidator createDataValidator(String metadataId) {
		MetadataElement metadataElement = metadataHolder.getMetadataElement(metadataId);
		return new DataTextVariableValidator((TextVariable) metadataElement);
	}

	private boolean recordIdIsMissing() {
		return !dataRecordLink.containsChildWithNameInData(LINKED_RECORD_ID);
	}

	private boolean finalValueIsDefinedInMetadata() {
		return null != recordLink.getFinalValue();
	}

	private void validateDataValueIsFinalValue() {
		String dataValue = dataRecordLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		if (!dataValueIsFinalValue(dataValue)) {
			createErrorMessageForFinalValue(dataValue);
		}
	}

	private boolean dataValueIsFinalValue(String dataValue) {
		return recordLink.getFinalValue().equals(dataValue);
	}

	private void createErrorMessageForFinalValue(String dataValue) {
		validationAnswer.addErrorMessage(
				"Value:" + dataValue + " is not finalValue:" + recordLink.getFinalValue());
	}

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + dataRecordLink.getNameInData();
	}

	private void validateNoLinkedPath() {
		if (thereIsALinkedPath()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should not have a linkedPath");
		}
	}

	private boolean thereIsALinkedPath() {
		return dataRecordLink.containsChildWithNameInData("linkedPath");
	}

	private void validateLinkedRepeatId() {
		if (dataShouldContainALinkedRepeatId()) {
			validateHasLinkedRepeatId();
		} else {
			validateDoesNotHaveLinkedRepeatId();
		}
	}

	private boolean dataShouldContainALinkedRepeatId() {
		return recordLink.getLinkedPath() != null;
	}

	private void validateHasLinkedRepeatId() {
		if (linkedRepeatIdIsMissing()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should have a linkedRepeatId");
		}else{
			validateRepeatIdValue();
		}
	}

	private void validateRepeatIdValue() {
		validateTextVariableValueByMetadataIdAndNameInData("linkedRepeatIdTextVar", "linkedRepeatId");
	}

	private boolean linkedRepeatIdIsMissing() {
		return (!dataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID));
	}

	private void validateDoesNotHaveLinkedRepeatId() {
		if (linkedRepeatIdIsNotNull()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should not have a linkedRepeatId");
		}
	}

	private boolean linkedRepeatIdIsNotNull() {
		return dataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID);
	}

}
