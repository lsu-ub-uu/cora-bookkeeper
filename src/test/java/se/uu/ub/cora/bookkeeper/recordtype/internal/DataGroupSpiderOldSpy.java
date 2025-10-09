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
package se.uu.ub.cora.bookkeeper.recordtype.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataGroupSpiderOldSpy implements DataGroup {

	public String nameInData;
	public List<DataChild> children = new ArrayList<>();
	public String repeatId;
	public Set<DataAttribute> attributes = new HashSet<>();
	public List<String> removedNameInDatas = new ArrayList<>();
	public List<String> requestedAtomicValues = new ArrayList<>();
	public List<String> requestedDataGroups = new ArrayList<>();
	public List<DataChild> addedChildren = new ArrayList<>();
	public MethodCallRecorder MCR = new MethodCallRecorder();

	public DataGroupSpiderOldSpy(String nameInData) {
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
		MCR.addCall("nameInData", nameInData);
		requestedAtomicValues.add(nameInData);
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				if (dataElement instanceof DataAtomic) {
					String value = ((DataAtomic) dataElement).getValue();
					MCR.addReturned(value);
					return value;
				}
			}
		}
		throw new DataMissingException("Atomic value not found for childNameInData:" + nameInData);
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		requestedDataGroups.add(childNameInData);
		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())) {
				if (dataElement instanceof DataGroup) {
					DataGroup dataGroup = (DataGroup) dataElement;
					MCR.addReturned(dataGroup);
					return dataGroup;
				}
			}
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	@Override
	public void addChild(DataChild dataElement) {
		MCR.addCall("dataElement", dataElement);
		addedChildren.add(dataElement);
		children.add(dataElement);

	}

	@Override
	public List<DataChild> getChildren() {
		return children;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				MCR.addReturned(true);
				return true;
			}
		}
		MCR.addReturned(false);
		return false;
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;

	}

	@Override
	public void addAttributeByIdWithValue(String id, String value) {
		DataAttributeSpy dataAttributeSpy = new DataAttributeSpy();
		attributes.add(dataAttributeSpy);
		dataAttributeSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> id);
		dataAttributeSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);

	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				return dataElement;
			}
		}
		return null;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		List<DataGroup> matchingDataGroups = new ArrayList<>();
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())
					&& dataElement instanceof DataGroup) {
				matchingDataGroups.add((DataGroup) dataElement);
			}
		}
		return matchingDataGroups;
	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		for (DataAttribute dataAttribute : attributes) {
			if (attributeId.equals(dataAttribute.getNameInData())) {
				return dataAttribute;
			}
		}
		return null;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		List<DataAtomic> matchingDataAtomics = new ArrayList<>();
		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())
					&& dataElement instanceof DataAtomic) {
				matchingDataAtomics.add((DataAtomic) dataElement);
			}
		}
		return matchingDataAtomics;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		removedNameInDatas.add(childNameInData);
		DataChild foundChild = tryToFindChildToRemove(childNameInData);
		if (foundChild != null) {
			children.remove(foundChild);
			MCR.addReturned(true);
			return true;
		}
		MCR.addReturned(false);
		return false;
	}

	private DataChild tryToFindChildToRemove(String childNameInData) {

		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())) {
				return dataElement;
			}
		}
		return null;
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildren(Collection<DataChild> dataElements) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		List<DataChild> matchingDataGroups = new ArrayList<>();
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				matchingDataGroups.add(dataElement);
			}
		}
		return matchingDataGroups;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())
					&& dataElement instanceof DataAtomic) {
				return (DataAtomic) dataElement;
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public <T extends DataChild> List<T> getChildrenOfType(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

}
