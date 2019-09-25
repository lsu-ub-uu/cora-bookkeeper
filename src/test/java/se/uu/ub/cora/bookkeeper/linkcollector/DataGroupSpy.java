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
	public Map<String, String> attributes = new HashMap<>();
	public List<DataGroup> groupsWithNameInData = new ArrayList<>();
	public Map<String, String> atomicValues = new HashMap<>();
	public Map<String, DataGroupSpy> dataGroups = new HashMap<>();

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
		return atomicValues.get(nameInData);
		// return "someAtomicValueFromSpyFor" + nameInData;
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		if (dataGroups.containsKey(childNameInData)) {
			return dataGroups.get(childNameInData);
		}
		return new DataGroupSpy(childNameInData);
	}

	@Override
	public void addChild(DataElement dataElement) {
		if (dataElement instanceof DataAtomicSpy) {
			DataAtomicSpy atomicSpyChild = (DataAtomicSpy) dataElement;
			atomicValues.put(atomicSpyChild.nameInData, atomicSpyChild.value);

		} else if (dataElement instanceof DataGroupSpy) {
			DataGroupSpy dataGroup = (DataGroupSpy) dataElement;
			dataGroups.put(dataGroup.nameInData, dataGroup);
		}
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

	@Override
	public DataElement getFirstChildWithNameInData(String nameInData) {
		DataGroupSpy dataGroupSpy = new DataGroupSpy(nameInData);
		if ("refCollection".contentEquals(nameInData)) {
			dataGroupSpy.addChild(new DataAtomicSpy("linkedRecordId", "someSpyLinkedRecordId"));
		}
		return dataGroupSpy;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		return groupsWithNameInData;
	}

}
