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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapperTest {

	private DataGroupForWrapperSpy dataGroup;
	private DataGroup wrapperAsDG;
	private DataGroupWrapper wrapperAsDGW;

	@BeforeMethod
	public void setUp() {
		dataGroup = new DataGroupForWrapperSpy();
		DataGroupWrapperImp wrapper = new DataGroupWrapperImp(dataGroup);
		wrapperAsDG = wrapper;
		wrapperAsDGW = wrapper;
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWithoutAttributes() {

		String expectedNameInData = "someNameInData";
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("someId2", "someType2");

		boolean returnedValue = wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(
				expectedNameInData, dataAttribute, dataAttribute2);

		dataGroup.MCR.assertParameter("removeAllChildrenWithNameInDataAndAttributes", 0,
				"childNameInData", expectedNameInData);

		List<?> childAttributes = (List<?>) dataGroup.MCR.getParametersForMethodAndCallNumber(
				"removeAllChildrenWithNameInDataAndAttributes", 0).get("childAttributes");
		assertSame(childAttributes.get(0), dataAttribute);
		assertSame(childAttributes.get(1), dataAttribute2);

		assertSame(dataGroup.sentInAttributes.get(0), dataAttribute);
		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeAllChildrenWithNameInDataAndAttributes", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);

		DataGroupSpy expectedChild = new DataGroupSpy(expectedNameInData);
		expectedChild.addAttributeByIdWithValue("someId", "someType");
		expectedChild.addAttributeByIdWithValue("someId2", "someType2");

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(expectedChild));
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWithAttributes() {

		String expectedNameInData = "someOtherNameInData";
		DataGroupSpy expectedChild = new DataGroupSpy(expectedNameInData);

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(expectedNameInData);

		dataGroup.MCR.assertParameter("removeAllChildrenWithNameInDataAndAttributes", 0,
				"childNameInData", expectedNameInData);

		List<?> childAttributes = (List<?>) dataGroup.MCR.getParametersForMethodAndCallNumber(
				"removeAllChildrenWithNameInDataAndAttributes", 0).get("childAttributes");
		assertEquals(childAttributes.size(), 0);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(expectedChild));
	}

	@Test
	public void testRemoveAllChildrenSameNameDifferentAttributes() {
		String nameInData = "someNameInData";
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("someId2", "someType2");
		DataAttributeSpy dataAttribute3 = new DataAttributeSpy("someId3", "someType3");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(nameInData, dataAttribute,
				dataAttribute2);

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(nameInData, dataAttribute3);

		DataGroupSpy expectedChild1 = new DataGroupSpy(nameInData);
		expectedChild1.addAttributeByIdWithValue("someId", "someType");
		expectedChild1.addAttributeByIdWithValue("someId2", "someType2");

		wrapperAsDGW.hasRemovedBeenCalled(expectedChild1);

		DataGroupSpy expectedChild2 = new DataGroupSpy(nameInData);
		expectedChild1.addAttributeByIdWithValue("someId3", "someType3");

		wrapperAsDGW.hasRemovedBeenCalled(expectedChild2);
	}

	@Test
	public void testSetRepeatId() {
		wrapperAsDG.setRepeatId("someRepeatId");
		dataGroup.MCR.assertParameters("setRepeatId", 0, "someRepeatId");
	}

	@Test
	public void testGetRepeatId() {
		String returnedValue = wrapperAsDG.getRepeatId();
		String returnedValueFromDataGroup = (String) dataGroup.MCR.getReturnValue("getRepeatId", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetNameInData() {
		String returnedValue = wrapperAsDG.getNameInData();
		String returnedValueFromDataGroup = (String) dataGroup.MCR.getReturnValue("getNameInData",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testHasChildren() {
		boolean returnedValue = wrapperAsDG.hasChildren();
		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR.getReturnValue("hasChildren",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testContainsChildWithNameInData() {
		boolean returnedValue = wrapperAsDG.containsChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("containsChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testAddChild() {
		DataAtomicSpy dataAtomicChild = new DataAtomicSpy("atomicNameInData", "atomicValue");
		wrapperAsDG.addChild(dataAtomicChild);
		dataGroup.MCR.assertParameters("addChild", 0, dataAtomicChild);
	}

	@Test
	public void testAddChildren() {
		DataAtomicSpy dataAtomicChild = new DataAtomicSpy("atomicNameInData", "atomicValue");
		DataAtomicSpy dataAtomicChild2 = new DataAtomicSpy("atomicNameInData2", "atomicValue2");
		List<DataElement> children = Arrays.asList(dataAtomicChild, dataAtomicChild2);

		wrapperAsDG.addChildren(children);
		dataGroup.MCR.assertParameters("addChildren", 0, children);
	}

	@Test
	public void testGetChildren() {
		List<DataElement> returnedValue = wrapperAsDG.getChildren();
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR.getReturnValue("getChildren",
				0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllChildrenWithNameInData() {
		List<DataElement> returnedValue = wrapperAsDG
				.getAllChildrenWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllChildrenWithNameInData", 0, "someNameInData");

		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllChildrenWithNameInDataAndAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		List<DataElement> returnedValue = wrapperAsDG
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
		DataElement returnedValue = wrapperAsDG.getFirstChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstChildWithNameInData", 0, "someNameInData");

		DataElement returnedValueFromDataGroup = (DataElement) dataGroup.MCR
				.getReturnValue("getFirstChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstAtomicValueWithNameInData() {
		String returnedValue = wrapperAsDG.getFirstAtomicValueWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "someNameInData");
		String returnedValueFromDataGroup = (String) dataGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		List<DataAtomic> returnedValue = wrapperAsDG
				.getAllDataAtomicsWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllDataAtomicsWithNameInData", 0, "someNameInData");
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllDataAtomicsWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstGroupWithNameInData() {
		DataGroup returnedValue = wrapperAsDG.getFirstGroupWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "someNameInData");
		DataGroup returnedValueFromDataGroup = (DataGroup) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		List<DataGroup> returnedValue = wrapperAsDG.getAllGroupsWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getAllGroupsWithNameInData", 0, "someNameInData");
		List<?> returnedValueFromDataGroup = (List<?>) dataGroup.MCR
				.getReturnValue("getAllGroupsWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void getAllGroupWithNameInDataAndAttributes() {
		DataAttributeSpy dataAttribute = new DataAttributeSpy("someId", "someType");
		Collection<DataGroup> returnedValue = wrapperAsDG
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
		boolean returnedValue = wrapperAsDG.removeFirstChildWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("removeFirstChildWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeFirstChildWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		boolean returnedValue = wrapperAsDG.removeAllChildrenWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("removeAllChildrenWithNameInData", 0, "someNameInData");

		boolean returnedValueFromDataGroup = (boolean) dataGroup.MCR
				.getReturnValue("removeAllChildrenWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	@Test
	public void testGetFirstDataAtomicWithNameInData() {
		DataAtomic returnedValue = wrapperAsDG.getFirstDataAtomicWithNameInData("someNameInData");
		dataGroup.MCR.assertParameters("getFirstDataAtomicWithNameInData", 0, "someNameInData");
		DataAtomic returnedValueFromDataGroup = (DataAtomic) dataGroup.MCR
				.getReturnValue("getFirstDataAtomicWithNameInData", 0);
		assertEquals(returnedValue, returnedValueFromDataGroup);
	}

	// -- only name in data
	// other nameInData removed OK
	// test not removed OK
	// right child removed OK

	// -- one attribute
	// not removed
	// other nameInData removed
	// other attribute same name in data remove
	// right child removed

	// more attributes(two)
	// nochild removed
	// other nameInData removed
	// other attribute same name in data remove
	// one attribute not same
	// right child removed

	@Test
	public void testHasRemovedBeenCalledNoRemoveCallAtAll() throws Exception {
		DataGroupSpy child = new DataGroupSpy("someNameInData");
		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledOtherNameInData() throws Exception {
		DataGroupSpy child = new DataGroupSpy("someNameInData");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes("notTheSameNameInData");

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithoutAttributesFound() throws Exception {
		String childNameInData = "childWithoutAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithOneAttributesNotFound() throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId", "attributeValue");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData);

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithOtherAttributeNotFound() throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId", "attributeValue");

		DataAttributeSpy dataAttribute = new DataAttributeSpy("AnotherAttributeId",
				"attributeValue");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute);

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithOtherAttributeValueNotFound()
			throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId", "attributeValue");

		DataAttributeSpy dataAttribute = new DataAttributeSpy("attributeId",
				"anotherAttributeValue");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute);

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithOtherAttributeValueFound()
			throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId", "attributeValue");

		DataAttributeSpy dataAttribute = new DataAttributeSpy("attributeId", "attributeValue");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithSeveralAttributesNotFound()
			throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId_0", "attributeValue_0");
		child.addAttributeByIdWithValue("attributeId_1", "attributeValue_1");

		DataAttributeSpy dataAttribute0 = new DataAttributeSpy("otherAttributeId_0",
				"attributeValue_0");
		DataAttributeSpy dataAttribute1 = new DataAttributeSpy("otherAttributeId_1",
				"attributeValue_1");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0,
				dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes("otherNameInData", dataAttribute0,
				dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0);

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithSeveralAttributesFoundOrderNotImportant1()
			throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId_0", "attributeValue_0");
		child.addAttributeByIdWithValue("attributeId_1", "attributeValue_1");
		// child.addAttributeByIdWithValue("attributeId_2", "attributeValue_2");

		DataAttributeSpy dataAttribute0 = new DataAttributeSpy("attributeId_0", "attributeValue_0");
		DataAttributeSpy dataAttribute1 = new DataAttributeSpy("attributeId_1", "attributeValue_1");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("attributeId_2", "attributeValue_2");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute2);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute2,
				dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0,
				dataAttribute1);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledCheckIsForAllAttributesNotJustFirst() throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId_0", "attributeValue_0");
		child.addAttributeByIdWithValue("attributeId_1", "attributeValue_1");

		DataAttributeSpy dataAttribute0 = new DataAttributeSpy("attributeId_0", "attributeValue_0");
		DataAttributeSpy dataAttribute1 = new DataAttributeSpy("attributeId_1", "attributeValue_1");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute1);

		assertFalse(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledRemovedChildWithSeveralAttributesFoundOrderNotImportant2()
			throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId_1", "attributeValue_1");
		child.addAttributeByIdWithValue("attributeId_2", "attributeValue_2");
		child.addAttributeByIdWithValue("attributeId_0", "attributeValue_0");

		DataAttributeSpy dataAttribute0 = new DataAttributeSpy("attributeId_0", "attributeValue_0");
		DataAttributeSpy dataAttribute1 = new DataAttributeSpy("attributeId_1", "attributeValue_1");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("attributeId_2", "attributeValue_2");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute2);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0,
				dataAttribute1, dataAttribute2);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledWithSeveralRemoveCallsLastOneCorrect() throws Exception {
		String childNameInData = "childWithAttributes";
		DataGroupSpy child = new DataGroupSpy(childNameInData);
		child.addAttributeByIdWithValue("attributeId_0", "attributeValue_0");
		child.addAttributeByIdWithValue("attributeId_1", "attributeValue_1");

		DataAttributeSpy dataAttribute0 = new DataAttributeSpy("attributeId_0", "attributeValue_0");
		DataAttributeSpy dataAttribute1 = new DataAttributeSpy("attributeId_1", "attributeValue_1");
		DataAttributeSpy dataAttribute2 = new DataAttributeSpy("attributeId_2", "attributeValue_2");

		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes("otherNameInData", dataAttribute0,
				dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute2,
				dataAttribute1);
		wrapperAsDG.removeAllChildrenWithNameInDataAndAttributes(childNameInData, dataAttribute0,
				dataAttribute1);

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledForRemoveFirstChild() throws Exception {
		DataGroupSpy child = new DataGroupSpy("someNameInData");

		wrapperAsDG.removeFirstChildWithNameInData("someNameInData");

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

	@Test
	public void testHasRemovedBeenCalledForRemoveAllChildrenWithNameInData() throws Exception {
		DataGroupSpy child = new DataGroupSpy("someNameInData");

		wrapperAsDG.removeAllChildrenWithNameInData("someNameInData");

		assertTrue(wrapperAsDGW.hasRemovedBeenCalled(child));
	}

}
