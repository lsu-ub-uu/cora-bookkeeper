/*
 * Copyright 2020 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.recordpart;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class GroupMatcherTest {

	private MetadataMatchDataSpy dataMatcher;
	private GroupMatcher matcher;
	private DataGroupSpy dataGroupSpy;
	private MetadataGroupSpy metadataGroupSpy;

	@BeforeMethod
	public void setUp() {
		dataMatcher = new MetadataMatchDataSpy();
		dataGroupSpy = new DataGroupSpy();
		DataGroupSpy child = new DataGroupSpy();
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("getAllGroupsWithNameInData",
				(Supplier<List<DataGroupSpy>>) () -> List.of(child));

		metadataGroupSpy = new MetadataGroupSpy("recordInfoGroup", "recordInfo");
		matcher = new GroupMatcher(dataMatcher, dataGroupSpy, metadataGroupSpy);
	}

	@Test
	public void testInit() {
		assertEquals(matcher.getMetadataMatchData(), dataMatcher);
	}

	@Test
	public void testHasNotMatchingChild() {
		dataMatcher.isValid = false;
		boolean hasMatchingChild = matcher.groupHasMatchingDataChild();
		assertFalse(hasMatchingChild);
	}

	@Test
	public void testHasMatchingChild() {
		boolean hasMatchingChild = matcher.groupHasMatchingDataChild();
		dataGroupSpy.MCR.assertParameters("getAllGroupsWithNameInData", 0, "recordInfo");
		List<?> returnedChildGroups = (List<?>) dataGroupSpy.MCR
				.getReturnValue("getAllGroupsWithNameInData", 0);

		dataMatcher.MCR.assertParameters("metadataSpecifiesData", 0, metadataGroupSpy,
				returnedChildGroups.get(0));

		assertTrue(hasMatchingChild);
	}

	@Test
	public void testGetMatchingDataChild() {
		DataGroup hasMatchingChild = matcher.getMatchingDataChild();
		dataGroupSpy.MCR.assertParameters("getAllGroupsWithNameInData", 0, "recordInfo");
		List<?> returnedChildGroups = (List<?>) dataGroupSpy.MCR
				.getReturnValue("getAllGroupsWithNameInData", 0);

		dataMatcher.MCR.assertParameters("metadataSpecifiesData", 0, metadataGroupSpy,
				returnedChildGroups.get(0));

		assertSame(hasMatchingChild, returnedChildGroups.get(0));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "No matching child found")
	public void testGetMatchingDataChildNoChildFound() {
		dataMatcher.isValid = false;
		matcher.getMatchingDataChild();
	}

	@Test
	public void testHasMatchingAndThenGetMatchingChildUsesSameFetchedChild() {
		matcher.groupHasMatchingDataChild();
		matcher.getMatchingDataChild();

		dataGroupSpy.MCR.assertNumberOfCallsToMethod("getAllGroupsWithNameInData", 1);

		dataMatcher.MCR.assertNumberOfCallsToMethod("metadataSpecifiesData", 1);

	}

	@Test
	public void testGetMatchingAndThenHasMatchingChildUsesSameFetchedChild() {
		matcher.getMatchingDataChild();
		matcher.groupHasMatchingDataChild();

		dataGroupSpy.MCR.assertNumberOfCallsToMethod("getAllGroupsWithNameInData", 1);

		dataMatcher.MCR.assertNumberOfCallsToMethod("metadataSpecifiesData", 1);

	}
}
