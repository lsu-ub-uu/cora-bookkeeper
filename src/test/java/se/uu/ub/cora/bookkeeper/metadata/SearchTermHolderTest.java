/*
 * Copyright 2017 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class SearchTermHolderTest {
	@Test
	public void testInit() {
		SearchTermHolder searchTermHolder = new SearchTermHolder();
		DataGroup searchTerm = DataGroup.withNameInData("searchTerm");
		DataGroup recordInfo = createRecordInfoWithIdAndType("titleSearchTerm", "searchTerm");
		searchTerm.addChild(recordInfo);
		searchTerm.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
		DataGroup searchFieldRef = DataGroup.withNameInData("searchFieldRef");
		searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		searchFieldRef.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));

		searchTerm.addChild(searchFieldRef);

		searchTermHolder.addSearchTerm(searchTerm);

		assertEquals(searchTermHolder.getSearchTerm("titleSearchTerm"), searchTerm);

	}

	private DataGroup createRecordInfoWithIdAndType(String id, String typeString) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", typeString));
		recordInfo.addChild(type);
		return recordInfo;
	}
}
