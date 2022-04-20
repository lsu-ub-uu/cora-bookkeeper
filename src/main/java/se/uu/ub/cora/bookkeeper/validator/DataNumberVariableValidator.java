/*
 * Copyright 2018, 2019, 2021 Uppsala University Library
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

import java.text.MessageFormat;

import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;

class DataNumberVariableValidator implements DataElementValidator {

	private NumberVariable numberVariable;
	private double dataValue;
	private String dataStringValue;

	public DataNumberVariableValidator(NumberVariable numberVariable) {
		this.numberVariable = numberVariable;
	}

	@Override
	public ValidationAnswer validateData(DataChild dataElement) {
		dataValue = getDataValueAsDouble((DataAtomic) dataElement);
		ValidationAnswer validationAnswer = new ValidationAnswer();
		addErrorIfDataIsNotValid(validationAnswer);
		return validationAnswer;
	}

	private double getDataValueAsDouble(DataAtomic dataAtomic) {
		dataStringValue = dataAtomic.getValue();
		return Double.parseDouble(dataStringValue);
	}

	private void addErrorIfDataIsNotValid(ValidationAnswer validationAnswer) {
		addErrorIfOutsideMinMax(validationAnswer);
		addErrorIfMoreDecimalsThanAllowed(validationAnswer);
	}

	private void addErrorIfOutsideMinMax(ValidationAnswer validationAnswer) {
		if (valueIsOutsideAllowedMinMax(dataValue)) {
			String message = "NumberVariable with nameInData: {0}"
					+ " is NOT valid, value {1} is outside range of {2} - {3}";
			validationAnswer.addErrorMessage(addValuesToErrorMessage(message));
		}
	}

	private String addValuesToErrorMessage(String message) {
		return MessageFormat.format(message, numberVariable.getNameInData(), dataValue,
				numberVariable.getMin(), numberVariable.getMax());
	}

	private void addErrorIfMoreDecimalsThanAllowed(ValidationAnswer validationAnswer) {
		int numOfDecimals = getNumberOfDecimals();
		if (numOfDecimals > numberVariable.getNumOfDecmials()) {
			String message = "NumberVariable with nameInData: {0}"
					+ " is NOT valid, value {1} has more decimals than the allowed {2}";
			validationAnswer.addErrorMessage(addValuesToDecimalsError(message));
		}
	}

	private String addValuesToDecimalsError(String message) {
		return MessageFormat.format(message, numberVariable.getNameInData(), dataStringValue,
				numberVariable.getNumOfDecmials());
	}

	private int getNumberOfDecimals() {
		String[] valueSplittedOnDecimal = dataStringValue.split("\\.");
		int noDecimals = 0;
		return valueHasDecimals(valueSplittedOnDecimal) ? numOfDecimals(valueSplittedOnDecimal)
				: noDecimals;
	}

	private boolean valueHasDecimals(String[] valueSplittedOnDecimal) {
		return valueSplittedOnDecimal.length > 1;
	}

	private int numOfDecimals(String[] valueSplittedOnDecimal) {
		return valueSplittedOnDecimal[1].length();
	}

	private boolean valueIsOutsideAllowedMinMax(double dataAsDouble) {
		return valueIsBelowAcceptedMinValue(dataAsDouble)
				|| valueIsAboveMaxAllowedValue(dataAsDouble);
	}

	private boolean valueIsAboveMaxAllowedValue(double dataAsDouble) {
		return dataAsDouble > numberVariable.getMax();
	}

	private boolean valueIsBelowAcceptedMinValue(double dataAsDouble) {
		return dataAsDouble < numberVariable.getMin();
	}

}
