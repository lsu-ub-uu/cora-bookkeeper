/*
 * Copyright 2015, 2019 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;

class DataCollectionVariableValidator implements DataElementValidator {

	protected MetadataHolder metadataHolder;
	protected CollectionVariable collectionVariable;
	private String dataValue;

	public DataCollectionVariableValidator(MetadataHolder metadataHolder,
			CollectionVariable collectionVariable) {
		this.metadataHolder = metadataHolder;
		this.collectionVariable = collectionVariable;
	}

	@Override
	public ValidationAnswer validateData(DataChild dataElement) {
		DataAtomic data = (DataAtomic) dataElement;
		dataValue = data.getValue();
		if (finalValueIsDefinedInMetadata()) {
			return validateDataValueIsFinalValue();
		}
		return validateDataValueExistsInReferredCollection();
	}

	private boolean finalValueIsDefinedInMetadata() {
		return null != collectionVariable.getFinalValue();
	}

	private ValidationAnswer validateDataValueIsFinalValue() {
		if (dataValueIsFinalValue()) {
			return new ValidationAnswer();
		}
		return createErrorMessageForFinalValue();
	}

	private boolean dataValueIsFinalValue() {
		return collectionVariable.getFinalValue().equals(dataValue);
	}

	private ValidationAnswer createErrorMessageForFinalValue() {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessage(
				"Value:" + dataValue + " is not finalValue:" + collectionVariable.getFinalValue());
		return validationAnswer;
	}

	private ValidationAnswer validateDataValueExistsInReferredCollection() {
		ItemCollection referredCollection = (ItemCollection) metadataHolder
				.getMetadataElement(collectionVariable.getRefCollectionId());
		if (dataValueFoundInReferredCollection(referredCollection)) {
			return new ValidationAnswer();
		}
		return createErrorMessageForReferredCollection(referredCollection);
	}

	private boolean dataValueFoundInReferredCollection(ItemCollection referredCollection) {
		for (String ref : referredCollection.getCollectionItemReferences()) {
			if (collectionItemMatchesDataValue(ref)) {
				return true;
			}
		}
		return false;
	}

	private boolean collectionItemMatchesDataValue(String ref) {
		CollectionItem colItem = (CollectionItem) metadataHolder.getMetadataElement(ref);
		return colItem.getNameInData().equals(dataValue);
	}

	private ValidationAnswer createErrorMessageForReferredCollection(
			ItemCollection referredCollection) {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessage("Data value:" + dataValue + " NOT found in collection:"
				+ referredCollection.getNameInData());
		return validationAnswer;
	}

	MetadataElement onlyForTestGetMetadataElement() {
		return collectionVariable;
	}

	public MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

}
