/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2025 Olov McKie
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public class DataToItemCollectionConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToItemCollectionConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");

		converter = DataToItemCollectionConverter.fromDataRecordGroup(dataRecordGroup);
	}

	@Test
	public void testToMetadata() {
		addCollectionItemReferenceIds("choice1", "choice2", "choice3");

		ItemCollection itemCollection = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						itemCollection, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());

		Iterator<String> iterator = itemCollection.getCollectionItemReferences().iterator();
		assertEquals(iterator.next(), "choice1");
		assertEquals(iterator.next(), "choice2");
		assertEquals(iterator.next(), "choice3");
	}

	private void addCollectionItemReferenceIds(String... attributeReferenceIds) {
		DataGroupSpy collectionItemReferencesGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "collectionItemReferences");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> collectionItemReferencesGroup, "collectionItemReferences");

		List<DataRecordLink> collectionItemReferences = new ArrayList<>();
		for (String attributeReferenceId : attributeReferenceIds) {
			DataRecordLinkSpy attributeReference = new DataRecordLinkSpy();
			attributeReference.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
					() -> attributeReferenceId);
			collectionItemReferences.add(attributeReference);
		}
		collectionItemReferencesGroup.MRV.setSpecificReturnValuesSupplier(
				"getChildrenOfTypeAndName", () -> collectionItemReferences, DataRecordLink.class,
				"ref");
	}
}
