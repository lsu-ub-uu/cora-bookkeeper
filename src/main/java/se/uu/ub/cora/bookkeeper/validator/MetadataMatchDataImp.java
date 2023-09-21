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

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataResourceLink;

public final class MetadataMatchDataImp implements MetadataMatchData {

	private MetadataHolder metadataHolder;
	private MetadataElement metadataElement;
	private DataChild dataChild;
	private ValidationAnswer validationAnswer;

	public static MetadataMatchData withMetadataHolder(MetadataHolder metadataHolder) {
		return new MetadataMatchDataImp(metadataHolder);
	}

	private MetadataMatchDataImp(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	@Override
	public ValidationAnswer metadataSpecifiesData(MetadataElement metadataElement,
			DataChild dataChild) {
		this.metadataElement = metadataElement;
		this.dataChild = dataChild;
		validationAnswer = new ValidationAnswer();
		validateNameInData();
		if (!(dataChild instanceof DataResourceLink)) {
			validateAttributes();
		}
		return validationAnswer;
	}

	private void validateNameInData() {
		String metadataNameInData = metadataElement.getNameInData();
		String dataNameInData = dataChild.getNameInData();
		if (!metadataNameInData.equals(dataNameInData)) {
			validationAnswer.addErrorMessage("DataGroup should have name(nameInData): "
					+ metadataElement.getNameInData() + " it does not.");
		}
	}

	private void validateAttributes() {
		validateDataContainsAllRequiredAttributesWithCorrectValues();
		validateDataContainsNoUnspecifiedAttributes();
	}

	private void validateDataContainsAllRequiredAttributesWithCorrectValues() {
		Collection<String> mdAttributeReferences = metadataElement.getAttributeReferences();
		for (String mdAttributeReference : mdAttributeReferences) {
			validateDataContainsAttributeReferenceWithCorrectData(mdAttributeReference);
		}
	}

	private void validateDataContainsAttributeReferenceWithCorrectData(
			String mdAttributeReference) {
		String nameInData = getNameInDataForAttributeReference(mdAttributeReference);

		try {
			DataAttribute foundDataAttribute = getDataAttributeUsingAttributeName(nameInData);
			DataAtomic dataAtomicElement = createDataAtomicFromAttribute(mdAttributeReference,
					foundDataAttribute);
			validateAttribute(mdAttributeReference, dataAtomicElement);
		} catch (DataValidationException e) {
			validationAnswer.addErrorMessage(
					"Attribute with nameInData: " + nameInData + " does not exist in data.");
		}
	}

	private DataAttribute getDataAttributeUsingAttributeName(String nameInData) {
		for (DataAttribute dataAttribute : dataChild.getAttributes()) {
			if (dataAttribute.getNameInData().equals(nameInData)) {
				return dataAttribute;
			}

		}
		throw DataValidationException.withMessage("No attribute found for " + nameInData);
	}

	private DataAtomic createDataAtomicFromAttribute(String mdAttributeReference,
			DataAttribute dataAttribute) {
		String nameInData = getNameInDataForAttributeReference(mdAttributeReference);
		String value = dataAttribute.getValue();

		return DataProvider.createAtomicUsingNameInDataAndValue(nameInData, value);
	}

	private void validateAttribute(String mdAttributeReference, DataAtomic dataElement) {
		CollectionVariable attributeElement = (CollectionVariable) metadataHolder
				.getMetadataElement(mdAttributeReference);
		DataElementValidator attributeValidator = new DataCollectionVariableValidator(
				metadataHolder, attributeElement);
		ValidationAnswer aValidationAnswer = attributeValidator.validateData(dataElement);
		addMessagesFromAnswerToTotalValidationAnswer(aValidationAnswer);
	}

	private void addMessagesFromAnswerToTotalValidationAnswer(ValidationAnswer aValidationAnswer) {
		validationAnswer.addErrorMessages(aValidationAnswer.getErrorMessages());
	}

	private String getNameInDataForAttributeReference(String mdAttributeReference) {
		CollectionVariable mdAttribute = (CollectionVariable) metadataHolder
				.getMetadataElement(mdAttributeReference);
		return mdAttribute.getNameInData();
	}

	private void validateDataContainsNoUnspecifiedAttributes() {
		Collection<DataAttribute> dAttributes = dataChild.getAttributes();
		for (DataAttribute attribute : dAttributes) {
			String nameInDataFromDataAttribute = attribute.getNameInData();
			validateNameInDataFromDataAttributeIsSpecifiedInMetadata(nameInDataFromDataAttribute);
		}
	}

	private void validateNameInDataFromDataAttributeIsSpecifiedInMetadata(String dataNameInData) {
		if (!isNameInDataFromDataSpecifiedInMetadata(dataNameInData)) {
			validationAnswer.addErrorMessage(
					"Data attribute with id: " + dataNameInData + " does not exist in metadata.");
		}
	}

	private boolean isNameInDataFromDataSpecifiedInMetadata(String dataNameInData) {
		Collection<String> mdAttributeReferences = metadataElement.getAttributeReferences();
		for (String mdAttributeReference : mdAttributeReferences) {
			String metadataNameInData = getNameInDataForAttributeReference(mdAttributeReference);
			if (dataNameInData.equals(metadataNameInData)) {
				return true;
			}
		}
		return false;
	}

	public MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}
}