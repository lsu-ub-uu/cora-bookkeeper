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

import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupForDataRedactorSpy implements DataGroup {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	private String nameInData;

	public DataGroupForDataRedactorSpy(String nameInData) {
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
		MCR.addCall("nameInData", nameInData);
		boolean returned = true;
		if (nameInData.equals("typeNameInData")) {
			returned = false;
		}
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public void addChild(DataElement dataElement) {
		// TODO Auto-generated method stub

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
		MCR.addCall("childNameInData", childNameInData);
		DataGroupForDataRedactorSpy returned = new DataGroupForDataRedactorSpy(childNameInData);
		MCR.addReturned(returned);
		return returned;
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
		return true;

	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildren(Collection<DataElement> dataElements) {
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		List<DataElement> children = new ArrayList<>();
		DataGroupForDataRedactorSpy childDataGroup = new DataGroupForDataRedactorSpy(nameInData);
		children.add(childDataGroup);
		MCR.addReturned(children);
		return children;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return false;
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

}
