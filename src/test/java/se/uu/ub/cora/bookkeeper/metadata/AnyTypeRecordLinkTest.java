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

package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AnyTypeRecordLinkTest {
	private AnyTypeRecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = AnyTypeRecordLink.withIdAndNameInDataAndTextIdAndDefTextId("id", "nameInData",
				"textId", "defTextId");
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getId(), "id");
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertEquals(recordLink.getTextId(), "textId");
		assertEquals(recordLink.getDefTextId(), "defTextId");
		assertTrue(recordLink.getAttributeReferences() instanceof ArrayList);

	}

	@Test
	public void testAddAttributeReference() {
		recordLink.addAttributeReference("attributeReference");
		assertEquals(recordLink.getAttributeReferences().iterator().next(), "attributeReference");
	}
}
