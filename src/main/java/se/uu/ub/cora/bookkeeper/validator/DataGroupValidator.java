/*
 * Copyright 2015 Uppsala University Library
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
import java.util.HashSet;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;

class DataGroupValidator implements DataElementValidator {

	private DataValidatorFactoryImp dataValidatorFactoryImp;
	private final MetadataGroup metadataGroup;
	private MetadataHolder metadataHolder;
	private DataGroup dataGroup;
	private ValidationAnswer validationAnswer;

	public DataGroupValidator(DataValidatorFactoryImp dataValidatorFactoryImp,
			MetadataHolder metadataHolder, MetadataGroup metadataGroup) {
		this.dataValidatorFactoryImp = dataValidatorFactoryImp;
		this.metadataHolder = metadataHolder;
		this.metadataGroup = metadataGroup;
	}

	/**
	 * validateData validates that the entered dataGroup is correct according to
	 * this validators metadataGroup
	 *
	 * @param dataGroup
	 *            A DataGroup to validate
	 * @return A ValidationAnswer with information if the dataGroup has valid
	 *         data and if not a list of errors
	 */
	@Override
	public ValidationAnswer validateData(DataElement dataGroup) {
		this.dataGroup = (DataGroup) dataGroup;
		validationAnswer = new ValidationAnswer();
		validateNameInDataAndAttributes(dataGroup);
		validateChildren();
		return validationAnswer;
	}

	private void validateNameInDataAndAttributes(DataElement dataGroup) {
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		ValidationAnswer va = metadataMatchData.metadataSpecifiesData(metadataGroup,
				dataGroup);
		addMessagesFromAnswerToTotalValidationAnswer(va);
	}

	private void validateChildren() {
		validateDataContainsAllRequiredChildrenWithCorrectValues();
		validateDataContainsNoUnspecifiedChildren();
	}

	private void validateDataContainsAllRequiredChildrenWithCorrectValues() {
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference childReference : childReferences) {
			validateDataContainsRequiredChildReferenceWithCorrectValue(childReference);
		}
	}

	private void validateDataContainsRequiredChildReferenceWithCorrectValue(
			MetadataChildReference childReference) {
		String referenceId = childReference.getReferenceId();
		boolean mayBeRepeated = childReference.getRepeatMax() > 1;

		int childrenFound = validateAndCountChildrenWithReferenceId(referenceId, mayBeRepeated);
		validateRepeatMinAndMax(childReference, childrenFound);
	}

	private int validateAndCountChildrenWithReferenceId(String referenceId, boolean mayBeRepeated) {
		int childrenFound = 0;
		Set<String> repeatIds = new HashSet<>();
		for (DataElement childData : dataGroup.getChildren()) {
			if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
				childrenFound++;
				validateRepeatId(mayBeRepeated, repeatIds, childData);
				validateChildElementData(referenceId, childData);
			}
		}
		return childrenFound;
	}

	private void validateRepeatId(boolean mayBeRepeated, Set<String> repeatIds,
			DataElement childData) {
		if (mayBeRepeated) {
			validateRepeatId(repeatIds, childData);
		} else {
			validateNoRepeatId(childData);
		}
	}

	private void validateRepeatId(Set<String> repeatIds, DataElement childData) {
		String repeatId = childData.getRepeatId();
		if (repeatId == null || repeatId.isEmpty()) {
			validationAnswer.addErrorMessage(
					createIdentifiedErrorMessage(childData) + " must have non empty repeatId");
		} else {
			validateUniqueRepeatId(repeatIds, childData);
		}
	}

	private String createIdentifiedErrorMessage(DataElement childData) {
		return "Repeatable child " + childData.getNameInData() + " in group "
				+ dataGroup.getNameInData();
	}

	private void validateUniqueRepeatId(Set<String> repeatIds, DataElement childData) {
		String repeatId = childData.getRepeatId();
		if (repeatIds.contains(repeatId)) {
			validationAnswer.addErrorMessage(createIdentifiedErrorMessage(childData)
					+ " must have unique repeatId: " + repeatId);
		} else {
			repeatIds.add(repeatId);
		}
	}

	private void validateRepeatMinAndMax(MetadataChildReference childReference, int childrenFound) {
		String referenceId = childReference.getReferenceId();
		if (childrenFound < childReference.getRepeatMin()) {
			validationAnswer.addErrorMessage(
					"Did not find enough data children with referenceId: " + referenceId + ".");
		}
		if (childrenFound > childReference.getRepeatMax()) {
			validationAnswer.addErrorMessage(
					"Found too many data children with referenceId: " + referenceId + ".");
		}
	}

	private boolean isChildDataSpecifiedByChildReferenceId(DataElement childData,
			String referenceId) {
		MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
		MetadataMatchData metadataMatchData = MetadataMatchData.withMetadataHolder(metadataHolder);
		return metadataMatchData.metadataSpecifiesData(childElement, childData).dataIsValid();
}
		


	private void validateNoRepeatId(DataElement childData) {
		String repeatId = childData.getRepeatId();
		if (repeatId != null) {
			validationAnswer.addErrorMessage(
					createIdentifiedErrorMessage(childData) + " can not have a repeatId");
		}
	}

	private void validateChildElementData(String referenceId, DataElement childData) {
		DataElementValidator childValidator = dataValidatorFactoryImp.factor(referenceId);
		ValidationAnswer va = childValidator.validateData(childData);
		addMessagesFromAnswerToTotalValidationAnswer(va);
	}

	private void addMessagesFromAnswerToTotalValidationAnswer(ValidationAnswer aValidationAnswer) {
		validationAnswer.addErrorMessages(aValidationAnswer.getErrorMessages());
	}

	private void validateDataContainsNoUnspecifiedChildren() {
		for (DataElement childData : dataGroup.getChildren()) {
			if (!isChildDataSpecifiedInMetadataGroup(childData)) {
				validationAnswer.addErrorMessage("Could not find metadata for child with nameInData: "
						+ childData.getNameInData() + " and a matching set of attributes.");
			}
		}
	}

	private boolean isChildDataSpecifiedInMetadataGroup(DataElement childData) {
		Collection<MetadataChildReference> childReferences = metadataGroup.getChildReferences();
		for (MetadataChildReference childReference : childReferences) {
			if (isChildDataSpecifiedByChildReferenceId(childData,
					childReference.getReferenceId())) {
				return true;
			}
		}
		return false;
	}
}
