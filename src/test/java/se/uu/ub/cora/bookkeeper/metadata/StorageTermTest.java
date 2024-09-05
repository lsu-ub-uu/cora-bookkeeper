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

public class StorageTermTest {

	@Test
	public void testStorageTermExtendsCollectTerm() throws Exception {
		Class<CollectTerm> collectTermClass = CollectTerm.class;
		assertTrue(collectTermClass.isAssignableFrom(StorageTerm.class));
		Field storageKey = StorageTerm.class.getDeclaredField("storageKey");
		Modifier.isFinal(storageKey.getModifiers());
	}

	@Test
	public void testCreateStorageTerm() throws Exception {
		StorageTerm storageTerm = StorageTerm.usingIdAndStorageKey("someId",
				"someStorageKey");

		assertEquals(storageTerm.type, "storage");
		assertEquals(storageTerm.id, "someId");
		assertEquals(storageTerm.storageKey, "someStorageKey");
	}
}
