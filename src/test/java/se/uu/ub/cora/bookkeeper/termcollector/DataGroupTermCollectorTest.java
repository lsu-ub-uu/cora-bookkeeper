/*
 * Copyright 2017, 2018, 2019 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.MetadataStorage;

public class DataGroupTermCollectorTest {
	private DataGroupTermCollectorImp collector;
	private MetadataStorage metadataStorage;
	private CollectedDataCreatorSpy collectedDataCreator;
	private DataGroup basicDataGroup;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForTermStub();
		collectedDataCreator = new CollectedDataCreatorSpy();
		collector = new DataGroupTermCollectorImp(metadataStorage, collectedDataCreator);
		basicDataGroup = createBookWithNoTitle();
	}

	@Test
	public void testGetMetadataStorage() {
		assertSame(collector.getMetadataStorage(), metadataStorage);
	}

	@Test
	public void testCollectedDataCreatorCalledAndDataGroupPassedOn() {
		collector.collectTerms("bookGroup", basicDataGroup);
		assertTrue(collectedDataCreator.createWasCalled);
		assertSame(collectedDataCreator.dataGroup, basicDataGroup);
	}

	@Test
	public void testResultFromTermCollectorNoCollectedTerms() {
		DataGroup collectedData = collector.collectTerms("bookGroup", basicDataGroup);
		assertEquals(collectedData.getNameInData(), "collectedDataFromSpy");
		assertEquals(collectedData.getChildren().size(), 0);
	}

	@Test
	public void testTermCollectorNoCollectedTerms() {
		collector.collectTerms("bookGroup", basicDataGroup);
		assertTrue(collectedDataCreator.collectedTerms.isEmpty());
	}

	@Test
	public void testTermCollectorOneCollectedTermAtomicValue() {
		basicDataGroup.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		collector.collectTerms("bookGroup", basicDataGroup);
		assertEquals(collectedDataCreator.collectedTerms.size(), 1);

		List<DataGroup> collectedIndexTermList = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList.size(), 1);

		assertCorrectCollectedDataTerm(collectedIndexTermList, 0, "titleIndexTerm", "Some title",
				"index");
	}

	@Test
	public void testTermCollectorTwoCollectedTermSameAtomicValue() {
		basicDataGroup.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		collector.collectTerms("bookWithMoreCollectTermsGroup", basicDataGroup);

		assertEquals(collectedDataCreator.collectedTerms.size(), 1);

		List<DataGroup> collectedIndexTermList = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList.size(), 2);

		assertCorrectCollectedDataTerm(collectedIndexTermList, 0, "titleIndexTerm", "Some title",
				"index");
		assertCorrectCollectedDataTerm(collectedIndexTermList, 1, "titleSecondIndexTerm",
				"Some title", "index");
	}

	@Test
	public void testTermCollectorThreeCollectedTermTwoWithSameAtomicValue() {
		basicDataGroup.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		basicDataGroup.addChild(
				DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle", "Some subtitle", "0"));
		basicDataGroup.addChild(DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle",
				"Some other subtitle", "1"));

		collector.collectTerms("bookGroup", basicDataGroup);

		assertEquals(collectedDataCreator.collectedTerms.size(), 1);

		List<DataGroup> collectedIndexTermList = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList.size(), 3);

		assertCorrectCollectedDataTerm(collectedIndexTermList, 0, "titleIndexTerm", "Some title",
				"index");
		assertCorrectCollectedDataTerm(collectedIndexTermList, 1, "subTitleIndexTerm",
				"Some subtitle", "index");
		assertCorrectCollectedDataTerm(collectedIndexTermList, 2, "subTitleIndexTerm",
				"Some other subtitle", "index");
	}

	private void assertCorrectCollectedDataTerm(List<DataGroup> collectedTermList, int index,
			String collectTermId, String collectTermValue, String type) {
		DataGroup collectedDataTerm = collectedTermList.get(index);
		assertEquals(collectedDataTerm.getNameInData(), "collectedDataTerm");
		assertEquals(collectedDataTerm.getChildren().size(), 3);

		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				collectTermId);
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				collectTermValue);

		assertEquals(collectedDataTerm.getAttribute("type"), type);

		assertCollectedExtraDataIsSameAsInCollectTerm(collectedDataTerm, collectTermId);
	}

	private void assertCollectedExtraDataIsSameAsInCollectTerm(DataGroup collectedDataTerm,
			String collectTermId) {
		DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		CollectTermHolder collectTermHolder = collector.getCollectTermHolder();
		assertSame(extraData, collectTermHolder.getCollectTerm(collectTermId)
				.getFirstGroupWithNameInData("extraData"));
	}

	@Test
	public void testTermCollectorOnePermissionAndOneIndexTermAtomicValues() {
		addChildrenToBook(basicDataGroup);

		collector.collectTerms("bookGroup", basicDataGroup);
		assertEquals(collectedDataCreator.collectedTerms.size(), 2);

		List<DataGroup> collectedPermissionTerms = collectedDataCreator.collectedTerms
				.get("permission");
		assertEquals(collectedPermissionTerms.size(), 1);
		assertCorrectCollectedDataTerm(collectedPermissionTerms, 0, "namePermissionTerm",
				"Kalle Kula", "permission");

		List<DataGroup> collectedIndexTerms = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTerms.size(), 3);
		assertCorrectCollectedDataTerm(collectedIndexTerms, 0, "titleIndexTerm", "Some title",
				"index");
		assertCorrectCollectedDataTerm(collectedIndexTerms, 1, "nameIndexTerm", "Kalle Kula",
				"index");
		assertCorrectCollectedDataTerm(collectedIndexTerms, 2, "subTitleIndexTerm", "Some subtitle",
				"index");

	}

	@Test
	public void testTermCollectorOneIndexTermsLink() {
		addLinkToOtherBook(basicDataGroup);

		DataGroup collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		List<DataGroup> collectedIndexTerms = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTerms.size(), 1);

		// assertEquals(collectedData.getNameInData(), "collectedData");
		// assertEquals(collectedData.getFirstAtomicValueWithNameInData("id"), "book1");
		// assertEquals(collectedData.getFirstAtomicValueWithNameInData("type"), "book");
		//
		// assertTrue(collectedData.containsChildWithNameInData("index"));
		// assertFalse(collectedData.containsChildWithNameInData("permission"));
		//
		// DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		// assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);
		//
		// DataGroup collectedDataTerm =
		// indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
		// assertEquals(collectedDataTerm.getRepeatId(), "0");
		// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
		// "otherBookIndexTerm");
		// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
		// "book_someOtherBookId");
		// DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
		// assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeId");
	}

	/**
	 ******************************************************************************************/

	// @Test
	// public void testCollectTermsNoTitle() {
	// DataGroup book = createBookWithNoTitle();
	//
	// DataGroup collectedData = collector.collectTerms("bookGroup", book);
	// assertEquals(collectedData.getNameInData(), "collectedData");
	// assertEquals(collectedData.getFirstAtomicValueWithNameInData("id"), "book1");
	// assertEquals(collectedData.getFirstAtomicValueWithNameInData("type"), "book");
	//
	// assertFalse(collectedData.containsChildWithNameInData("index"));
	// assertFalse(collectedData.containsChildWithNameInData("permission"));
	// }

	@Test
	public void testCollectTermsCalledTwiceReturnsTheSameResult() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);

		DataGroup collectedData2 = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData2.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms2 = collectedData2.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms2.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);
	}

	// @Test
	// public void testCollectTermsTitle() {
	// DataGroup book = createBookWithNoTitle();
	// book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
	//
	// DataGroup collectedData = collector.collectTerms("bookGroup", book);
	// assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);
	//
	// DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
	// assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);
	//
	// DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
	// assertEquals(collectedDataTerm.getRepeatId(), "0");
	// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
	// "Some title");
	// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
	// "titleIndexTerm");
	// DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
	// assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	// }

	// @Test
	// public void testCollectTermsTitleWithMoreIndexTerms() {
	// DataGroup book = createBookWithNoTitle();
	// book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
	//
	// DataGroup collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup", book);
	// assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);
	//
	// DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
	// assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 2);
	//
	// DataGroup collectedDataTerm = indexTerms.getFirstGroupWithNameInData("collectedDataTerm");
	// assertEquals(collectedDataTerm.getRepeatId(), "0");
	// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
	// "Some title");
	// assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
	// "titleIndexTerm");
	// DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
	// assertEquals(extraData.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	//
	// DataGroup collectedDataTerm2 = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
	// .get(1);
	// assertEquals(collectedDataTerm2.getRepeatId(), "1");
	// assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermValue"),
	// "Some title");
	// assertEquals(collectedDataTerm2.getFirstAtomicValueWithNameInData("collectTermId"),
	// "titleSecondIndexTerm");
	// DataGroup extraData2 = collectedDataTerm2.getFirstGroupWithNameInData("extraData");
	// assertEquals(extraData2.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	//
	// }

	// @Test
	// public void testCollectIndexTermsTwoSubTitles() {
	// DataGroup book = createBookWithNoTitle();
	// book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
	// book.addChild(
	// DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle", "Some subtitle", "0"));
	// book.addChild(DataAtomic.withNameInDataAndValueAndRepeatId("bookSubTitle",
	// "Some other subtitle", "1"));
	//
	// DataGroup collectedData = collector.collectTerms("bookGroup", book);
	// DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
	//
	// assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 3);
	//
	// }

	// @Test
	// public void testCollectPermissionAndIndexTermsWithTitleAndPersonName() {
	// DataGroup book = createBookWithNoTitle();
	// addChildrenToBook(book);
	//
	// DataGroup collectedData = collector.collectTerms("bookGroup", book);
	// assertTrue(collectedData.containsChildWithNameInData("permission"));
	//
	// DataGroup permissionTerms = collectedData.getFirstGroupWithNameInData("permission");
	// assertEquals(permissionTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);
	//
	// DataGroup collectedDataTerm = permissionTerms
	// .getAllGroupsWithNameInData("collectedDataTerm").get(0);
	// assertCollectTermHasRepeatIdAndTermValueAndTermId(collectedDataTerm, "0", "Kalle Kula",
	// "namePermissionTerm");
	// DataGroup extraData = collectedDataTerm.getFirstGroupWithNameInData("extraData");
	// assertEquals(extraData.getFirstAtomicValueWithNameInData("permissionKey"),
	// "PERMISSIONFORNAME");
	//
	// DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
	// assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 3);
	//
	// DataGroup collectedDataTerm2 = indexTerms.getAllGroupsWithNameInData("collectedDataTerm")
	// .get(1);
	// assertCollectTermHasRepeatIdAndTermValueAndTermId(collectedDataTerm2, "1", "Kalle Kula",
	// "nameIndexTerm");
	// DataGroup extraData2 = collectedDataTerm2.getFirstGroupWithNameInData("extraData");
	// assertEquals(extraData2.getFirstAtomicValueWithNameInData("indexType"), "indexTypeString");
	// }

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
