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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataGroupForWrapperSpy implements DataGroup {
	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public List<DataAttribute> sentInAttributes = new ArrayList<>();

	@Override
	public void setRepeatId(String repeatId) {
		MCR.addCall("repeatId", repeatId);
	}

	@Override
	public String getRepeatId() {
		MCR.addCall();
		String returned = "someRepeatIdFromSpy";
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public String getNameInData() {
		MCR.addCall();
		String returned = "someNameInDataFromSpy";
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public boolean hasChildren() {
		MCR.addCall();
		boolean returned = false;
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		boolean returned = false;
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public void addChild(DataElement dataElement) {
		MCR.addCall("dataElement", dataElement);
	}

	@Override
	public void addChildren(Collection<DataElement> dataElements) {
		MCR.addCall("dataElements", dataElements);
	}

	@Override
	public List<DataElement> getChildren() {
		MCR.addCall();
		List<DataElement> returned = Arrays.asList(new DataAtomicSpy("spyNameInData", "spyValue"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		List<DataElement> returned = Arrays.asList(new DataAtomicSpy("spyNameInData", "spyValue"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		for (DataAttribute dataAttribute : childAttributes) {
			sentInAttributes.add(dataAttribute);
		}
		MCR.addCall("nameInData", nameInData, "childAttributes", childAttributes);
		List<DataElement> returned = Arrays.asList(new DataAtomicSpy("spyNameInData", "spyValue"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public DataElement getFirstChildWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		DataElement returned = new DataAtomicSpy("spyNameInData", "spyValue");
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		String returned = "spyValue";
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		List<DataAtomic> returned = Arrays.asList(new DataAtomicSpy("spyNameInData", "spyValue"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		DataGroup returned = new DataGroupSpy("spyNameInData");
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		List<DataGroup> returned = Arrays.asList(new DataGroupSpy("spyNameInData"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		for (DataAttribute dataAttribute : childAttributes) {
			sentInAttributes.add(dataAttribute);
		}
		MCR.addCall("childNameInData", childNameInData, "childAttributes", childAttributes);
		List<DataGroup> returned = Arrays.asList(new DataGroupSpy("spyNameInData"));
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		boolean returned = true;
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		boolean returned = true;
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		MCR.addCall("childNameInData", childNameInData, "childAttributes",
				Arrays.asList(childAttributes));

		for (DataAttribute dataAttribute : childAttributes) {
			sentInAttributes.add(dataAttribute);
		}

		boolean returned = true;
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		DataAtomic returned = new DataAtomicSpy("spyNameInData", "spyValue");
		MCR.addReturned(returned);
		return returned;
	}

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		MCR.addCall("nameInData", nameInData, "value", value);

	}

	@Override
	public boolean hasAttributes() {
		MCR.addCall();
		boolean hasAttributes = false;
		MCR.addReturned(hasAttributes);
		return hasAttributes;
	}

	@Override
	public DataAttribute getAttribute(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		DataAttributeSpy dataAttributeSpy = new DataAttributeSpy(nameInData, nameInData);
		MCR.addReturned(dataAttributeSpy);
		return dataAttributeSpy;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		MCR.addCall();
		List<DataAttribute> emptyList = Collections.emptyList();
		MCR.addReturned(emptyList);
		return emptyList;
	}

}
