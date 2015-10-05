package se.uu.ub.cora.metadataformat.data;

import java.util.HashSet;
import java.util.Set;

public class DataRecord {
	Set<String> keys = new HashSet<>();
	private DataGroup dataGroup;

	public void addKey(String key) {
		keys.add(key);
	}

	public boolean containsKey(String key) {
		return keys.contains(key);
	}

	public void setDataGroup(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public DataGroup getDataGroup() {
		return dataGroup;
	}

	public Set<String> getKeys() {
		return keys;
	}

}
