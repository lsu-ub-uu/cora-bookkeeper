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
import static org.testng.Assert.assertFalse;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataGroupSearchTermCollectorTest {
	private DataGroupSearchTermCollectorImp collector;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageForSearchTermStub();
		collector = new DataGroupSearchTermCollectorImp(metadataStorage);
	}

	@Test
	public void testCollectSearchTermsNoTitle() {
		DataGroup book = createBookWithNoTitle();

		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertFalse(collectedSearchTerms.containsChildWithNameInData("searchTerm"));
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("id"), "book1");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("type"), "book");

		assertEquals(collectedSearchTerms.getAllGroupsWithNameInData("searchTerm").size(), 0);
	}

	@Test
	public void testCollectSearchTermsWithTitleAndPersonName() {
		DataGroup book = createBookWithNoTitle();
		addChildrenToBook(book);

		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);

		assertEquals(collectedSearchTerms.getNameInData(), "recordIndexData");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("id"), "book1");

		assertEquals(collectedSearchTerms.getAllGroupsWithNameInData("searchTerm").size(), 2);

		DataGroup searchTerm = collectedSearchTerms.getFirstGroupWithNameInData("searchTerm");
		assertEquals(searchTerm.getRepeatId(), "0");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermValue"), "Some title");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermId"),
				"titleSearchTerm");

		List<DataAtomic> indexTypes = searchTerm.getAllDataAtomicsWithNameInData("indexType");
		assertEquals(indexTypes.size(), 2);
		assertEquals(indexTypes.get(0).getValue(), "indexTypeString");
		assertEquals(indexTypes.get(1).getValue(), "indexTypeBoolean");

		// searchTerm: nameSearchTerm
		DataGroup searchTerm2 = collectedSearchTerms.getAllGroupsWithNameInData("searchTerm")
				.get(1);
		assertEquals(searchTerm2.getRepeatId(), "1");
		assertEquals(searchTerm2.getFirstAtomicValueWithNameInData("searchTermValue"),
				"Kalle Kula");
		assertEquals(searchTerm2.getFirstAtomicValueWithNameInData("searchTermId"),
				"nameSearchTerm");

		List<DataAtomic> indexTypes2 = searchTerm2.getAllDataAtomicsWithNameInData("indexType");
		assertEquals(indexTypes2.size(), 1);
		assertEquals(indexTypes2.get(0).getValue(), "indexTypeString");

	}

	private void addChildrenToBook(DataGroup book) {
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		book.addChild(DataAtomic.withNameInDataAndValue("bookSubTitle", "Some subtitle"));
		DataGroup personRole = DataGroup.withNameInData("personRole");
		personRole.addChild(DataAtomic.withNameInDataAndValue("name", "Kalle Kula"));
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
