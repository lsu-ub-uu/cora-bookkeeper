package se.uu.ub.cora.bookkeeper.recordtype.internal;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class CollectTermHolderSpy implements CollectTermHolder {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public CollectTermHolderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getCollectTermById", () -> StorageTerm
				.usingIdAndStorageKey("someId", "someStorageKey"));
	}

	@Override
	public CollectTerm getCollectTermById(String collectTermId) {
		return (CollectTerm) MCR.addCallAndReturnFromMRV("collectTermId", collectTermId);
	}

}
