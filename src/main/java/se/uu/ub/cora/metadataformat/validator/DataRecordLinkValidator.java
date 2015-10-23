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

package se.uu.ub.cora.metadataformat.validator;

import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataRecordLinkValidator implements DataElementValidator {

	private ValidationAnswer validationAnswer;
	private DataRecordLink dataRecordLink;
	private DataToDataLink dataToDataLink;

	public DataRecordLinkValidator(DataToDataLink dataLink) {
		this.dataToDataLink = dataLink;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		dataRecordLink = (DataRecordLink) dataElement;
		validateNameInData();
		validateRecordType();
		validateRecordId();
		validateLinkedPath();
		validateLinkedRepeatId();
		return validationAnswer;
	}

	private void validateNameInData() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink must have a nonempty nameInData");
		}
	}

	private void validateRecordType() {
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordType as child.");
		}
		if (incomingRecordTypeNotSameAsSpecifiedInMetadata()) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an recordType:" + dataToDataLink.getTargetRecordType());
		}
	}

	private boolean recordTypeIsEmpty() {
		return dataRecordLink.getRecordType().isEmpty();
	}

	private boolean incomingRecordTypeNotSameAsSpecifiedInMetadata() {
		return !dataRecordLink.getRecordType().equals(dataToDataLink.getTargetRecordType());
	}

	private void validateRecordId() {
		if (recordIdIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
		}
	}

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + dataRecordLink.getNameInData();
	}

	private boolean nameInDataIsEmpty() {
		return dataRecordLink.getNameInData().isEmpty();
	}

	private boolean recordIdIsEmpty() {
		return dataRecordLink.getRecordId().isEmpty();
	}

	private void validateLinkedPath() {
		if (dataShouldContainALinkedPath()) {
			validateLinkedPathIsSameAsSpecifiedInMetadata();
		} else {
			validateDoesNotHaveLinkedPath();
		}
	}

	private void validateLinkedPathIsSameAsSpecifiedInMetadata() {
		if (linkedPathIsMissing()) {
			validationAnswer
					.addErrorMessage(createNameInDataMessagePart() + " should have a linkedPath");
		} else {
			if (notSameAsSpecifiedPath()) {
				validationAnswer.addErrorMessage(createNameInDataMessagePart()
						+ " should have a linkedPath as " + "specified in metadata");
			}
		}
	}

	private boolean linkedPathIsMissing() {
		return dataRecordLink.getLinkedPath() == null;
	}

	private boolean notSameAsSpecifiedPath() {
		// TODO Auto-generated method stub

		return false;
	}

	private void validateDoesNotHaveLinkedPath() {
		if (thereIsALinkedPath()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should not have a linkedPath");
		}
	}

	private boolean thereIsALinkedPath() {
		return !linkedPathIsMissing();
	}

	private boolean dataShouldContainALinkedPath() {
		return dataToDataLink.getLinkedPath() != null;
	}

	private void validateLinkedRepeatId() {
		if (dataShouldContainALinkedRepeatId()) {
			validateHasLinkedRepeatId();
		} else {
			validateDoesNotHaveLinkedRepeatId();
		}
	}

	private boolean dataShouldContainALinkedRepeatId() {
		return dataToDataLink.getLinkedPath() != null;
	}

	private void validateHasLinkedRepeatId() {
		if (linkedRepeatIdIsMissingOrEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should have a linkedRepeatId");
		}
	}

	private boolean linkedRepeatIdIsMissingOrEmpty() {
		return dataRecordLink.getLinkedRepeatId() == null
				|| dataRecordLink.getLinkedRepeatId().isEmpty();
	}

	private void validateDoesNotHaveLinkedRepeatId() {
		if (thereIsANonEmptyLinkedRepeatId()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " should not have a linkedRepeatId");
		}
	}

	private boolean thereIsANonEmptyLinkedRepeatId() {
		return !linkedRepeatIdIsMissingOrEmpty();
	}

}
