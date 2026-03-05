/*
 * Copyright 2026 Uppsala University Library
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

import static org.testng.Assert.assertSame;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;

public class DataFilterCreatorTest {
	private DataFactorySpy dataFactorySpy;
	private DataFilterCreator dataFilterCreator;
	private MetadataHolderSpy metadataHolder;
	private MetadataElementSpy metadataElement;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		metadataHolder = new MetadataHolderSpy();
		dataFilterCreator = DataFilterCreatorImp.usingMetadataHolder(metadataHolder);

		metadataElement = new MetadataElementSpy();
		metadataElement.MRV.setDefaultReturnValuesSupplier("getNameInData",
				() -> "metadataElementNameInData");
	}

	@Test
	public void testFilterWithNameInData() {
		metadataElement.MRV.setDefaultReturnValuesSupplier("getAttributeReferences",
				Collections::emptyList);
		DataChildFilter dataChildFilter = dataFilterCreator
				.createDataChildFilterFromMetadata(metadataElement);

		DataChildFilterSpy factoredDataChildFilter = getFactoredChildFilterFromDataProvider();
		assertSame(dataChildFilter, factoredDataChildFilter);
		factoredDataChildFilter.MCR
				.assertMethodNotCalled("addAttributeUsingNameInDataAndPossibleValues");
	}

	private DataChildFilterSpy getFactoredChildFilterFromDataProvider() {
		return (DataChildFilterSpy) dataFactorySpy.MCR.assertCalledParametersReturn(
				"factorDataChildFilterUsingNameInData", "metadataElementNameInData");
	}

	@Test
	public void testFilterWithNameInDataAndAttributesWithFinalValue() {
		addTwoAttributesWithFinalValuesToElement();

		DataChildFilterSpy dataChildFilter = (DataChildFilterSpy) dataFilterCreator
				.createDataChildFilterFromMetadata(metadataElement);

		DataChildFilterSpy factoredDataChildFilter = getFactoredChildFilterFromDataProvider();
		assertSame(dataChildFilter, factoredDataChildFilter);
		factoredDataChildFilter.MCR
				.assertNumberOfCallsToMethod("addAttributeUsingNameInDataAndPossibleValues", 2);
		assertAttributeWithFinalValueAdded(dataChildFilter, "attributeFinal1", 0);
		assertAttributeWithFinalValueAdded(dataChildFilter, "attributeFinal2", 1);
	}

	private void assertAttributeWithFinalValueAdded(DataChildFilterSpy dataChildFilter, String name,
			int callNo) {
		dataChildFilter.MCR.assertParameters("addAttributeUsingNameInDataAndPossibleValues", callNo,
				name);
		dataChildFilter.MCR.assertParameterAsEqual("addAttributeUsingNameInDataAndPossibleValues",
				callNo, "possibleValues", Set.of(name + "_FinalValue"));
	}

	private void addTwoAttributesWithFinalValuesToElement() {
		metadataElement.MRV.setDefaultReturnValuesSupplier("getAttributeReferences",
				() -> List.of("attributeFinal1", "attributeFinal2"));

		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createFinalAttribute("attributeFinal1"), "attributeFinal1");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createFinalAttribute("attributeFinal2"), "attributeFinal2");

	}

	private CollectionVariable createFinalAttribute(String name) {
		CollectionVariableSpy collectionVariableSpy = new CollectionVariableSpy();
		collectionVariableSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		collectionVariableSpy.MRV.setDefaultReturnValuesSupplier("getFinalValue",
				() -> name + "_FinalValue");

		return collectionVariableSpy;
	}

	@Test
	public void testFilterWithNameInDataAndAttributesWithValues() {
		addTwoAttributesWithValuesToElement();

		DataChildFilterSpy dataChildFilter = (DataChildFilterSpy) dataFilterCreator
				.createDataChildFilterFromMetadata(metadataElement);

		DataChildFilterSpy factoredDataChildFilter = getFactoredChildFilterFromDataProvider();
		assertSame(dataChildFilter, factoredDataChildFilter);
		factoredDataChildFilter.MCR
				.assertNumberOfCallsToMethod("addAttributeUsingNameInDataAndPossibleValues", 2);

		assertAttributeWithValueAdded(dataChildFilter, "attribute1", 0);
		assertAttributeWithValueAdded(dataChildFilter, "attribute2", 1);
	}

	private void assertAttributeWithValueAdded(DataChildFilterSpy dataChildFilter, String name,
			int callNo) {
		dataChildFilter.MCR.assertParameters("addAttributeUsingNameInDataAndPossibleValues", callNo,
				name);
		dataChildFilter.MCR.assertParameterAsEqual("addAttributeUsingNameInDataAndPossibleValues",
				callNo, "possibleValues",
				Set.of(name + "_ItemCollection_Item1", name + "_ItemCollection_Item2"));
	}

	private void addTwoAttributesWithValuesToElement() {
		metadataElement.MRV.setDefaultReturnValuesSupplier("getAttributeReferences",
				() -> List.of("attribute1", "attribute2"));

		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createAttribute("attribute1"), "attribute1");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createAttribute("attribute2"), "attribute2");

		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createItemCollection("attribute1_ItemCollection"),
				"attribute1_ItemCollection");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createItemCollection("attribute2_ItemCollection"),
				"attribute2_ItemCollection");

		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createCollectionItem("attribute1_ItemCollection_Item1"),
				"attribute1_ItemCollection_Item1");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createCollectionItem("attribute1_ItemCollection_Item2"),
				"attribute1_ItemCollection_Item2");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createCollectionItem("attribute2_ItemCollection_Item1"),
				"attribute2_ItemCollection_Item1");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> createCollectionItem("attribute2_ItemCollection_Item2"),
				"attribute2_ItemCollection_Item2");
	}

	private CollectionVariable createAttribute(String name) {
		CollectionVariableSpy collectionVariableSpy = new CollectionVariableSpy();
		collectionVariableSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		collectionVariableSpy.MRV.setDefaultReturnValuesSupplier("getFinalValue", () -> null);
		collectionVariableSpy.MRV.setDefaultReturnValuesSupplier("getRefCollectionId",
				() -> name + "_ItemCollection");

		return collectionVariableSpy;
	}

	private ItemCollectionSpy createItemCollection(String name) {
		ItemCollectionSpy itemCollectionSpy = new ItemCollectionSpy();
		itemCollectionSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		itemCollectionSpy.MRV.setDefaultReturnValuesSupplier("getCollectionItemReferences",
				() -> List.of(name + "_Item1", name + "_Item2"));

		return itemCollectionSpy;
	}

	private CollectionItemSpy createCollectionItem(String name) {
		CollectionItemSpy collectionItemSpy = new CollectionItemSpy();
		collectionItemSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		return collectionItemSpy;
	}
}
