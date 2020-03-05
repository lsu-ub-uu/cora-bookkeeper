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

import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterTest {

	@Test
	public void testInit() throws Exception {
		DataGroup dataGroup = new DataGroupSpy("someDataGroup");
		dataGroup.addChild(new DataAtomicSpy("someChildId", "someChildValue"));
		List<String> recordPartPermissions = Collections.emptyList();

		RecordPartFilter recordPartFilter = new RecordPartFilterImp();
		recordPartFilter.filterReadRecorPartsUsingPermissions(dataGroup, recordPartPermissions);
		assertTrue(dataGroup.containsChildWithNameInData("someChildId"));
	}
}
