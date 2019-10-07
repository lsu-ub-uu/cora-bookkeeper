package se.uu.ub.cora.bookkeeper.validator;

public class DataValidatorFactorySpy implements DataValidatorFactory {

	public DataElementValidatorSpy elementValidator;
	public boolean factorWasCalled = false;
	public String metadataIdSentToFactory;
	public int numOfInvalidMessages = 0;
	public boolean throwError = false;

	@Override
	public DataElementValidator factor(String elementId) {
		factorWasCalled = true;
		metadataIdSentToFactory = elementId;
		if (throwError) {
			throw DataValidationException.withMessage("Error from validatorFactorySpy");
		}
		elementValidator = new DataElementValidatorSpy();
		elementValidator.numOfInvalidMessages = numOfInvalidMessages;

		return elementValidator;
	}

}
