/*
 * Copyright 2015, 2020 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MetadataChildReferenceTest {
	private MetadataChildReference metadataChildReference;

	@BeforeMethod
	public void beforeMethod() {
		metadataChildReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadataGroup",
						"metadataChildReference", 1, MetadataChildReference.UNLIMITED);
	}

	@Test
	public void testInit() {
		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getLinkedRecordId(), "metadataChildReference");
		assertEquals(metadataChildReference.getRepeatMin(), 1);
		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE);

	}

	@Test
	public void testCollectIndexTerms() {
		CollectTerm collectTerm = CollectTerm.createCollectTermWithTypeAndId("index",
				"someIndexTerm");
		metadataChildReference.addCollectIndexTerm(collectTerm);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "someIndexTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "index");
	}

	@Test
	public void testCollectPermissionTerm() {
		CollectTerm collectTerm = CollectTerm.createCollectTermWithTypeAndId("permission",
				"somePermissionTerm");
		metadataChildReference.addCollectIndexTerm(collectTerm);
		assertEquals(metadataChildReference.getCollectTerms().get(0).id, "somePermissionTerm");
		assertEquals(metadataChildReference.getCollectTerms().get(0).type, "permission");
	}

	@Test
	public void testWithRecordPartConstraint() {
		metadataChildReference.setRecordPartConstraint(ConstraintType.READ_WRITE);
		assertEquals(metadataChildReference.getRecordPartConstraint(), ConstraintType.READ_WRITE);
	}
}
