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
	private DataRecordLink data;
	private DataToDataLink dataLink;

	public DataRecordLinkValidator(DataToDataLink dataLink) {
		this.dataLink = dataLink;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		validationAnswer = new ValidationAnswer();
		data = (DataRecordLink) dataElement;
		validateNoEmptyValues();
		validateTargetRecordType();
		return validationAnswer;
	}

	private void validateNoEmptyValues() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataRecordLink must have a nonempty nameInData");
		}
		if (recordTypeIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordType as child.");
		}
		if (recordIdIsEmpty()) {
			validationAnswer.addErrorMessage(
					createNameInDataMessagePart() + " must have an nonempty recordId as child.");
		}
	}

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + data.getNameInData();
	}

	private boolean nameInDataIsEmpty() {
		return data.getNameInData().isEmpty();
	}

	private boolean recordTypeIsEmpty() {
		return data.getRecordType().isEmpty();
	}

	private boolean recordIdIsEmpty() {
		return data.getRecordId().isEmpty();
	}

	private void validateTargetRecordType() {
		if (incomingRecordTypeNotSameAsSpecifiedInMetadata()) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an recordType:" + dataLink.getTargetRecordType());
		}
	}

	private boolean incomingRecordTypeNotSameAsSpecifiedInMetadata() {
		return !data.getRecordType().equals(dataLink.getTargetRecordType());
	}

}
