/*
 * Copyright 2026 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.AnyTypeRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

class DataAnyTypeRecordLinkValidator implements DataElementValidator {

	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private ValidationAnswer validationAnswer;
	private MetadataHolder metadataHolder;
	private DataGroup dataRecordLink;
	private AnyTypeRecordLink recordLink;
	private Map<String, DataGroup> recordTypeHolder;

	public DataAnyTypeRecordLinkValidator(Map<String, DataGroup> recordTypeHolder,
			MetadataHolder metadataHolder, AnyTypeRecordLink recordLink) {
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
		}
	}

	private boolean recordTypeIsEmpty() {
		return !dataRecordLink.containsChildWithNameInData(LINKED_RECORD_TYPE)
				|| dataRecordLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE).isEmpty();
	}

	private void validateRecordId() {
		if (recordIdIsMissing()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
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

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + dataRecordLink.getNameInData();
	}

	MetadataElement onlyForTestGetMetadataElement() {
		return recordLink;
	}

	public Map<String, DataGroup> onlyForTestGetRecordTypeHolder() {
		return recordTypeHolder;
	}

	public MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}
}
