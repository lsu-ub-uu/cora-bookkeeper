/*
 * Copyright 2018, 2019 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;

class DataNumberVariableValidator implements DataElementValidator {

	private NumberVariable numberVariable;
	private double dataValue;
	private String dataStringValue;

	public DataNumberVariableValidator(NumberVariable numberVariable) {
		this.numberVariable = numberVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
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
			validationAnswer.addErrorMessage("");
		}
	}

	private void addErrorIfMoreDecimalsThanAllowed(ValidationAnswer validationAnswer) {
		int numOfDecimals = getNumberOfDecimals();
		if (numOfDecimals > numberVariable.getNumOfDecmials()) {
			validationAnswer.addErrorMessage("");
		}
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
