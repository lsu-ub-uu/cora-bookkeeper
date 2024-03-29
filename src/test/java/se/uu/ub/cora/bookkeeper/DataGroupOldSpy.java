/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.bookkeeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;

public class DataGroupOldSpy implements DataGroup {

	public String nameInData;
	public List<DataChild> children = new ArrayList<>();
	public Set<DataAttribute> attributes = new HashSet<>();
	public List<DataGroup> groupsWithNameInData = new ArrayList<>();
	public Map<String, String> atomicValues = new HashMap<>();
	public Map<String, DataGroup> dataGroups = new HashMap<>();
	public Map<String, List<DataGroup>> dataGroupsAsList = new HashMap<>();
	private String repeatId;
	public Map<String, Integer> numOfGetAllGroupsWithNameInDataToReturn = new HashMap<>();

	public DataGroupOldSpy(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		return atomicValues.get(nameInData);
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		if (dataGroups.containsKey(childNameInData)) {
			return dataGroups.get(childNameInData);
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
		// return new DataGroupSpy(childNameInData);
	}

	@Override
	public void addChild(DataChild dataElement) {
		if (dataElement instanceof DataAtomicOldSpy) {
			DataAtomicOldSpy atomicSpyChild = (DataAtomicOldSpy) dataElement;
			atomicValues.put(atomicSpyChild.nameInData, atomicSpyChild.value);

		} else if (dataElement instanceof DataGroup) {
			DataGroup dataGroup = (DataGroup) dataElement;
			String dataGroupNameInData = dataGroup.getNameInData();
			dataGroups.put(dataGroupNameInData, dataGroup);

			if (!dataGroupsAsList.containsKey(dataGroupNameInData)) {
				dataGroupsAsList.put(dataGroupNameInData, new ArrayList<>());
			}
			dataGroupsAsList.get(dataGroupNameInData).add(dataGroup);

		}
		children.add(dataElement);
	}

	@Override
	public List<DataChild> getChildren() {
		return children;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		if (atomicValues.containsKey(nameInData) || dataGroups.containsKey(nameInData)) {
			return true;
		}
		return false;
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;

	}

	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}

	@Override
	public void addAttributeByIdWithValue(String id, String value) {
		attributes.add(new DataAttributeSpy(id, value));
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
		if (dataGroups.containsKey(nameInData)) {
			return dataGroups.get(nameInData);
		} else if (atomicValues.containsKey(nameInData)) {
			return new DataAtomicOldSpy(nameInData, atomicValues.get(nameInData));
		}
		DataGroupOldSpy dataGroupSpy = new DataGroupOldSpy(nameInData);
		if ("refCollection".contentEquals(nameInData)) {
			dataGroupSpy.addChild(new DataAtomicOldSpy("linkedRecordId", "someSpyLinkedRecordId"));
		}
		return dataGroupSpy;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		List<DataGroup> matchingDataGroups = new ArrayList<>();
		if (numOfGetAllGroupsWithNameInDataToReturn.containsKey(nameInData)) {
			for (int i = 0; i < numOfGetAllGroupsWithNameInDataToReturn.get(nameInData); i++) {
				DataGroupOldSpy dataGroupSpy = new DataGroupOldSpy(nameInData);
				possiblyAddChildren(nameInData, dataGroupSpy);
				matchingDataGroups.add(dataGroupSpy);
			}
		} else if (dataGroupsAsList.containsKey(nameInData)) {
			matchingDataGroups.addAll(dataGroupsAsList.get(nameInData));
		} else if (dataGroups.containsKey(nameInData)) {
			matchingDataGroups.add(dataGroups.get(nameInData));
		}
		return matchingDataGroups;
	}

	private void possiblyAddChildren(String nameInData, DataGroupOldSpy dataGroupSpy) {
		if ("childRefCollectTerm".equals(nameInData)) {
			dataGroupSpy
					.addChild(new DataAtomicOldSpy("linkedRecordId", "someLinkedRecordIdFromSpy"));
			dataGroupSpy.addAttributeByIdWithValue("type", "someAttributeTypeFromSpy");
		} else if ("ref".equals(nameInData)) {
			dataGroupSpy
					.addChild(new DataAtomicOldSpy("linkedRecordId", "someLinkedRecordIdFromSpy"));
		}

	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		for (DataAttribute dataAttribute : attributes) {
			if (dataAttribute.getNameInData().equals(attributeId)) {
				return dataAttribute;
			}
		}
		return null;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		DataChild foundElement = tryToFindElementToRemove(childNameInData);
		if (foundElement != null) {
			getChildren().remove(foundElement);
			if (foundElement instanceof DataAtomic) {
				atomicValues.remove(foundElement.getNameInData());
				return true;
			} else if (foundElement instanceof DataGroup) {
				dataGroups.remove(foundElement.getNameInData());
				return true;
			}
		}
		return false;
	}

	private DataChild tryToFindElementToRemove(String childNameInData) {
		for (DataChild dataElement : getChildren()) {
			if (dataElementsNameInDataIs(dataElement, childNameInData)) {
				return dataElement;
			}
		}
		return null;
	}

	private boolean dataElementsNameInDataIs(DataChild dataElement, String childNameInData) {
		return dataElement.getNameInData().equals(childNameInData);
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		return false;
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildren(Collection<DataChild> dataElements) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> T getFirstChildOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> type,
			String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean hasRepeatId() {
		// TODO Auto-generated method stub
		return false;
	}

}
