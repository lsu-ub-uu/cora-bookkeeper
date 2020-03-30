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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.MetadataStorage;

/**
 * ValidateData is a class to validate if a set of data is valid according to its metadataFormat
 */
public class DataValidatorImp implements DataValidator {

	private MetadataStorage metadataStorage;
	private String metadataId;
	private DataGroup dataGroup;
	private DataValidatorFactory dataValidatorFactory;

	public DataValidatorImp(MetadataStorage metadataStorage,
			DataValidatorFactory validatorFactory) {
		this.metadataStorage = metadataStorage;
		this.dataValidatorFactory = validatorFactory;
	}

	@Override
	public ValidationAnswer validateData(String metadataId, DataGroup dataGroup) {
		this.metadataId = metadataId;
		this.dataGroup = dataGroup;
		try {
			return tryToValidateData();
		} catch (Exception exception) {
			ValidationAnswer validationAnswer = new ValidationAnswer();
			validationAnswer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage(
					"DataElementValidator not created for the requested metadataId: " + metadataId
							+ " with error:",
					exception);
			return validationAnswer;
		}
	}

	private ValidationAnswer tryToValidateData() {
		return validateDataUsingDataValidator();
	}

	private ValidationAnswer validateDataUsingDataValidator() {
		DataElementValidator elementValidator = dataValidatorFactory.factor(metadataId);
		return elementValidator.validateData(dataGroup);
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
