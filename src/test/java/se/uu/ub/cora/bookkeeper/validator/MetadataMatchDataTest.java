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

package se.uu.ub.cora.bookkeeper.validator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderImp;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;

public class MetadataMatchDataTest {
	private static final String NAME_IN_DATA = "nameInData";
	private MetadataHolder metadataHolder;
	private MetadataMatchData metadataMatch;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		metadataHolder = new MetadataHolderImp();
		metadataMatch = MetadataMatchDataImp.withMetadataHolder(metadataHolder);
	}

	@Test
	public void testMatchingNameInDataOnGroup() {
		MetadataElement metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"id", NAME_IN_DATA, "textId", "defTextId");
		DataChild dataElement = new DataGroupOldSpy(NAME_IN_DATA);

		assertTrue(dataIsMatching(metadataElement, dataElement));
	}

	private boolean dataIsMatching(MetadataElement metadataElement, DataChild dataElement) {
		return metadataMatch.metadataSpecifiesData(metadataElement, dataElement).dataIsValid();
	}

	@Test
	public void testNoMatchNameInDataOnGroupWrongNameInData() {
		MetadataElement metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"id", NAME_IN_DATA, "textId", "defTextId");
		DataChild dataElement = new DataGroupOldSpy("NOT_nameInData");

		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testMatchingAttributeOnGroup() {
		DataAtomicSpy dataAtomicSpy = new DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue",
				() -> "collectionItem2NameInData");
		dataFactorySpy.MRV.setDefaultReturnValuesSupplier("factorAtomicUsingNameInDataAndValue",
				() -> dataAtomicSpy);

		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("collectionVariableNameInData",
				"collectionItem2NameInData");

		ValidationAnswer metadataSpecifiesData = metadataMatch
				.metadataSpecifiesData(metadataElement, dataElement);
		assertTrue(metadataSpecifiesData.dataIsValid());
	}

	private void addCollectionVariableToMetadataHolder() {
		CollectionItem collectionItem1 = new CollectionItem("collectionItem1Id",
				"collectionItem1NameInData", "collectionItem1TextId", "collectionItem1DefTextId");
		metadataHolder.addMetadataElement(collectionItem1);

		CollectionItem collectionItem2 = new CollectionItem("collectionItem2Id",
				"collectionItem2NameInData", "collectionItem2TextId", "collectionItem2DefTextId");
		metadataHolder.addMetadataElement(collectionItem2);

		CollectionItem collectionItem3 = new CollectionItem("collectionItem3Id",
				"collectionItem3NameInData", "collectionItem3TextId", "collectionItem3DefTextId");
		metadataHolder.addMetadataElement(collectionItem3);

		ItemCollection itemCollection = new ItemCollection("itemCollectionId",
				"itemCollectionNameInData", "itemCollectionTextId", "itemCollectionDefTextId");
		metadataHolder.addMetadataElement(itemCollection);
		itemCollection.addItemReference("collectionItem1Id");
		itemCollection.addItemReference("collectionItem2Id");
		itemCollection.addItemReference("collectionItem3Id");

	}

	private void addCollectionVariableChildWithFinalValue() {
		CollectionVariable collectionVariable = new CollectionVariable("collectionVariableId",
				"collectionVariableNameInData", "collectionVariableTextId",
				"collectionVariableDefTextId", "itemCollectionId");
		metadataHolder.addMetadataElement(collectionVariable);

		CollectionVariable collectionVariableChild = new CollectionVariable(
				"collectionVariableChildId", "collectionVariableNameInData",
				"collectionVariableChildTextId", "collectionVariableChildDefTextId",
				"itemCollectionId");
		metadataHolder.addMetadataElement(collectionVariableChild);
		collectionVariableChild.setRefParentId("collectionVariableId");
		collectionVariableChild.setFinalValue("collectionItem2NameInData");
	}

	@Test
	public void testNoMatchAttributeOnGroupWrongCollectionItem() {
		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("collectionVariableNameInData",
				"collectionItem1NameInData");
		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testNoMatchAttributeOnGroupNoAttributeInData() {
		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testNoMatchAttributeOnGroupAttributeButNotTheOneWeAreLookingForInData() {
		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("NOT_collectionVariableNameInData",
				"collectionItem2NameInData");
		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testNoMatchAttributeOnGroupExtraAttributeInData() {
		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("collectionVariableNameInData",
				"collectionItem2NameInData");
		dataElement.addAttributeByIdWithValue("NOT_collectionVariableNameInData",
				"collectionItem2NameInData");
		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testNoMatchMetadataDoesNotSpecifyAnyAttributeExtraAttributeInData() {
		MetadataElement metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"id", NAME_IN_DATA, "textId", "defTextId");
		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("collectionVariableNameInData",
				"collectionItem2NameInData");

		assertFalse(dataIsMatching(metadataElement, dataElement));
	}

	@Test
	public void testMatchingTwoAttributeOnGroup() {
		DataAtomicSpy dataAtomicSpy = new DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue",
				() -> "collectionItem2NameInData");
		dataFactorySpy.MRV.setSpecificReturnValuesSupplier("factorAtomicUsingNameInDataAndValue",
				() -> dataAtomicSpy, "collectionVariableNameInData", "collectionItem2NameInData");

		DataAtomicSpy dataAtomicSpy2 = new DataAtomicSpy();
		dataAtomicSpy2.MRV.setDefaultReturnValuesSupplier("getValue",
				() -> "collectionItem3NameInData");
		dataFactorySpy.MRV.setSpecificReturnValuesSupplier("factorAtomicUsingNameInDataAndValue",
				() -> dataAtomicSpy2, "collectionVariable3NameInData", "collectionItem3NameInData");

		addCollectionVariableToMetadataHolder();
		addCollectionVariableChildWithFinalValue();
		addCollectionVariableChildWithFinalValue3();

		MetadataGroup metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				NAME_IN_DATA, "textId", "defTextId");
		metadataElement.addAttributeReference("collectionVariableChildId");
		metadataElement.addAttributeReference("collectionVariableChild3Id");

		DataGroup dataElement = new DataGroupOldSpy(NAME_IN_DATA);
		dataElement.addAttributeByIdWithValue("collectionVariableNameInData",
				"collectionItem2NameInData");
		dataElement.addAttributeByIdWithValue("collectionVariable3NameInData",
				"collectionItem3NameInData");
		ValidationAnswer metadataSpecifiesData = metadataMatch
				.metadataSpecifiesData(metadataElement, dataElement);
		dataFactorySpy.MCR.assertNumberOfCallsToMethod("factorAtomicUsingNameInDataAndValue", 2);

		assertTrue(metadataSpecifiesData.dataIsValid());
	}

	private void addCollectionVariableChildWithFinalValue3() {
		CollectionVariable collectionVariable = new CollectionVariable("collectionVariable3Id",
				"collectionVariable3NameInData", "collectionVariableTextId",
				"collectionVariableDefTextId", "itemCollectionId");
		metadataHolder.addMetadataElement(collectionVariable);

		CollectionVariable collectionVariableChild = new CollectionVariable(
				"collectionVariableChild3Id", "collectionVariable3NameInData",
				"collectionVariableChild3TextId", "collectionVariableChild3DefTextId",
				"itemCollectionId");
		metadataHolder.addMetadataElement(collectionVariableChild);
		collectionVariableChild.setRefParentId("collectionVariable3Id");
		collectionVariableChild.setFinalValue("collectionItem3NameInData");
	}

	@Test
	public void testValidateResourceLinkSkipAttributesValidation() throws Exception {
		MetadataElement metadataElement = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"id", NAME_IN_DATA, "textId", "defTextId");

		DataResourceLinkSpy dataResourceLinkSpy = new DataResourceLinkSpy();

		metadataMatch.metadataSpecifiesData(metadataElement, dataResourceLinkSpy);
		dataResourceLinkSpy.MCR.assertMethodNotCalled("getAttributes");
	}
}
