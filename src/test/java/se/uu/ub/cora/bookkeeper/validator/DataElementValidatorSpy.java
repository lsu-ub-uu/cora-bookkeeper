package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.data.DataElement;

public class DataElementValidatorSpy implements DataElementValidator {

	public DataElement dataElement;
	public int numOfInvalidMessages = 0;

	@Override
	public ValidationAnswer validateData(DataElement dataElement) {
		this.dataElement = dataElement;

		ValidationAnswer validationAnswer = new ValidationAnswer();
		for (int i = 0; i < numOfInvalidMessages; i++) {
			validationAnswer.addErrorMessage("an errorMessageFromSpy " + i);

		}
		return validationAnswer;
	}

}
