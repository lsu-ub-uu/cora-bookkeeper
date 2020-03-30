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

import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;

class DataTextVariableValidator implements DataElementValidator {

	private TextVariable textVariable;
	private String dataValue;

	public DataTextVariableValidator(TextVariable textVariable) {
		this.textVariable = textVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		DataAtomic dataAtomic = (DataAtomic) dataElement;
		dataValue = dataAtomic.getValue();
		if (finalValueIsDefinedInMetadata()) {
			return validateDataValueIsFinalValue();
		}
		return validateDataValueIsValidAccordingToRegEx();
	}

	private boolean finalValueIsDefinedInMetadata() {
		return null != textVariable.getFinalValue();
	}

	private ValidationAnswer validateDataValueIsFinalValue() {
		if (dataValueIsFinalValue()) {
			return new ValidationAnswer();
		}
		return createErrorMessageForFinalValue();
	}

	private boolean dataValueIsFinalValue() {
		return textVariable.getFinalValue().equals(dataValue);
	}

	private ValidationAnswer createErrorMessageForFinalValue() {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessage(
				"Value:" + dataValue + " is not finalValue:" + textVariable.getFinalValue());
		return validationAnswer;
	}

	private ValidationAnswer validateDataValueIsValidAccordingToRegEx() {
		if (dataIsInvalidAccordingToRegEx()) {
			return createValidationAnswerWithError();
		}
		return new ValidationAnswer();
	}

	private boolean dataIsInvalidAccordingToRegEx() {
		return !dataValue.matches(textVariable.getRegularExpression());
	}

	private ValidationAnswer createValidationAnswerWithError() {
		ValidationAnswer validationAnswer = new ValidationAnswer();
		validationAnswer.addErrorMessage("TextVariable with nameInData:"
				+ textVariable.getNameInData() + " is NOT valid, regular expression("
				+ textVariable.getRegularExpression() + ") does not match:" + dataValue);
		return validationAnswer;
	}

}
