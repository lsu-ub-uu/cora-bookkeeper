package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class CollectedDataCreatorSpy implements CollectedDataCreator {

	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();

	public Map<String, List<DataGroup>> collectedTerms = new HashMap<>();

	@Override
	public DataGroup createCollectedDataFromCollectedTermsAndRecord(
			Map<String, List<DataGroup>> collectedTerms, DataGroup record) {
		MCR.addCall("collectedTerms", collectedTerms, "record", record);
		this.collectedTerms = collectedTerms;

		DataGroup collectedData = new DataGroupSpy("collectedDataFromSpy");
		MCR.addReturned(collectedData);
		return collectedData;
	}

	@Override
	public DataGroup createCollectedDataFromCollectedTermsAndRecordWithoutTypeAndId(
			Map<String, List<DataGroup>> collectedTerms) {
		MCR.addCall("collectedTerms", collectedTerms);
		this.collectedTerms = collectedTerms;

		DataGroup collectedData = new DataGroupSpy("collectedDataFromSpy");

		MCR.addReturned(collectedData);
		return collectedData;
	}

}
