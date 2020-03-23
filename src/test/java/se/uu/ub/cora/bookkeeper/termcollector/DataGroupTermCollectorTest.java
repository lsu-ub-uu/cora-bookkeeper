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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataAtomicFactorySpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataGroupFactorySpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.storage.MetadataStorage;

public class DataGroupTermCollectorTest {
	private DataGroupTermCollectorImp collector;
	private MetadataStorage metadataStorage;
	private CollectedDataCreatorSpy collectedDataCreator;
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	private DataGroup basicDataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);

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
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		collector.collectTerms("bookGroup", basicDataGroup);
		assertEquals(collectedDataCreator.collectedTerms.size(), 1);

		List<DataGroup> collectedIndexTermList = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList.size(), 1);

		assertCorrectCollectedDataTerm(collectedIndexTermList, 0, "titleIndexTerm", "Some title",
				"index");
	}

	@Test
	public void testTermCollectorTwoCollectedTermSameAtomicValue() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));
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
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		basicDataGroup.addChild(new DataAtomicSpy("bookSubTitle", "Some subtitle", "0"));
		basicDataGroup.addChild(new DataAtomicSpy("bookSubTitle", "Some other subtitle", "1"));

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

		assertEquals(collectedDataTerm.getAttribute("type").getValue(), type);

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

		collector.collectTerms("bookGroup", basicDataGroup);

		List<DataGroup> collectedIndexTerms = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTerms.size(), 1);

		assertCorrectCollectedDataTerm(collectedIndexTerms, 0, "otherBookIndexTerm",
				"book_someOtherBookId", "index");

	}

	@Test
	public void testTermCollectorTwoIndexTermsSameLink() {
		addLinkToOtherBook(basicDataGroup);

		collector.collectTerms("bookWithMoreCollectTermsGroup", basicDataGroup);

		List<DataGroup> collectedIndexTerms = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTerms.size(), 2);

		assertCorrectCollectedDataTerm(collectedIndexTerms, 0, "otherBookIndexTerm",
				"book_someOtherBookId", "index");
		assertCorrectCollectedDataTerm(collectedIndexTerms, 1, "otherBookSecondIndexTerm",
				"book_someOtherBookId", "index");
	}

	@Test
	public void testCollectTermsCalledTwiceReturnsTheSameResult() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		collector.collectTerms("bookGroup", basicDataGroup);

		List<DataGroup> collectedIndexTermList = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList.size(), 1);

		assertCorrectCollectedDataTerm(collectedIndexTermList, 0, "titleIndexTerm", "Some title",
				"index");

		collector.collectTerms("bookGroup", basicDataGroup);

		List<DataGroup> collectedIndexTermList2 = collectedDataCreator.collectedTerms.get("index");
		assertEquals(collectedIndexTermList2.size(), 1);

		assertCorrectCollectedDataTerm(collectedIndexTermList2, 0, "titleIndexTerm", "Some title",
				"index");
	}

	private DataGroup createBookWithNoTitle() {
		DataGroup book = new DataGroupSpy("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private void addChildrenToBook(DataGroup book) {
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		book.addChild(new DataAtomicSpy("bookSubTitle", "Some subtitle"));
		DataGroup personRole = new DataGroupSpy("personRole");
		personRole.addChild(new DataAtomicSpy("name", "Kalle Kula"));
		personRole.setRepeatId("0");
		book.addChild(personRole);
	}

	private void addLinkToOtherBook(DataGroup book) {
		DataGroup otherBookLink = new DataGroupSpy("otherBook");
		otherBookLink.addChild(new DataAtomicSpy("linkedRecordType", "book"));
		otherBookLink.addChild(new DataAtomicSpy("linkedRecordId", "someOtherBookId"));
		otherBookLink.setRepeatId("0");
		book.addChild(otherBookLink);
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "book1"));
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
	public void testGetCollectedDataCreator() {
		assertSame(collector.getCollectedDataCreator(), collectedDataCreator);
	}
}
