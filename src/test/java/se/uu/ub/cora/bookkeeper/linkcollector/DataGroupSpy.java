package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupSpy implements DataGroup {

	public String nameInData;
	public List<DataElement> children = new ArrayList<>();
	private Map<String, String> attributes = new HashMap<>();

	public DataGroupSpy(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getRepeatId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		return "someAtomicValueFromSpyFor" + nameInData;
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		return new DataGroupSpy(childNameInData);
	}

	@Override
	public void addChild(DataElement dataElement) {
		children.add(dataElement);
	}

	@Override
	public List<DataElement> getChildren() {
		return children;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRepeatId(String repeatId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAttributeByIdWithValue(String id, String value) {
		attributes.put(id, value);
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

}
