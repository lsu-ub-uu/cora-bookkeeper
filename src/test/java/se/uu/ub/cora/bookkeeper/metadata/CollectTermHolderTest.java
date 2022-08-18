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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataGroup;

public class CollectTermHolderTest {
	@Test
	public void testInit() {
		CollectTermHolder collectTermHolder = new CollectTermHolder();
		DataGroup searchTerm = new DataGroupOldSpy("searchTerm");
		DataGroup recordInfo = createRecordInfoWithIdAndType("titleSearchTerm", "searchTerm");
		searchTerm.addChild(recordInfo);
		searchTerm.addChild(new DataAtomicSpy("searchTermType", "final"));
		DataGroup searchFieldRef = new DataGroupOldSpy("searchFieldRef");
		searchFieldRef.addChild(new DataAtomicSpy("linkedRecordType", "metadata"));
		searchFieldRef.addChild(new DataAtomicSpy("linkedRecordId", "searchTitleTextVar"));

		searchTerm.addChild(searchFieldRef);

		collectTermHolder.addCollectTerm(searchTerm);

		assertEquals(collectTermHolder.getCollectTerm("titleSearchTerm"), searchTerm);

	}

	private DataGroup createRecordInfoWithIdAndType(String id, String typeString) {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", id));
		DataGroup type = new DataGroupOldSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", typeString));
		recordInfo.addChild(type);
		return recordInfo;
	}
}
