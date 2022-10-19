/*
 * Copyright 2015, 2017, 2019, 2020 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

/**
 * ValidateData is a class to validate if a set of data is valid according to its metadataFormat
 */
class DataValidatorImp implements DataValidator {

	private MetadataStorageView metadataStorage;
	private DataElementValidatorFactory dataValidatorFactory;
	private Map<String, DataGroup> recordTypeHolder;

	DataValidatorImp(MetadataStorageView metadataStorage, DataElementValidatorFactory validatorFactory,
			Map<String, DataGroup> recordTypeHolder) {
		this.metadataStorage = metadataStorage;
		this.dataValidatorFactory = validatorFactory;
		this.recordTypeHolder = recordTypeHolder;
	}

	@Override
	public ValidationAnswer validateData(String metadataId, DataGroup dataGroup) {
		try {
			return tryToValidateData(metadataId, dataGroup);
		} catch (Exception exception) {
			return createValidationAnswerForError(metadataId, exception);
		}
	}

	private ValidationAnswer tryToValidateData(String metadataId, DataGroup dataGroup) {
		DataElementValidator elementValidator = dataValidatorFactory.factor(metadataId);
		return elementValidator.validateData(dataGroup);
	}

	private ValidationAnswer createValidationAnswerForError(String metadataId,
			Exception exception) {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage(
				"DataElementValidator not created for the requested metadataId: " + metadataId
						+ " with error:",
				exception);
		return validationAnswer;
	}

	@Override
	public ValidationAnswer validateListFilter(String recordType, DataGroup filterDataGroup) {
		String linkNameInData = "filter";
		return extractAndValidate(recordType, filterDataGroup, linkNameInData);
	}

	private ValidationAnswer extractAndValidate(String recordType, DataGroup dataGroupToValidate,
			String linkNameInData) {
		String metadataId = extractFilterIdOrThrowErrorIfMissing(recordType, linkNameInData);
		try {
			return tryToValidateData(metadataId, dataGroupToValidate);
		} catch (Exception exception) {
			return createValidationAnswerForError(metadataId, exception);
		}
	}

	private String extractFilterIdOrThrowErrorIfMissing(String recordType, String linkNameInData) {
		return extractLinkedDataGroupIdOrThrowErrorIfMissing(recordType, linkNameInData);
	}

	private String extractLinkedDataGroupIdOrThrowErrorIfMissing(String recordType,
			String nameInData) {
		DataGroup recordTypeGroup = recordTypeHolder.get(recordType);
		throwErrorIfRecordTypeHasNoDefinedLinkedDataGroup(recordType, recordTypeGroup, nameInData);
		return extractLinkedId(recordTypeGroup, nameInData);
	}

	private String extractLinkedId(DataGroup recordTypeGroup, String nameInData) {
		DataGroup filterGroup = recordTypeGroup.getFirstGroupWithNameInData(nameInData);
		return filterGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void throwErrorIfRecordTypeHasNoDefinedLinkedDataGroup(String recordType,
			DataGroup recordTypeDataGroup, String nameInData) {
		if (!recordTypeDataGroup.containsChildWithNameInData(nameInData)) {
			throw DataValidationException
					.withMessage("No " + nameInData + " exists for recordType: " + recordType);
		}
	}

	@Override
	public ValidationAnswer validateIndexSettings(String recordType, DataGroup indexSettings) {
		String linkNameInData = "indexSettings";
		return extractAndValidate(recordType, indexSettings, linkNameInData);
	}

	MetadataStorageView getMetadataStorage() {
		// needed for test
		return metadataStorage;
	}

	DataElementValidatorFactory getDataElementValidatorFactory() {
		// needed for test
		return dataValidatorFactory;
	}

	Map<String, DataGroup> getRecordTypeHolder() {
		// needed for test
		return recordTypeHolder;
	}
}
