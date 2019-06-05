/*
 * Copyright 2015, 2017 Uppsala University Library
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderFromStoragePopulator;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

/**
 * ValidateData is a class to validate if a set of data is valid according to its metadataFormat
 * 
 * @author olov
 * 
 */
public class DataValidatorImp implements DataValidator {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;
	private String metadataId;
	private DataElement dataElement;
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();

	public DataValidatorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public ValidationAnswer validateData(String metadataId, DataElement dataElement) {
		this.metadataId = metadataId;
		this.dataElement = dataElement;
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
		getRecordTypesFromStorage();
		getMetadataFromStorage();
		return validateDataUsingDataValidator();
	}

	private void getRecordTypesFromStorage() {
		Collection<DataGroup> recordTypes = metadataStorage.getRecordTypes();
		for (DataGroup dataGroup : recordTypes) {
			DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
			String recordId = recordInfo.getFirstAtomicValueWithNameInData("id");
			recordTypeHolder.put(recordId, dataGroup);
		}
	}

	private void getMetadataFromStorage() {
		metadataHolder = new MetadataHolderFromStoragePopulator()
				.createAndPopulateMetadataHolderFromMetadataStorage(metadataStorage);
	}

	private ValidationAnswer validateDataUsingDataValidator() {
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator elementValidator = dataValidatorFactory.factor(metadataId);
		return elementValidator.validateData(dataElement);
	}

	public MetadataStorage getMetadataStorage() {
		// needed for test
		return metadataStorage;
	}
}
