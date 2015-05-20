package epc.metadataformat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataGroup implements DataElement {

	private final String dataId;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElement> children = new ArrayList<>();

	public static DataGroup withDataId(String dataId) {
		return new DataGroup(dataId);
	}

	private DataGroup(String dataId) {
		this.dataId = dataId;
	}

	@Override
	public String getDataId() {
		return dataId;
	}

	public void addAttributeByIdWithValue(String attributeId, String attributeValue) {
		attributes.put(attributeId, attributeValue);
	}

	public String getAttribute(String attributeId) {
		return attributes.get(attributeId);
	}

	public void addChild(DataElement child) {
		children.add(child);
	}

	public List<DataElement> getChildren() {
		return children;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public boolean containsChildWithDataId(String dataId) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getDataId().equals(dataId)) {
				return true;
			}
		}
		return false;
	}

	public String getFirstAtomicValueWithDataId(String dataId) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataAtomic && dataElement.getDataId().equals(dataId)) {
				return ((DataAtomic) dataElement).getValue();
			}
		}
		throw new DataMissingException("Atomic value not found for dataId:" + dataId);
	}

	public DataGroup getFirstGroupWithDataId(String dataId) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataGroup && dataElement.getDataId().equals(dataId)) {
				return ((DataGroup) dataElement);
			}
		}
		throw new DataMissingException("Group not found for dataId:" + dataId);
	}
}
