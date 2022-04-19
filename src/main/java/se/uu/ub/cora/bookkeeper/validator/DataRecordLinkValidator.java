/*
 * Copyright 2015, 2017, 2019 Uppsala University Library
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

import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

class DataRecordLinkValidator implements DataElementValidator {

	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private ValidationAnswer validationAnswer;
	private MetadataHolder metadataHolder;
	private DataGroup dataRecordLink;
	private RecordLink recordLink;
	private Map<String, DataGroup> recordTypeHolder;

	public DataRecordLinkValidator(Map<String, DataGroup> recordTypeHolder,
			MetadataHolder metadataHolder, RecordLink recordLink) {
		this.recordTypeHolder = recordTypeHolder;
		this.metadataHolder = metadataHolder;
		this.recordLink = recordLink;
	}

	@Override
	public ValidationAnswer validateData(DataChild dataElement) {
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
		MetadataMatchData metadataMatchData = MetadataMatchDataImp
				.withMetadataHolder(metadataHolder);
		ValidationAnswer va = metadataMatchData.metadataSpecifiesData(recordLink, dataRecordLink);
		addMessagesFromAnswerToTotalValidationAnswer(va);
	}

	private void addMessagesFromAnswerToTotalValidationAnswer(ValidationAnswer aValidationAnswer) {
		validationAnswer.addErrorMessages(aValidationAnswer.getErrorMessages());
	}

	private void validateRecordType() {
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordType as child.");
		} else if (incomingRecordTypeNotSameAsOrChildOfTypeSpecifiedInMetadata()) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an recordType:" + recordLink.getLinkedRecordType());
		}
	}

	private boolean recordTypeIsEmpty() {
		return !dataRecordLink.containsChildWithNameInData(LINKED_RECORD_TYPE)
				|| dataRecordLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE).isEmpty();
	}

	private boolean incomingRecordTypeNotSameAsOrChildOfTypeSpecifiedInMetadata() {
		String linkedRecordType = dataRecordLink
				.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		if (matchesRecordTypeInLink(linkedRecordType)) {
			return false;
		}

		return !recordTypeChildOfRecordTypeSpecifiedInMetadata(linkedRecordType);
	}

	private boolean matchesRecordTypeInLink(String parentId) {
		return parentId.equals(recordLink.getLinkedRecordType());
	}

	private boolean recordTypeChildOfRecordTypeSpecifiedInMetadata(String linkedRecordType) {
		if (recordTypeExistsAndHasParent(linkedRecordType)) {
			return parentMatchesRecordTypeSpecifiedInMetadata(linkedRecordType);
		}
		return false;
	}

	private boolean parentMatchesRecordTypeSpecifiedInMetadata(String linkedRecordType) {
		DataGroup recordType = recordTypeHolder.get(linkedRecordType);
		String parentId = extractParentId(recordType);

		if (matchesRecordTypeInLink(parentId)) {
			return true;
		}
		return recordTypeChildOfRecordTypeSpecifiedInMetadata(parentId);
	}

	private boolean recordTypeExistsAndHasParent(String linkedRecordType) {
		return recordTypeExist(linkedRecordType) && recordTypeHasParent(linkedRecordType);
	}

	private boolean recordTypeExist(String linkedRecordType) {
		return recordTypeHolder.containsKey(linkedRecordType);
	}

	private boolean recordTypeHasParent(String linkedRecordType) {
		DataGroup recordType = recordTypeHolder.get(linkedRecordType);
		return recordType.containsChildWithNameInData("parentId");
	}

	private String extractParentId(DataGroup recordType) {
		DataGroup parentGroup = (DataGroup) recordType.getFirstChildWithNameInData("parentId");
		return parentGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private void validateRecordId() {
		if (recordIdIsMissing()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
		} else {
			validateRecordIdValue();
		}
	}

	private void validateRecordIdValue() {
		if (finalValueIsDefinedInMetadata()) {
			validateDataValueIsFinalValue();
		} else {
			validateTextVariableValueByMetadataIdAndNameInData("linkedRecordIdTextVar",
					LINKED_RECORD_ID);
		}
	}

	private void validateTextVariableValueByMetadataIdAndNameInData(String metadataId,
			String nameInData) {
		DataTextVariableValidator dataValidator = createDataValidator(metadataId);
		DataChild linkedRecordIdData = dataRecordLink.getFirstChildWithNameInData(nameInData);
		validateTextVariableData(dataValidator, linkedRecordIdData);
	}

	private void validateTextVariableData(DataTextVariableValidator dataValidator,
			DataChild textVariableData) {
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
		} else {
			validateRepeatIdValue();
		}
	}

	private void validateRepeatIdValue() {
		validateTextVariableValueByMetadataIdAndNameInData("linkedRepeatIdTextVar",
				LINKED_REPEAT_ID);
	}

	private boolean linkedRepeatIdIsMissing() {
		return !dataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID);
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
