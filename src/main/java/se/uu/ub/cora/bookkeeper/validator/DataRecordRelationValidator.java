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

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordRelation;

public class DataRecordRelationValidator implements DataElementValidator {

	private DataValidatorFactoryImp dataValidatorFactoryImp;
	private MetadataHolder metadataHolder;
	private RecordRelation recordRelation;
	protected DataGroup dataGroup;
	protected ValidationAnswer validationAnswer;

	public DataRecordRelationValidator(DataValidatorFactoryImp dataValidatorFactoryImp,
			MetadataHolder metadataHolder, RecordRelation recordRelation) {
		this.dataValidatorFactoryImp = dataValidatorFactoryImp;
		this.metadataHolder = metadataHolder;
		this.recordRelation = recordRelation;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataGroup) {
		this.dataGroup = (DataGroup) dataGroup;
		validationAnswer = new ValidationAnswer();
		validateNameInData();
		validateLink();
		validateGroup();
		return validationAnswer;
	}

	private void validateNameInData() {
		String metadataNameInData = recordRelation.getNameInData();
		String dataNameInData = dataGroup.getNameInData();
		if (!metadataNameInData.equals(dataNameInData)) {
			validationAnswer.addErrorMessage("DataGroup should have name(nameInData): "
					+ recordRelation.getNameInData() + " it does not.");
		}
	}

	private void validateLink() {
		String refRecordLinkId = recordRelation.getRefRecordLinkId();
		int noOfChildrenFound = validateAndCountChildrenWithReferenceId(refRecordLinkId);
		if (noOfChildrenFound != 1) {
			validationAnswer
					.addErrorMessage("RecordRelation should have one and only one recordLink");
		}
	}

	private int validateAndCountChildrenWithReferenceId(String referenceId) {
		int childrenFound = 0;
		for (DataElement childData : dataGroup.getChildren()) {
			if (isChildDataSpecifiedByChildReferenceId(childData, referenceId)) {
				childrenFound++;
				validateNoRepeatId(childData);
				validateChildElementData(referenceId, childData);
			}
		}
		return childrenFound;
	}

	private String createIdentifiedErrorMessage(DataElement childData) {
		return "Repeatable child " + childData.getNameInData() + " in group "
				+ dataGroup.getNameInData();
	}

	private boolean isChildDataSpecifiedByChildReferenceId(DataElement childData,
			String referenceId) {
		MetadataElement childElement = metadataHolder.getMetadataElement(referenceId);
		if (childElement == null) {
			throw DataValidationException.withMessage(referenceId + " not found in metadataHolder");
		}
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

	private void validateGroup() {
		String refMetadataGroupId = recordRelation.getRefMetadataGroupId();
		int noOfChildrenFound = validateAndCountChildrenWithReferenceId(refMetadataGroupId);
		if (noOfChildrenFound != 1) {
			validationAnswer.addErrorMessage(
					"RecordRelation should have data for one and only one metadataGroup with id:"
							+ refMetadataGroupId + " (found:" + noOfChildrenFound + ")");
		}
	}

}
