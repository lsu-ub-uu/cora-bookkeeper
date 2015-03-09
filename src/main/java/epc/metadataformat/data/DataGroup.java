package epc.metadataformat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGroup implements DataElement {

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

	public Object getAttribute(String attributeId) {
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
}
