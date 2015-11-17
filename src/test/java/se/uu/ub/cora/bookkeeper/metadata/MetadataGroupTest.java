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

package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MetadataGroupTest {
	private MetadataGroup metadataGroup;

	@BeforeMethod
	public void setUp() {
		metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id", "nameInData",
				"textId", "defTextId");

	}

	@Test
	public void testInit() {
		assertEquals(metadataGroup.getId(), "id",
				"Id should have the value set in the constructor");

		assertEquals(metadataGroup.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(metadataGroup.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(metadataGroup.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertNotNull(metadataGroup.getAttributeReferences(),
				"attributeReferences should not be null for a new metadataGroup");

		assertNotNull(metadataGroup.getChildReferences(),
				"childReferences should not be null for a new metadataGroup");
	}

	@Test
	public void testAddRefParentId() {
		metadataGroup.setRefParentId("refParentId");
		assertEquals(metadataGroup.getRefParentId(), "refParentId");
	}

	@Test
	public void testAddAttributeReference() {
		metadataGroup.addAttributeReference("attributeReference");
		assertEquals(metadataGroup.getAttributeReferences().iterator().next(), "attributeReference",
				"AttributeReference should be the same as the one added");
	}

	@Test
	public void testAddChildReference() {
		MetadataChildReference metadataChildReference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("aChildReference", 1,
						MetadataChildReference.UNLIMITED);
		metadataGroup.addChildReference(metadataChildReference);
		assertEquals(metadataGroup.getChildReferences().iterator().next(), metadataChildReference,
				"MetadataChildReference should be the same as the one added.");
	}
}
