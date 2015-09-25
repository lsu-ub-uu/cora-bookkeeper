package epc.metadataformat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataGroup implements DataElement {

	private final String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElement> children = new ArrayList<>();

	public static DataGroup withNameInData(String nameInData) {
		return new DataGroup(nameInData);
	}

	private DataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getNameInData() {
		return nameInData;
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

	public boolean containsChildWithNameInData(String nameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(nameInData)) {
				return true;
			}
		}
		return false;
	}

	public String getFirstAtomicValueWithNameInData(String nameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataAtomic && dataElement.getNameInData().equals(nameInData)) {
				return ((DataAtomic) dataElement).getValue();
			}
		}
		throw new DataMissingException("Atomic value not found for nameInData:" + nameInData);
	}

	public DataGroup getFirstGroupWithNameInData(String nameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataGroup && dataElement.getNameInData().equals(nameInData)) {
				return ((DataGroup) dataElement);
			}
		}
		throw new DataMissingException("Group not found for nameInData:" + nameInData);
	}
}
