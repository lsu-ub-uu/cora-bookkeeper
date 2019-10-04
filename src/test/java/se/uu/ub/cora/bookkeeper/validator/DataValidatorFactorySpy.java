package se.uu.ub.cora.bookkeeper.validator;

public class DataValidatorFactorySpy implements DataValidatorFactory {

	public DataElementValidator elementValidator;
	public boolean factorWasCalled = false;
	public String metadataIdSentToFactory;

	@Override
	public DataElementValidator factor(String elementId) {
		factorWasCalled = true;
		metadataIdSentToFactory = elementId;
		elementValidator = new DataElementValidatorSpy();
		return elementValidator;
	}

}
