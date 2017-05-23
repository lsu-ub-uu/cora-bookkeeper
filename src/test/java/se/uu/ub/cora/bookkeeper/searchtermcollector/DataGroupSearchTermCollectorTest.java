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
package se.uu.ub.cora.bookkeeper.searchtermcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataGroupSearchTermCollectorTest {
	private DataGroupSearchTermCollector collector;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageForSearchTermStub();
		collector = new DataGroupSearchTermCollector(metadataStorage);
	}

	@Test
	public void testCollectSearchTermsNoTitle() {
		DataGroup book = createBookWithNoTitle();

		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertNull(collectedSearchTerms);
	}

	@Test
	public void testCollectSearchTermsWithTitle() {
		DataGroup book = createBookWithNoTitle();
		addChildrenToBook(book);

		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);

		assertEquals(collectedSearchTerms.getNameInData(), "searchData");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("id"), "book1");

		DataGroup searchTerm = collectedSearchTerms.getFirstGroupWithNameInData("searchTerm");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermValue"), "Some title");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermName"), "searchTitle");
		assertEquals(searchTerm.getRepeatId(), "0");
	}

	private void addChildrenToBook(DataGroup book) {
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		book.addChild(DataAtomic.withNameInDataAndValue("bookSubTitle", "Some subtitle"));
		DataGroup personRole = DataGroup.withNameInData("personRole");
		personRole.addChild(DataAtomic.withNameInDataAndValue("name", "someName"));
		personRole.setRepeatId("0");
		book.addChild(personRole);
	}

	private DataGroup createBookWithNoTitle() {
		DataGroup book = DataGroup.withNameInData("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "book1"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "book"));
		DataGroup type = DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"type", "recordType", "book");
		recordInfo.addChild(type);
		DataGroup dataDivider = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("dataDivider",
						"system", "testSystem");
		recordInfo.addChild(dataDivider);
		return recordInfo;
	}

}
