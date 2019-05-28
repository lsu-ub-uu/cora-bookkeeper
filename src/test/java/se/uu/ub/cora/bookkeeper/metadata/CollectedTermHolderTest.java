/*
 * Copyright 2017, 2019 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataMissingException;
import se.uu.ub.cora.data.DataGroup;

public class CollectedTermHolderTest {
	private CollectedTermHolder collectedTermHolder;
	private DataGroup extraData;
	private CollectedTerm collectedTerm;

	@BeforeMethod
	public void beforeMethod() {
		collectedTermHolder = CollectedTermHolder
				.createCollectedTermHolderWithRecordTypeAndRecordId("book", "myBook");
	}

	private void addOneCollectIndexTermCollectedTermToHolder() {
		String id = "someIndexTerm";
		addOneCollectIndexTermCollectedTermToHolderWithId(id);
	}

	private void addOneCollectIndexTermCollectedTermToHolderWithId(String id) {
		extraData = DataGroup.withNameInData("extraData");
		collectedTerm = CollectedTerm.createCollectedTermWithTypeAndIdAndNameInDataAndExtraData(
				"collectIndexTerm", id, "someTerm", extraData);
		collectedTermHolder.addCollectedTerm(collectedTerm);
	}

	@Test
	public void testInitWithTypeAndId() {
		assertEquals(collectedTermHolder.recordType, "book");
		assertEquals(collectedTermHolder.recordId, "myBook");
	}

	@Test
	public void testHasCollectedTermsWithType() {
		assertTrue(collectedTermHolder.hasNotCollectedTermsWithType("someCollectedTermType"));
	}

	@Test
	public void testAddCollectedTerm() {
		addOneCollectIndexTermCollectedTermToHolder();
		assertFalse(collectedTermHolder.hasNotCollectedTermsWithType("collectIndexTerm"));

	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetCollectedTermsByTypeMissingType() {
		collectedTermHolder.getByCollectedTermType("someCollectedTermType");
	}

	@Test
	public void testGetCollectedTermsByType() {
		addOneCollectIndexTermCollectedTermToHolder();
		assertEquals(collectedTermHolder.getByCollectedTermType("collectIndexTerm").size(), 1);
	}

	@Test
	public void testGetCollectedTermsByTypeWithTwoAddedTypes() {
		addOneCollectIndexTermCollectedTermToHolder();
		addOneCollectPermissionTermCollectedTermToHolder();
		assertEquals(collectedTermHolder.getByCollectedTermType("collectIndexTerm").size(), 1);
		assertEquals(collectedTermHolder.getByCollectedTermType("collectPermissionTerm").size(), 1);
	}

	@Test
	public void testGetCollectedTermsByTypeWithTwoAddedOfOneType() {
		addOneCollectIndexTermCollectedTermToHolder();
		addOneCollectIndexTermCollectedTermToHolderWithId("someOtherIndexTerm");
		assertEquals(collectedTermHolder.getByCollectedTermType("collectIndexTerm").size(), 2);
	}

	private void addOneCollectPermissionTermCollectedTermToHolder() {
		DataGroup extraData = DataGroup.withNameInData("extraData");
		CollectedTerm collectedTerm = CollectedTerm
				.createCollectedTermWithTypeAndIdAndNameInDataAndExtraData("collectPermissionTerm",
						"somePermissionTerm", "someTerm", extraData);
		collectedTermHolder.addCollectedTerm(collectedTerm);
	}

}