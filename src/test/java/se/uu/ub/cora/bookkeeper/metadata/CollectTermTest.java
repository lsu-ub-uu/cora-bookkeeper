/*
 * Copyright 2017, 2019, 2024 Uppsala University Library
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

import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

public class CollectTermTest {

	@Test
	public void testCollectTermIsAbstractClass() throws Exception {
		Class<CollectTerm> clazz = CollectTerm.class;

		assertTrue(Modifier.isAbstract(clazz.getModifiers()));
	}

	@Test
	public void testCollectedTerm() {
		CollectTerm collectTerm = new OnlyForTestCollectTerm("someType", "someId");
		assertEquals(collectTerm.type, "someType");
		assertEquals(collectTerm.id, "someId");
	}

	class OnlyForTestCollectTerm extends CollectTerm {
		public OnlyForTestCollectTerm(String type, String id) {
			super(type, id);
		}
	}
}
