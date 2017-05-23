package se.uu.ub.cora.bookkeeper.metadata;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class SearchTermHolder {
	private Map<String, DataGroup> searchTerms = new HashMap<>();

	public void addSearchTerm(DataGroup searchTerm) {
		DataGroup recordInfo = searchTerm.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		searchTerms.put(id, searchTerm);
	}

	public DataGroup getSearchTerm(String id) {
		return searchTerms.get(id);
	}
}
