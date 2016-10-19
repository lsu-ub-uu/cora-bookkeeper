/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataGroup implements DataPart, DataElement, Data {

	private final String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElement> children = new ArrayList<>();
	private String repeatId;

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

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public boolean containsChildWithNameInData(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(childNameInData)) {
				return true;
			}
		}
		return false;
	}

	public String getFirstAtomicValueWithNameInData(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataAtomic
					&& dataElement.getNameInData().equals(childNameInData)) {
				return ((DataAtomic) dataElement).getValue();
			}
		}
		throw new DataMissingException(
				"Atomic value not found for childNameInData:" + childNameInData);
	}

	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement instanceof DataGroup
					&& dataElement.getNameInData().equals(childNameInData)) {
				return ((DataGroup) dataElement);
			}
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	public DataElement getFirstChildWithNameInData(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(childNameInData)) {
				return dataElement;
			}
		}
		throw new DataMissingException("Element not found for childNameInData:" + childNameInData);
	}

	public DataGroup extractGroup(String groupId) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(groupId)) {
				return (DataGroup) dataElement;
			}
		}
		throw new DataMissingException("Requested dataGroup " + groupId + " doesn't exist");
	}

	public String extractAtomicValue(String atomicId) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(atomicId)) {
				return ((DataAtomic) dataElement).getValue();
			}
		}
		throw new DataMissingException("Requested dataAtomic " + atomicId + " does not exist");
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	public void removeFirstChildWithNameInData(String childNameInData) {
		boolean childRemoved = tryToRemoveChild(childNameInData);
		if (!childRemoved) {
			throw new DataMissingException(
					"Element not found for childNameInData:" + childNameInData);
		}
	}

	private boolean tryToRemoveChild(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(childNameInData)) {
				getChildren().remove(dataElement);
				return true;
			}
		}
		return false;
	}
}
