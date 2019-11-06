package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class CollectedDataCreatorSpy implements CollectedDataCreator {

	public boolean createWasCalled = false;
	public DataGroup dataGroup;
	public Map<String, List<DataGroup>> collectedTerms = new HashMap<>();

	@Override
	public DataGroup createCollectedDataFromCollectedTermsAndRecord(
			Map<String, List<DataGroup>> collectedTerms, DataGroup record) {
		this.collectedTerms = collectedTerms;
		createWasCalled = true;
		dataGroup = record;

		DataGroup collectedData = new DataGroupSpy("collectedDataFromSpy");
		return collectedData;
	}

}
