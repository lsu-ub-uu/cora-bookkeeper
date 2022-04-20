/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordpart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupForDataGroupRedactorSpy implements DataGroup {

	public boolean removeAllChildrenWasCalled = false;
	public boolean removeAllChildrenWithAttributeWasCalled = false;
	public boolean containsChildWithNameInDataWasCalled = false;
	public String childNameInDataToRemove;
	public String childNameInDataWithAttributesToRemove;
	public List<String> childNamesInDataToRemoveAll = new ArrayList<>();
	public List<String> childNamesInDataWithAttributesToRemoveAll = new ArrayList<>();
	public List<String> nameInDatasContainsChildWithNameInData = new ArrayList<>();
	public Map<String, String> usedAttributesForRemove = new TreeMap<>();
	public boolean childExists = true;
	public boolean addChildWasCalled = false;
	List<DataChild> titleCollection = new ArrayList<>();
	List<Collection<DataChild>> addedChildrenCollections = new ArrayList<>();
	List<DataChild> otherConstraintCollection = new ArrayList<>();
	public boolean getAllChildrenWithNameInDataAndAttributesWasCalled = false;

	public DataGroupForDataGroupRedactorSpy(String nameInData) {
		titleCollection.add(new DataAtomicSpy("title", "some title"));
		otherConstraintCollection.add(new DataAtomicSpy("otherConstraint", "other"));
	}

	@Override
	public String getRepeatId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRepeatId(String repeatId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAttributeByIdWithValue(String id, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return null;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		containsChildWithNameInDataWasCalled = true;
		nameInDatasContainsChildWithNameInData.add(nameInData);
		return childExists;
	}

	@Override
	public void addChild(DataChild dataElement) {
		// TODO Auto-generated method stub
		addChildWasCalled = true;

	}

	@Override
	public List<DataChild> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {

		return null;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		return false;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		childNameInDataToRemove = childNameInData;
		removeAllChildrenWasCalled = true;
		childNamesInDataToRemoveAll.add(childNameInData);
		return true;

	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildren(Collection<DataChild> dataElements) {
		addedChildrenCollections.add(dataElements);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		if ("title".equals(nameInData)) {
			return titleCollection;
		}
		if ("otherConstraint".equals(nameInData)) {
			return otherConstraintCollection;
		}
		return null;
	}

	@Override
	public boolean hasChildren() {
		return childExists;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		childNameInDataWithAttributesToRemove = childNameInData;
		removeAllChildrenWithAttributeWasCalled = true;
		childNamesInDataWithAttributesToRemoveAll.add(childNameInData);
		for (DataAttribute attribute : childAttributes) {
			usedAttributesForRemove.put(attribute.getNameInData(), attribute.getValue());
		}
		return false;
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		getAllChildrenWithNameInDataAndAttributesWasCalled = true;
		if ("title".equals(nameInData)) {
			return titleCollection;
		}
		if ("otherConstraint".equals(nameInData)) {
			return otherConstraintCollection;
		}
		return null;
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

}
