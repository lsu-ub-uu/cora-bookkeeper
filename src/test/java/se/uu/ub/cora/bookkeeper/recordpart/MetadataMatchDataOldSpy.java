/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class MetadataMatchDataOldSpy implements MetadataMatchData {

	public MetadataElement metadataElement;
	public DataChild dataElement;
	public boolean isValid = true;

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public ValidationAnswer metadataSpecifiesData(MetadataElement metadataElement,
			DataChild dataElement) {
		MCR.addCall("metadataElement", metadataElement, "dataElement", dataElement);
		this.metadataElement = metadataElement;
		this.dataElement = dataElement;
		ValidationAnswer validationAnswer = new ValidationAnswer();
		if (!isValid) {
			validationAnswer.addErrorMessage("some message");
		}
		MCR.addReturned(validationAnswer);
		return validationAnswer;
	}

}
