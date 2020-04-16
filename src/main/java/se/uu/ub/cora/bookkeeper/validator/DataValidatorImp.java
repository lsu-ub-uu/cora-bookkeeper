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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.MetadataStorage;

/**
 * ValidateData is a class to validate if a set of data is valid according to its metadataFormat
 */
public class DataValidatorImp implements DataValidator {

	private MetadataStorage metadataStorage;
	private DataValidatorFactory dataValidatorFactory;
	private Map<String, DataGroup> recordTypeHolder;

	public DataValidatorImp(MetadataStorage metadataStorage, DataValidatorFactory validatorFactory,
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
			return createValidationAnswer(metadataId, exception);
		}
	}

	private ValidationAnswer tryToValidateData(String metadataId, DataGroup dataGroup) {
		DataElementValidator elementValidator = dataValidatorFactory.factor(metadataId);
		return elementValidator.validateData(dataGroup);
	}

	private ValidationAnswer createValidationAnswer(String metadataId, Exception exception) {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage(
				"DataElementValidator not created for the requested metadataId: " + metadataId
						+ " with error:",
				exception);
		return validationAnswer;
	}

	@Override
	public ValidationAnswer validateListFilter(String recordType, DataGroup filterDataGroup) {
		String filterId = extractFilterIdOrThrowErrorIfMissing(recordType);
		try {
			return tryToValidateData(filterId, filterDataGroup);
		} catch (Exception exception) {
			return createValidationAnswer(filterId, exception);
		}
	}

	private String extractFilterIdOrThrowErrorIfMissing(String recordType) {
		DataGroup recordTypeGroup = recordTypeHolder.get(recordType);
		throwErrorIfRecordTypeHasNoDefinedFilter(recordType, recordTypeGroup);
		return extractFilterId(recordTypeGroup);
	}

	private String extractFilterId(DataGroup recordTypeGroup) {
		DataGroup filterGroup = recordTypeGroup.getFirstGroupWithNameInData("filter");
		return filterGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void throwErrorIfRecordTypeHasNoDefinedFilter(String recordType,
			DataGroup recordTypeDataGroup) {
		if (!recordTypeDataGroup.containsChildWithNameInData("filter")) {
			throw DataValidationException
					.withMessage("No filter exists for recordType: " + recordType);
		}
	}

	public MetadataStorage getMetadataStorage() {
		// needed for test
		return metadataStorage;
	}

	public DataValidatorFactory getDataValidatorFactory() {
		// needed for test
		return dataValidatorFactory;
	}

}
