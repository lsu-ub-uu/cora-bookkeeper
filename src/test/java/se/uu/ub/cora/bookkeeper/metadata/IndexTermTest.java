/*
 * Copyright 2024 Uppsala University Library
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

public class IndexTermTest {

	@Test
	public void testIndexTermExtendsCollectTerm() throws Exception {
		assertTrue(CollectTerm.class.isAssignableFrom(IndexTerm.class));

		Field indexFieldName = IndexTerm.class.getDeclaredField("indexFieldName");
		Modifier.isFinal(indexFieldName.getModifiers());
		Field indexType = IndexTerm.class.getDeclaredField("indexType");
		Modifier.isFinal(indexType.getModifiers());
	}

	@Test
	public void testCreateIndexTerm() throws Exception {
		IndexTerm storageTerm = IndexTerm.usingIdAndNameInDataAndIndexFieldNameAndIndexType(
				"someId", "someNameInData", "someIndexFieldName", "someIndexType");

		assertEquals(storageTerm.type, "index");
		assertEquals(storageTerm.id, "someId");
		assertEquals(storageTerm.nameInData, "someNameInData");
		assertEquals(storageTerm.indexFieldName, "someIndexFieldName");
		assertEquals(storageTerm.indexType, "someIndexType");
	}
}
