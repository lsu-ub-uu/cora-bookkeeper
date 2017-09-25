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

public class DataGroupTermCollectorTest {
	private DataGroupTermCollectorImp collector;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageForTermStub();
		collector = new DataGroupTermCollectorImp(metadataStorage);
	}

	@Test
	public void testCollectTermsNoTitle() {
		DataGroup book = createBookWithNoTitle();

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData.getNameInData(), "collectedData");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("id"), "book1");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("type"), "book");

		assertFalse(collectedData.containsChildWithNameInData("index"));
		assertFalse(collectedData.containsChildWithNameInData("permission"));
	}

	@Test
	public void testCollectTermsTitle() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		DataGroup collectTerm = indexTerms.getFirstGroupWithNameInData("collectTerm");
		assertEquals(collectTerm.getRepeatId(), "0");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Some title");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"titleIndexTerm");
		DataGroup extraData = collectTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	}

	@Test
	public void testCollectIndexTermsTwoSubTitles() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		book.addChild(
				DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle", "Some subtitle", "0"));
		book.addChild(DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle",
				"Some other subtitle", "1"));

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");

		assertEquals(indexTerms.getAllGroupsWithNameInData("collectTerm").size(), 3);

	}

	@Test
	public void testCollectIndexTermsWithTitleAndPersonName() {
		DataGroup book = createBookWithNoTitle();
		addChildrenToBook(book);

		DataGroup collectedData = collector.collectTerms("bookGroup", book);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectTerm").size(), 3);

		DataGroup collectTerm = indexTerms.getAllGroupsWithNameInData("collectTerm").get(1);
		assertEquals(collectTerm.getRepeatId(), "1");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Kalle Kula");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"nameIndexTerm");

		DataGroup extraData = collectTerm.getFirstGroupWithNameInData("extraData");

		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");

	}

	@Test
	public void testCollectIndexTermsWithTitleAndTwoPersonRolesName() {
		DataGroup book = createBookWithNoTitle();
		addChildrenToBook(book);

		DataGroup personRole = DataGroup.withNameInData("personRole");
		personRole.addChild(DataAtomic.withNameInDataAndValue("name", "Arne Anka"));
		personRole.setRepeatId("1");
		book.addChild(personRole);

		DataGroup collectedData = collector.collectTerms("bookGroup", book);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectTerm").size(), 4);

		DataGroup collectTerm = indexTerms.getAllGroupsWithNameInData("collectTerm").get(1);
		assertEquals(collectTerm.getRepeatId(), "1");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Kalle Kula");
		assertEquals(collectTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"nameIndexTerm");

		DataGroup extraData = collectTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");

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
