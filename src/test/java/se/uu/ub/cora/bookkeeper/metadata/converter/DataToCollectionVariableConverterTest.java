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

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToCollectionVariableConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToCollectionVariableConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addCollectionVariableSpecificsToDataRecordGroup();

		converter = DataToCollectionVariableConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addCollectionVariableSpecificsToDataRecordGroup() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refCollection", "refCollection");
	}

	@Test
	public void testToMetadata() {
		CollectionVariable collectionVariable = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						collectionVariable, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());
		assertEquals(collectionVariable.getRefCollectionId(), "refCollection");

		assertEquals(collectionVariable.getFinalValue(), null);
		assertEquals(collectionVariable.getRefParentId(), null);
	}

	@Test
	public void testToMetadataWithRefParentId() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refParentId", "refParentId");
		CollectionVariable collectionVariable = converter.toMetadata();

		assertEquals(collectionVariable.getRefParentId(), "refParentId");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "finalValue", "finalValue");

		CollectionVariable collectionVariable = converter.toMetadata();

		assertEquals(collectionVariable.getFinalValue(), "finalValue");
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		CollectionVariable collectionVariable = converter.toMetadata();

		assertEquals(collectionVariable.getAttributeReferences().size(), 2);
		assertEquals(collectionVariable.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(collectionVariable.getAttributeReferences().get(1), "someOtherCollectionVar");
	}
}
