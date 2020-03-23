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

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupForRecordPartFilterSpy implements DataGroup {

	public boolean removeAllChildrenWasCalled = false;
	public boolean containsChildWithNameInDataWasCalled = false;
	public String childNameInDataToRemove;
	public List<String> childNamesInDataToRemoveAll = new ArrayList<>();
	public List<String> nameInDatasContainsChildWithNameInData = new ArrayList<>();
	public boolean childExists = true;
	public boolean addChildWasCalled = false;
	List<DataElement> titleCollection = new ArrayList<>();
	List<Collection<DataElement>> addedChildrenCollections = new ArrayList<>();
	List<DataElement> otherConstraintCollection = new ArrayList<>();

	public DataGroupForRecordPartFilterSpy(String nameInData) {
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
	public void addChild(DataElement dataElement) {
		// TODO Auto-generated method stub
		addChildWasCalled = true;

	}

	@Override
	public List<DataElement> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataElement getFirstChildWithNameInData(String nameInData) {
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
		// TODO Auto-generated method stub
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
	public void addChildren(Collection<DataElement> dataElements) {
		addedChildrenCollections.add(dataElements);
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInData(String nameInData) {
		if ("title".equals(nameInData)) {
			return titleCollection;
		}
		if ("otherConstraint".equals(nameInData)) {
			return otherConstraintCollection;
		}
		return null;
	}

}
