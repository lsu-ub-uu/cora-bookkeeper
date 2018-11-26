package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;

public class DataNumberVariableValidator implements DataElementValidator {

	private NumberVariable numberVariable;
	private double dataValue;
	private String dataStringValue;

	public DataNumberVariableValidator(NumberVariable numberVariable) {
		this.numberVariable = numberVariable;
	}

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		dataValue = getDataValueAsDouble(dataElement);
		ValidationAnswer validationAnswer = new ValidationAnswer();
		addErrorIfDataIsNotValid(validationAnswer);
		return validationAnswer;
	}

	private double getDataValueAsDouble(DataElement dataElement) {
		DataAtomic dataAtomic = (DataAtomic) dataElement;
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
