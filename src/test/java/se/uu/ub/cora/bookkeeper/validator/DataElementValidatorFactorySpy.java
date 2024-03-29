package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataElementValidatorFactorySpy implements DataElementValidatorFactory {

	public DataElementValidatorSpy elementValidator;
	public boolean factorWasCalled = false;
	public String metadataIdSentToFactory;
	public int numOfInvalidMessages = 0;
	public boolean throwError = false;

	MethodCallRecorder MCR = new MethodCallRecorder();

	@SuppressWarnings("exports")
	@Override
	public DataElementValidator factor(String elementId) {
		MCR.addCall("elementId", elementId);

		factorWasCalled = true;
		metadataIdSentToFactory = elementId;
		if (throwError) {
			throw DataValidationException.withMessage("Error from validatorFactorySpy");
		}
		elementValidator = new DataElementValidatorSpy();
		elementValidator.numOfInvalidMessages = numOfInvalidMessages;

		MCR.addReturned(elementValidator);
		return elementValidator;
	}

}
