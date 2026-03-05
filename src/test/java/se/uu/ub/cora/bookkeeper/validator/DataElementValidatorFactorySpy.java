package se.uu.ub.cora.bookkeeper.validator;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataElementValidatorFactorySpy implements DataElementValidatorFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataElementValidatorFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factor", DataElementValidatorSpy::new);
	}

	@Override
	public DataElementValidator factor(String elementId) {
		return (DataElementValidator) MCR.addCallAndReturnFromMRV("elementId", elementId);
	}
}
