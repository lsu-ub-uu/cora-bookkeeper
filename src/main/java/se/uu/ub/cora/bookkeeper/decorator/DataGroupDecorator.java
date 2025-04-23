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

package se.uu.ub.cora.bookkeeper.decorator;

import java.util.Collection;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.text.TextElement;
import se.uu.ub.cora.bookkeeper.text.TextHolder;
import se.uu.ub.cora.bookkeeper.text.Translation;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchDataImp;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

class DataGroupDecorator implements DataChildDecorator {

	private DataChildDecoratorFactory dataDecoratorFactory;
	private final MetadataGroup metadataGroup;
	private MetadataHolder metadataHolder;
	// protected DataGroup dataGroup;
	protected ValidationAnswer validationAnswer;
	private TextHolder textHolder;

	DataGroupDecorator(DataChildDecoratorFactory dataDecoratorFactory,
			MetadataHolder metadataHolder, MetadataGroup metadataGroup, TextHolder textHolder) {
		this.dataDecoratorFactory = dataDecoratorFactory;
		this.metadataHolder = metadataHolder;
		this.metadataGroup = metadataGroup;
		this.textHolder = textHolder;
	}

	@Override
	public void decorateData(DataChild dataGroup) {
		decorateGroup(dataGroup);
		decorateChildren((DataGroup) dataGroup);
	}

	private void decorateGroup(DataChild dataChild) {
		String textId = metadataGroup.getTextId();
		TextElement textElement = textHolder.getTextElement(textId);
		Set<Translation> translations = textElement.getTranslations();
		addTranslationsAsAttributes(dataChild, translations);
	}

	private void addTranslationsAsAttributes(DataChild dataChild, Set<Translation> translations) {
		translations.forEach(translation -> addAttribute(dataChild, translation));
	}

	private void addAttribute(DataChild dataChild, Translation translation) {
		String language = attributeNameWithUnderscore(translation.language());
		String text = translation.text();
		dataChild.addAttributeByIdWithValue(language, text);
	}

	private String attributeNameWithUnderscore(String language) {
		return "_" + language;
	}

	private void decorateChildren(DataGroup dataGroup) {
		// if (!dataGroup.hasChildren()) Do we need to handle it?
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference childReference : childReferences) {
			String referenceId = childReference.getLinkedRecordId();
			for (DataChild childData : dataGroup.getChildren()) {
				if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
					DataChildDecorator decorator = dataDecoratorFactory.factor(referenceId);
					decorator.decorateData(childData);
				}
			}
		}
	}

	private boolean isChildDataSpecifiedByChildReferenceId(DataChild childData,
			String referenceId) {
		if (!metadataHolder.containsElement(referenceId)) {
			throw DataValidationException.withMessage(referenceId + " not found in metadataHolder");
		}
		MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
		MetadataMatchData metadataMatchData = MetadataMatchDataImp
				.withMetadataHolder(metadataHolder);
		return metadataMatchData.metadataSpecifiesData(childElement, childData).dataIsValid();
	}

	////////////////  OLD CODE /////////////////////
	///
	// @Override
	// public ValidationAnswer validateData(DataChild dataGroup) {
	// this.dataGroup = (DataGroup) dataGroup;
	// validationAnswer = new ValidationAnswer();
	// validateNameInDataAndAttributes(dataGroup);
	// validateChildren();
	// return validationAnswer;
	// }

	// private void validateNameInDataAndAttributes(DataChild dataGroup) {
	// MetadataMatchData metadataMatchData = MetadataMatchDataImp
	// .withMetadataHolder(metadataHolder);
	// ValidationAnswer va = metadataMatchData.metadataSpecifiesData(metadataGroup, dataGroup);
	// addMessagesFromAnswerToTotalValidationAnswer(va);
	// }

	// private void validateChildren() {
	// validateHasChildren();
	// validateDataContainsAllRequiredChildrenWithCorrectValues();
	// validateDataContainsNoUnspecifiedChildren();
	// }
	//
	// private void validateHasChildren() {
	// if (!dataGroup.hasChildren()) {
	// validationAnswer.addErrorMessage("DataGroup " + metadataGroup.getNameInData()
	// + " should have children, it does not.");
	// }
	// }
	//
	// private void validateDataContainsAllRequiredChildrenWithCorrectValues() {
	// Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
	// for (MetadataChildReference childReference : childReferences) {
	// validateDataContainsRequiredChildReferenceWithCorrectValue(childReference);
	// }
	// }
	//
	// private void validateDataContainsRequiredChildReferenceWithCorrectValue(
	// MetadataChildReference childReference) {
	// String referenceId = childReference.getLinkedRecordId();
	// boolean mayBeRepeated = childReference.getRepeatMax() > 1;
	//
	// int childrenFound = validateAndCountChildrenWithReferenceId(referenceId, mayBeRepeated);
	// validateRepeatMinAndMax(childReference, childrenFound);
	// }
	//
	// private int validateAndCountChildrenWithReferenceId(String referenceId, boolean
	// mayBeRepeated) {
	// int childrenFound = 0;
	// Set<String> repeatIds = new HashSet<>();
	// for (DataChild childData : dataGroup.getChildren()) {
	// if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
	// childrenFound++;
	// validateRepeatId(mayBeRepeated, repeatIds, childData);
	// validateChildElementData(referenceId, childData);
	// }
	// }
	// return childrenFound;
	// }
	//
	// private void validateRepeatId(boolean mayBeRepeated, Set<String> repeatIds,
	// DataChild childData) {
	// if (mayBeRepeated) {
	// validateRepeatId(repeatIds, childData);
	// } else {
	// validateNoRepeatId(childData);
	// }
	// }
	//
	// private void validateRepeatId(Set<String> repeatIds, DataChild childData) {
	// String repeatId = childData.getRepeatId();
	// if (repeatId == null || repeatId.isEmpty()) {
	// validationAnswer.addErrorMessage(
	// createIdentifiedErrorMessage(childData) + " must have non empty repeatId");
	// } else {
	// validateUniqueRepeatId(repeatIds, childData);
	// }
	// }
	//
	// private String createIdentifiedErrorMessage(DataChild childData) {
	// return "Repeatable child " + childData.getNameInData() + " in group "
	// + dataGroup.getNameInData();
	// }
	//
	// private void validateUniqueRepeatId(Set<String> repeatIds, DataChild childData) {
	// String repeatId = childData.getRepeatId();
	// if (repeatIds.contains(repeatId)) {
	// validationAnswer.addErrorMessage(createIdentifiedErrorMessage(childData)
	// + " must have unique repeatId: " + repeatId);
	// } else {
	// repeatIds.add(repeatId);
	// }
	// }
	//
	// private void validateRepeatMinAndMax(MetadataChildReference childReference, int
	// childrenFound) {
	// String referenceId = childReference.getLinkedRecordId();
	//
	// if (childrenFound < childReference.getRepeatMin()) {
	// validationAnswer.addErrorMessage("Did not find enough data children with referenceId: "
	// + referenceId + getReferenceText(referenceId) + ".");
	// }
	// if (childrenFound > childReference.getRepeatMax()) {
	// validationAnswer.addErrorMessage(
	// "Found too many data children with referenceId: " + referenceId + ".");
	// }
	// }
	//
	// private String getReferenceText(String referenceId) {
	// MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
	// StringBuilder sb = new StringBuilder();
	// sb.append("(with nameInData:");
	// sb.append(childElement.getNameInData());
	// sb.append(getAttributesTextForMetadata(childElement));
	// sb.append(")");
	// return sb.toString();
	// }
	//
	// private String getAttributesTextForMetadata(MetadataElement childElement) {
	// if (childElement.getAttributeReferences().isEmpty()) {
	// return "";
	// }
	// return getTextForExistingMetadataAttributes(childElement);
	// }
	//
	// private String getTextForExistingMetadataAttributes(MetadataElement childElement) {
	// StringJoiner joiner = new StringJoiner(", ");
	// for (String attributeRef : childElement.getAttributeReferences()) {
	// CollectionVariable attributeElement = (CollectionVariable) metadataHolder
	// .getMetadataElement(attributeRef);
	//
	// joiner.add(attributeElement.getNameInData() + ":" + attributeElement.getFinalValue());
	// }
	// return " and attributes: " + joiner.toString();
	// }
	//
	// private boolean isChildDataSpecifiedByChildReferenceId(DataChild childData,
	// String referenceId) {
	// if (!metadataHolder.containsElement(referenceId)) {
	// throw DataValidationException.withMessage(referenceId + " not found in metadataHolder");
	// }
	// MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
	// MetadataMatchData metadataMatchData = MetadataMatchDataImp
	// .withMetadataHolder(metadataHolder);
	// return metadataMatchData.metadataSpecifiesData(childElement, childData).dataIsValid();
	// }
	//
	// private void validateNoRepeatId(DataChild childData) {
	// String repeatId = childData.getRepeatId();
	// if (repeatId != null) {
	// validationAnswer.addErrorMessage(
	// createIdentifiedErrorMessage(childData) + " can not have a repeatId");
	// }
	// }
	//
	// private void validateChildElementData(String referenceId, DataChild childData) {
	// DataElementValidator childValidator = dataDecoratorFactory.factor(referenceId);
	// ValidationAnswer va = childValidator.validateData(childData);
	// addMessagesFromAnswerToTotalValidationAnswer(va);
	// }
	//
	// private void addMessagesFromAnswerToTotalValidationAnswer(ValidationAnswer aValidationAnswer)
	// {
	// validationAnswer.addErrorMessages(aValidationAnswer.getErrorMessages());
	// }
	//
	// private void validateDataContainsNoUnspecifiedChildren() {
	// for (DataChild childData : dataGroup.getChildren()) {
	// if (!isChildDataSpecifiedInMetadataGroup(childData)) {
	// validationAnswer
	// .addErrorMessage("Could not find metadata for child with nameInData: "
	// + childData.getNameInData() + getAttributesText(childData));
	// }
	// }
	// }
	//
	// private boolean isChildDataSpecifiedInMetadataGroup(DataChild childData) {
	// Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
	// for (MetadataChildReference childReference : childReferences) {
	// String referenceId = childReference.getLinkedRecordId();
	// if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private String getAttributesText(DataChild childData) {
	// if (childData.hasAttributes()) {
	// return getTextForExistingDataAttributes(childData);
	// }
	// return "";
	// }
	//
	// private String getTextForExistingDataAttributes(DataChild childData) {
	// StringJoiner joiner = new StringJoiner(", ");
	// Collection<DataAttribute> attributes = childData.getAttributes();
	//
	// Comparator<? super DataAttribute> attributeComparator = (o1, o2) -> o1.getNameInData()
	// .compareTo(o2.getNameInData());
	// for (DataAttribute dataAttribute : attributes.stream().sorted(attributeComparator)
	// .toList()) {
	// joiner.add(dataAttribute.getNameInData() + ":" + dataAttribute.getValue());
	// }
	// return " and attributes: " + joiner.toString();
	// }

	DataChildDecoratorFactory onlyForTestGetDataElementValidatorFactory() {
		return dataDecoratorFactory;
	}

	MetadataHolder onlyForTestGetMetadataHolder() {
		return metadataHolder;
	}

	MetadataElement onlyForTestGetMetadataElement() {
		return metadataGroup;
	}

}
