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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MetadataChildReferenceTest {
	private MetadataChildReference metadataChildReference;

	@BeforeMethod
	public void beforeMethod() {
		metadataChildReference = MetadataChildReference.withReferenceIdAndRepeatMinAndRepeatMax(
				"metadataChildReference", 1, MetadataChildReference.UNLIMITED);
	}

	@Test
	public void testInit() {
		assertEquals(metadataChildReference.getReferenceId(), "metadataChildReference",
				"ChildReference should be the value set in the constructor");

		assertEquals(metadataChildReference.getRepeatMin(), 1,
				"RepeatMin should be the value set in the constructor");

		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE,
				"RepeatMax should be the value set in the constructor");

	}

	@Test
	public void testRepeatMinKey() {
		metadataChildReference.setRepeatMinKey("REPEAT_MIN");
		assertEquals(metadataChildReference.getRepeatMinKey(), "REPEAT_MIN");
	}

	@Test
	public void testSecretTrue() {
		metadataChildReference.setSecret(true);
		assertTrue(metadataChildReference.isSecret());
	}

	@Test
	public void testSecretFalse() {
		metadataChildReference.setSecret(false);
		assertFalse(metadataChildReference.isSecret());
	}

	@Test
	public void testSecretKey() {
		metadataChildReference.setSecretKey("SECRET");
		assertEquals(metadataChildReference.getSecretKey(), "SECRET");
	}

	@Test
	public void testReadOnlyTrue() {
		metadataChildReference.setReadOnly(true);
		assertTrue(metadataChildReference.isReadOnly());
	}

	@Test
	public void testReadOnlyFalse() {
		metadataChildReference.setReadOnly(false);
		assertFalse(metadataChildReference.isReadOnly());
	}

	@Test
	public void testReadOnlyKey() {
		metadataChildReference.setReadOnlyKey("READ_ONLY");
		assertEquals(metadataChildReference.getReadOnlyKey(), "READ_ONLY");
	}
}
