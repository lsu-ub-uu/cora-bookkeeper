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
package se.uu.ub.cora.bookkeeper.termcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);

		DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerm.getRepeatId(), "0");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Some title");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"titleIndexTerm");
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	}

	@Test
	public void testCollectTermsTitleWithMoreIndexTerms() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));

		DataGroup collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup", book);
		assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 2);

		DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerm.getRepeatId(), "0");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Some title");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"titleIndexTerm");
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");

		DataGroup collectedDataTerm2 = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
				.get(1);
		assertEquals(collectedDataTerm2.getRepeatId(), "1");
		assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Some title");
		assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermId"),
				"titleSecondIndexTerm");
		DataGroup extraData2 = collectedDataTerm2.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData2.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");

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

		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 3);

	}

	@Test
	public void testCollectIndexTermsWithTitleAndPersonName() {
		DataGroup book = createBookWithNoTitle();
		addChildrenToBook(book);

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertTrue(collectedData.containsChildWithNameInData("permission"));

		DataGroup permissionTerms = collectedData.getFirstGroupWithNameInData("permission");
		assertEquals(permissionTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);

		DataGroup collectedDataTerm = permissionTerms
				.getAllGroupsWithNameInData("collectedDataTerm").get(0);
		assertCollectTermHasRepeatIdAndTermValueAndTermId(collectedDataTerm, "0", "Kalle Kula",
				"namePermissionTerm");
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("permissionKey"),
				"PERMISSIONFORNAME");

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 3);

		DataGroup collectedDataTerm2 = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
				.get(1);
		assertCollectTermHasRepeatIdAndTermValueAndTermId(collectedDataTerm2, "1", "Kalle Kula",
				"nameIndexTerm");
		DataGroup extraData2 = collectedDataTerm2.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData2.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	}

	private void assertCollectTermHasRepeatIdAndTermValueAndTermId(DataGroup collectedDataTerm,
			String repeatId, String termValue, String termId) {
		assertEquals(collectedDataTerm.getRepeatId(), repeatId);
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				termValue);
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"), termId);
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

		assertTrue(collectedData.containsChildWithNameInData("permission"));

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 4);

		DataGroup collectedDataTerm = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
				.get(1);
		assertEquals(collectedDataTerm.getRepeatId(), "1");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Kalle Kula");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"nameIndexTerm");

		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");

	}

	private DataGroup createBookWithNoTitle() {
		DataGroup book = DataGroup.withNameInData("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private void addChildrenToBook(DataGroup book) {
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		book.addChild(DataAtomic.withNameInDataAndValue("bookSubTitle", "Some subtitle"));
		DataGroup personRole = DataGroup.withNameInData("personRole");
		personRole.addChild(DataAtomic.withNameInDataAndValue("name", "Kalle Kula"));
		personRole.setRepeatId("0");
		book.addChild(personRole);
	}

	private void addLinkToOtherBook(DataGroup book) {
		DataGroup otherBookLink = DataGroup.withNameInData("otherBook");
		otherBookLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "book"));
		otherBookLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "someOtherBookId"));
		otherBookLink.setRepeatId("0");
		book.addChild(otherBookLink);
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

	@Test
	public void testCollectTermsOneLinkToOtherBook() {
		DataGroup book = createBookWithNoTitle();
		addLinkToOtherBook(book);

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData.getNameInData(), "collectedData");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("id"), "book1");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("type"), "book");

		assertTrue(collectedData.containsChildWithNameInData("index"));
		assertFalse(collectedData.containsChildWithNameInData("permission"));

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);

		DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerm.getRepeatId(), "0");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"otherBookIndexTerm");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"book_someOtherBookId");
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeId");
	}

	@Test
	public void testCollectTermsOneLinkToOtherBookWithMoreIndexTerms() {
		DataGroup book = createBookWithNoTitle();
		addLinkToOtherBook(book);

		DataGroup collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup", book);
		assertEquals(collectedData.getNameInData(), "collectedData");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("id"), "book1");
		assertEquals(collectedData.getFirstAtomicValueWithNameInData("type"), "book");

		assertTrue(collectedData.containsChildWithNameInData("index"));
		assertFalse(collectedData.containsChildWithNameInData("permission"));

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 2);

		DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerm.getRepeatId(), "0");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"otherBookIndexTerm");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"book_someOtherBookId");
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeId");

		DataGroup collectedDataTerm2 = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
				.get(1);
		assertEquals(collectedDataTerm2.getRepeatId(), "1");
		assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermId"),
				"otherBookSecondIndexTerm");
		assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermValue"),
				"book_someOtherBookId");
		DataGroup extraData2 = collectedDataTerm2.getFirstGroupWithNameInData("extraData");
		assertEquals(extraData2.getFirstAtomicValueWithNameInData("indexType"), "indexTypeId");
	}

}
