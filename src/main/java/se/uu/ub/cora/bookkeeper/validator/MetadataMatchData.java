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
import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;

public final class MetadataMatchData {

	private MetadataHolder metadataHolder;
	private MetadataElement metadataElement;
	private DataElement dataElement;
	private ValidationAnswer validationAnswer;

	public static MetadataMatchData withMetadataHolder(MetadataHolder metadataHolder) {
		return new MetadataMatchData(metadataHolder);
	}

	private MetadataMatchData(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	public ValidationAnswer metadataSpecifiesData(MetadataElement metadataElement,
			DataElement dataElement) {
		this.metadataElement = metadataElement;
		this.dataElement = dataElement;
		validationAnswer = new ValidationAnswer();
		validateNameInData();
		validateAttributes();
		return validationAnswer;
	}

	private void validateNameInData() {
		String metadataNameInData = metadataElement.getNameInData();
		String dataNameInData = dataElement.getNameInData();
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

		Map<String, String> dataAttributes = dataElement.getAttributes();
		boolean dataAttributesContainsValueForAttribute = dataAttributes.containsKey(nameInData);
		if (dataAttributesContainsValueForAttribute) {
			DataAtomic dataAtomicElement = createDataAtomicFromAttribute(mdAttributeReference,
					dataAttributes);
			validateAttribute(mdAttributeReference, dataAtomicElement);
		} else {
			validationAnswer.addErrorMessage(
					"Attribute with nameInData: " + nameInData + " does not exist in data.");
		}
	}

	private DataAtomic createDataAtomicFromAttribute(String mdAttributeReference,
			Map<String, String> dataAttributes) {
		String nameInData = getNameInDataForAttributeReference(mdAttributeReference);
		String value = dataAttributes.get(nameInData);
		return DataAtomic.withNameInDataAndValue(nameInData, value);
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
		Map<String, String> dAttributes = dataElement.getAttributes();
		for (Entry<String, String> attribute : dAttributes.entrySet()) {
			String nameInDataFromDataAttribute = attribute.getKey();
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
}