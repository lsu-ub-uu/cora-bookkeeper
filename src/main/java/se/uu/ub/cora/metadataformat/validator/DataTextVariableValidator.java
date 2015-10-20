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

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

public class DataTextVariableValidator implements DataElementValidator {

	private TextVariable textVariable;

	public DataTextVariableValidator(TextVariable textVariable) {
		this.textVariable = textVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		DataAtomic dataAtomic = (DataAtomic) dataElement;
		ValidationAnswer validationAnswer = new ValidationAnswer();
		if (!dataIsValidAccordingToRegEx(dataAtomic)) {
			validationAnswer.addErrorMessage("TextVariable with nameInData:" + dataAtomic.getNameInData()
					+ " is NOT valid, regular expression(" + textVariable.getRegularExpression()
					+ ") does not match:" + dataAtomic.getValue());
		}
		return validationAnswer;
	}

	private boolean dataIsValidAccordingToRegEx(DataAtomic dataElement) {
		return dataElement.getValue().matches(textVariable.getRegularExpression());
	}
}
