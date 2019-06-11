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
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class RecordLinkTest {
	private RecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
				"id", "nameInData", "textId", "defTextId", "linkedRecordType");
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getId(), "id");
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertEquals(recordLink.getTextId(), "textId");
		assertEquals(recordLink.getDefTextId(), "defTextId");
		assertEquals(recordLink.getLinkedRecordType(), "linkedRecordType");
		assertTrue(recordLink.getAttributeReferences() instanceof ArrayList);

	}

	@Test
	public void testInitWithPath() {
		recordLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));
		assertEquals(recordLink.getId(), "id");
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertEquals(recordLink.getTextId(), "textId");
		assertEquals(recordLink.getDefTextId(), "defTextId");
		assertEquals(recordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(recordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

	@Test
	public void testWithRefParentId() {
		recordLink.setRefParentId("refParentId");
		assertEquals(recordLink.getRefParentId(), "refParentId");
	}

	@Test
	public void testWithFinalValue() {
		recordLink.setFinalValue("finalValue");
		assertEquals(recordLink.getFinalValue(), "finalValue");
	}
	
	@Test
	public void testAddAttributeReference() {
		recordLink.addAttributeReference("attributeReference");
		assertEquals(recordLink.getAttributeReferences().iterator().next(), "attributeReference");
	}
}
