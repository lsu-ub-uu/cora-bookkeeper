/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

class DataResourceLinkValidator implements DataElementValidator {

	private static final String STREAM_ID = "streamId";
	private ValidationAnswer validationAnswer;
	private MetadataHolder metadataHolder;
	private DataGroup dataForResourceLink;

	public DataResourceLinkValidator(MetadataHolder metadataHolder) {
		this.metadataHolder = metadataHolder;
	}

	@Override
	public ValidationAnswer validateData(DataChild dataElement) {
		validationAnswer = new ValidationAnswer();
		dataForResourceLink = (DataGroup) dataElement;
		validateNameInData();
		validateStreamId();
		validateFileName();
		validateFileSize();
		validateMimeType();
		return validationAnswer;
	}

	private void validateNameInData() {
		if (nameInDataIsEmpty()) {
			validationAnswer.addErrorMessage("DataResourceLink must have a nonempty nameInData");
		}
	}

	private boolean nameInDataIsEmpty() {
		return dataForResourceLink.getNameInData().isEmpty();
	}

	private void validateStreamId() {
		validateChild("streamIdTextVar", STREAM_ID);
	}

	private void validateChild(String metadataId, String nameInData) {
		if (childIsMissing(nameInData)) {
			validationAnswer.addErrorMessage(createNameInDataMessagePart()
					+ " must have an nonempty " + nameInData + " as child.");
		} else {
			validateChildValue(metadataId, nameInData);
		}
	}

	private boolean childIsMissing(String nameInData) {
		return !dataForResourceLink.containsChildWithNameInData(nameInData);
	}

	private void validateChildValue(String metdataId, String nameInData) {
		validateTextVariableValueByMetadataIdAndNameInData(metdataId, nameInData);
	}

	private void validateTextVariableValueByMetadataIdAndNameInData(String metadataId,
			String nameInData) {
		DataTextVariableValidator dataValidator = createDataValidator(metadataId);
		DataChild linkedRecordIdData = dataForResourceLink
				.getFirstChildWithNameInData(nameInData);
		validateTextVariableData(dataValidator, linkedRecordIdData);
	}

	private void validateTextVariableData(DataTextVariableValidator dataValidator,
			DataChild textVariableData) {
		ValidationAnswer va = dataValidator.validateData(textVariableData);

		if (va.dataIsInvalid()) {
			validationAnswer.addErrorMessages(va.getErrorMessages());
		}
	}

	private DataTextVariableValidator createDataValidator(String metadataId) {
		MetadataElement metadataElement = metadataHolder.getMetadataElement(metadataId);
		return new DataTextVariableValidator((TextVariable) metadataElement);
	}

	private String createNameInDataMessagePart() {
		return "DataRecordLink with nameInData:" + dataForResourceLink.getNameInData();
	}

	private void validateFileName() {
		validateChild("filenameTextVar", "filename");
	}

	private void validateFileSize() {
		validateChild("filesizeTextVar", "filesize");
	}

	private void validateMimeType() {
		validateChild("mimeTypeTextVar", "mimeType");
	}
}
