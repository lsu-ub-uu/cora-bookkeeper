/*
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
import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;

public class DataToMetadataElementTestHelper {
	public static DataRecordGroupSpy createDataRecordGroupForMetadata(String id, String nameInData,
			String textId, String defTextId) {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> id);
		addAtomic(dataRecordGroup, "nameInData", nameInData);
		addTexts(dataRecordGroup, textId, defTextId);
		return dataRecordGroup;
	}

	public static void addAtomic(DataRecordGroupSpy dataRecordGroup, String nameInData,
			String value) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, nameInData);
	}

	public static void addLink(DataRecordGroupSpy dataRecordGroup, String nameInData,
			String linkedRecordId) {
		DataRecordLinkSpy linkSpy = new DataRecordLinkSpy();
		linkSpy.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkedRecordId);

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> linkSpy, DataRecordLink.class, nameInData);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, nameInData);
	}

	private static void addTexts(DataRecordGroupSpy dataRecordGroup, String textId,
			String defTextId) {
		addLink(dataRecordGroup, "textId", textId);
		addLink(dataRecordGroup, "defTextId", defTextId);
	}

	public static void addAttributeReferenceIds(DataRecordGroupSpy dataRecordGroup,
			String... attributeReferenceIds) {
		DataGroupSpy attributeReferencesGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "attributeReferences");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> attributeReferencesGroup, "attributeReferences");

		List<DataRecordLink> attributeReferences = new ArrayList<>();
		for (String attributeReferenceId : attributeReferenceIds) {
			DataRecordLinkSpy attributeReference = new DataRecordLinkSpy();
			attributeReference.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
					() -> attributeReferenceId);
			attributeReferences.add(attributeReference);
		}
		attributeReferencesGroup.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> attributeReferences, DataRecordLink.class, "ref");
	}

	public static void addAtomic(DataGroupSpy dataGroup, String nameInData, String value) {
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
		dataGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				nameInData);
	}

	public static void addLink(DataGroupSpy dataGroup, String nameInData, String linkedRecordType,
			String linkedRecordId) {
		DataRecordLinkSpy linkSpy = new DataRecordLinkSpy();
		linkSpy.MRV.setDefaultReturnValuesSupplier("getLinkedRecordType", () -> linkedRecordType);
		linkSpy.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkedRecordId);

		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName", () -> linkSpy,
				DataRecordLink.class, nameInData);
		dataGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				nameInData);
	}

	public static DataGroupSpy createChildReference(String ref, String repeatMin,
			String repeatMax) {
		DataGroupSpy childReferenceGroup = new DataGroupSpy();
		DataToMetadataElementTestHelper.addLink(childReferenceGroup, "ref", "metadata", ref);
		DataToMetadataElementTestHelper.addAtomic(childReferenceGroup, "repeatMin", repeatMin);
		DataToMetadataElementTestHelper.addAtomic(childReferenceGroup, "repeatMax", repeatMax);
		return childReferenceGroup;
	}

	public static void addChildReferencesToDataRecordGroupUsingReference(
			DataRecordGroupSpy dataRecordGroup, DataGroupSpy... childReference) {
		DataGroupSpy childReferencesGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "childReferences");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> childReferencesGroup, "childReferences");

		List<DataGroup> childReferences = new ArrayList<>();
		for (DataGroupSpy reference : childReference) {
			childReferences.add(reference);
		}
		childReferencesGroup.MRV.setDefaultReturnValuesSupplier("getChildren",
				() -> childReferences);
	}

	public static void assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
			MetadataElement collectionItem, String id, String nameInData, String textId,
			String defTextId, List<Object> attributeReferences) {
		assertEquals(collectionItem.getId(), id);
		assertEquals(collectionItem.getNameInData(), nameInData);
		assertEquals(collectionItem.getTextId(), textId);
		assertEquals(collectionItem.getDefTextId(), defTextId);
		assertEquals(collectionItem.getAttributeReferences(), attributeReferences);
	}

}
