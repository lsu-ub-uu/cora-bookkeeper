package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.DataGroup;

public interface CollectedDataCreator {

	DataGroup createCollectedDataFromCollectedTermsAndRecord(Map<String, List<DataGroup>> collectedTerms, DataGroup record);

}
