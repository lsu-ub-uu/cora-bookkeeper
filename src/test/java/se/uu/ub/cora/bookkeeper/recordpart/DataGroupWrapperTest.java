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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperTest {

	private DataGroupForWrapperSpy dataGroup;
	private DataGroupWrapper wrapper;

	@BeforeMethod
	public void setUp() {
		dataGroup = new DataGroupForWrapperSpy();
		wrapper = new DataGroupWrapper(dataGroup);
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataAndAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("someId2", "someType2");
		boolean returnedValue = wrapper.removeAllChildrenWithNameInDataAndAttributes(
				"someNameInData", dataAttribute, dataAttribute2);

		dataGroup.MCR.assertParameter("removeAllChildrenWithNameInDataAndAttributes", 0,
				"childNameInData", "someNameInData");

		List<?> childAttributes = (List<?>) dataGroup.MCR.getParametersForMethodAndCallNumber(
				"removeAllChildrenWithNameInDataAndAttributes", 0).get("childAttributes");
		assertSame(childAttributes.get(0), dataAttribute);
		assertSame(childAttributes.get(1), dataAttribute2);

		assertSame(dataGroup.sentInAttributes.get(0), dataAttribute);
		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeAllChildrenWithNameInDataAndAttributes", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);

		Map<String, List<List<DataAttribute>>> removedNameInDatas = wrapper.getRemovedNameInDatas();
		List<List<DataAttribute>> list = removedNameInDatas.get("someNameInData");
		assertSame(list.get(0).get(0), dataAttribute);
		assertSame(list.get(0).get(1), dataAttribute2);

		wrapper.removeAllChildrenWithNameInDataAndAttributes("someOtherNameInData");
		assertTrue(removedNameInDatas.get("someOtherNameInData").get(0).isEmpty());

	}

	@Test
	public void testRemoveAllChildrenSameNameDifferentAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("someId2", "someType2");
		wrapper.removeAllChildrenWithNameInDataAndAttributes("someNameInData", dataAttribute,
				dataAttribute2);

		DataAttributeSpy dataAttribute3 = new DataAttributeSpy("someId3", "someType3");
		wrapper.removeAllChildrenWithNameInDataAndAttributes("someNameInData", dataAttribute3);

		Map<String, List<List<DataAttribute>>> removedNameInDatas = wrapper.getRemovedNameInDatas();
		List<List<DataAttribute>> list = removedNameInDatas.get("someNameInData");
		List<DataAttribute> attributesForFirstRemove = list.get(0);
		assertSame(attributesForFirstRemove.get(0), dataAttribute);
		assertSame(attributesForFirstRemove.get(1), dataAttribute2);

		List<DataAttribute> attributesForSecondRemove = list.get(1);
		assertSame(attributesForSecondRemove.get(0), dataAttribute3);

	}

	@Test
	public void testSetRepeatId() {
		wrapper.setRepeatId("someRepeatId");
		dataGroup.MCR.assertParameters("setRepeatId", 0, "someRepeatId");
	}

	@Test
	public void testGetRepeatId() {
		String returnedValue = wrapper.getRepeatId();
		String returnedValueFromDataGroup = (String) dataGroup.MCR.getReturnValue("getRepeatId", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetNameInData() {
		String returnedValue = wrapper.getNameInData();
		String returnedValueFromDataGroup = (String) dataGroup.MCR.getReturnValue("getNameInData",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testHasChildren() {
		boolean returnedValue = wrapper.hasChildren();
		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR.getReturnValue("hasChildren",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testContainsChildWithNameInData() {
		boolean returnedValue = wrapper.containsChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("containsChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testAddChild() {
		DataAtomicSpy dataAtomicChild = new DataAtomicSpy("atomicNameInData", "atomicValue");
		wrapper.addChild(dataAtomicChild);
		dataGroup.MCR.assertParameters("addChild", 0, dataAtomicChild);
	}

	@Test
	public void testAddChildren() {
		DataAtomicSpy dataAtomicChild = new DataAtomicSpy("atomicNameInData", "atomicValue");
		DataAtomicSpy dataAtomicChild2 = new DataAtomicSpy("atomicNameInData2", "atomicValue2");
		List<DataElement> children = Arrays.asList(dataAtomicChild, dataAtomicChild2);

		wrapper.addChildren(children);
		dataGroup.MCR.assertParameters("addChildren", 0, children);
	}

	@Test
	public void testGetChildren() {
		List<DataElement> returnedValue = wrapper.getChildren();
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR.getReturnValue("getChildren",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllChildrenWithNameInData() {
		List<DataElement> returnedValue = wrapper.getAllChildrenWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllChildrenWithNameInData", 0, "someNameInData");

		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllChildrenWithNameInDataAndAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		List<DataElement> returnedValue = wrapper
				.getAllChildrenWithNameInDataAndAttributes("someNameInData", dataAttribute);

		// TODO: for some reason different dataAttribute objects??
		// dataGroup.MCR.assertParameters("getAllChildrenWithNameInDataAndAttributes", 0,
		// "someNameInData", dataAttribute);

		assertSame(dataGroup.sentInAttributes.get(0), dataAttribute);
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInDataAndAttributes", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		DataElement returnedValue = wrapper.getFirstChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstChildWithNameInData", 0, "someNameInData");

		DataElement returnedValueFromDataGroup = (DataElement) dataGroup.MCR
				.getReturnValue("getFirstChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstAtomicValueWithNameInData() {
		String returnedValue = wrapper.getFirstAtomicValueWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "someNameInData");
		String returnedValueFromDataGroup = (String) dataGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		List<DataAtomic> returnedValue = wrapper.getAllDataAtomicsWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllDataAtomicsWithNameInData", 0, "someNameInData");
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllDataAtomicsWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstGroupWithNameInData() {
		DataGroup returnedValue = wrapper.getFirstGroupWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "someNameInData");
		DataGroup returnedValueFromDataGroup = (DataGroup) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		List<DataGroup> returnedValue = wrapper.getAllGroupsWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllGroupsWithNameInData", 0, "someNameInData");
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllGroupsWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllGroupWithNameInDataAndAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		Collection<DataGroup> returnedValue = wrapper
				.getAllGroupsWithNameInDataAndAttributes("someNameInData", dataAttribute);

		// TODO: for some reason different dataAttribute objects??
		// dataGroup.MCR.assertParameters("getAllGroupsWithNameInDataAndAttributes", 0,
		// "someNameInData", dataAttribute);

		assertSame(dataGroup.sentInAttributes.get(0), dataAttribute);
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllGroupsWithNameInDataAndAttributes", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testRemoveFirstChildWithNameInData() {
		boolean returnedValue = wrapper.removeFirstChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("removeFirstChildWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeFirstChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		boolean returnedValue = wrapper.removeAllChildrenWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("removeAllChildrenWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeAllChildrenWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstDataAtomicWithNameInData() {
		DataAtomic returnedValue = wrapper.getFirstDataAtomicWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstDataAtomicWithNameInData", 0, "someNameInData");
		DataAtomic returnedValueFromDataGroup = (DataAtomic) dataGroup.MCR
				.getReturnValue("getFirstDataAtomicWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

}
