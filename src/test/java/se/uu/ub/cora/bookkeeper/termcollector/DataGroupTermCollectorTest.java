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

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataAtomicFactorySpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataGroupFactorySpy;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.storage.MetadataStorage;

public class DataGroupTermCollectorTest {
	private DataGroupTermCollectorImp collector;
	private MetadataStorage metadataStorage;
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForTermStub();
		collector = new DataGroupTermCollectorImp(metadataStorage);
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

	@Test
	public void testGetMetadataStorage() {
		assertSame(collector.getMetadataStorage(), metadataStorage);
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

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 1);
		assertEquals(namesOfGroupsFactored.get(0), "collectedData");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertEquals(dataAtomicFactory.usedValues.size(), 2);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "type", "book");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "id", "book1");
	}

	@Test
	public void testCollectTermsCalledTwiceReturnsTheSameResult() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		// setNumOfChildRefCollectTermsToReturnFromSpy(book, 1);

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms = collectedData.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);

		DataGroup collectedData2 = collector.collectTerms("bookGroup", book);
		assertEquals(collectedData2.getAllGroupsWithNameInData("index").size(), 1);

		DataGroup indexTerms2 = collectedData2.getFirstGroupWithNameInData("index");
		assertEquals(indexTerms2.getAllGroupsWithNameInData("collectedDataTerm").size(), 1);
	}

	@Test
	public void testCollectTermsTitle() {
		DataGroupSpy book = (DataGroupSpy) createBookWithNoTitle();
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));

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
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		DataGroup collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup", book);
		assertEquals(collectedData.getAllGroupsWithNameInData("index").size(), 1);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 4);
		assertEquals(namesOfGroupsFactored.get(0), "collectedDataTerm");

		assertEquals(namesOfGroupsFactored.get(1), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(2), "collectedData");
		assertEquals(namesOfGroupsFactored.get(3), "index");

		DataGroup indexGroup = collectedData.getFirstGroupWithNameInData("index");

		List<DataGroup> collectedDataTerms = indexGroup
				.getAllGroupsWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerms.get(0).getRepeatId(), "0");
		assertEquals(collectedDataTerms.get(1).getRepeatId(), "1");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 6);
		assertEquals(dataAtomicFactory.usedValues.size(), 6);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "collectTermId", "titleIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "collectTermValue", "Some title");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "collectTermId",
				"titleSecondIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "collectTermValue", "Some title");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "type", "book");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "id", "book1");

	}

	@Test
	public void testCollectIndexTermsTwoSubTitles() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		book.addChild(new DataAtomicSpy("bookSubTitle", "Some subtitle", "0"));
		book.addChild(new DataAtomicSpy("bookSubTitle", "Some other subtitle", "1"));

		DataGroup collectedData = collector.collectTerms("bookGroup", book);
		DataGroup indexGroup = collectedData.getFirstGroupWithNameInData("index");

		List<DataGroup> collectedDataTerms = indexGroup
				.getAllGroupsWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerms.get(0).getRepeatId(), "0");
		assertEquals(collectedDataTerms.get(1).getRepeatId(), "1");
		assertEquals(collectedDataTerms.get(2).getRepeatId(), "2");

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 5);
		assertEquals(namesOfGroupsFactored.get(0), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(1), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(2), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(3), "collectedData");
		assertEquals(namesOfGroupsFactored.get(4), "index");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 8);
		assertEquals(dataAtomicFactory.usedValues.size(), 8);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "collectTermId", "titleIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "collectTermValue", "Some title");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "collectTermId",
				"subTitleIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "collectTermValue", "Some subtitle");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "collectTermId",
				"subTitleIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "collectTermValue",
				"Some other subtitle");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(6, "type", "book");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(7, "id", "book1");

	}

	private void assertCorrectAtomicDataUsingIndexNameInDataAndValue(int index, String nameInData,
			String value) {
		List<String> namesOfAtomicDataFactored = dataAtomicFactory.usedNameInDatas;
		List<String> valuesOfAtomicDataFactored = dataAtomicFactory.usedValues;
		assertEquals(namesOfAtomicDataFactored.get(index), nameInData);
		assertEquals(valuesOfAtomicDataFactored.get(index), value);

	}

	@Test
	public void testCollectPermissionAndIndexTermsWithTitleAndPersonName() {
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

		DataGroup personRole = new DataGroupSpy("personRole");
		personRole.addChild(new DataAtomicSpy("name", "Arne Anka"));
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

		DataGroup indexGroup = collectedData.getFirstGroupWithNameInData("index");

		List<DataGroup> collectedDataTerms = indexGroup
				.getAllGroupsWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerms.get(0).getRepeatId(), "0");
		assertTrue(collectedDataTerms.get(0).containsChildWithNameInData("extraData"));
		assertEquals(collectedDataTerms.get(1).getRepeatId(), "1");
		assertTrue(collectedDataTerms.get(1).containsChildWithNameInData("extraData"));

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 4);
		assertEquals(namesOfGroupsFactored.get(0), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(1), "collectedDataTerm");
		assertEquals(namesOfGroupsFactored.get(2), "collectedData");
		assertEquals(namesOfGroupsFactored.get(3), "index");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 6);
		assertEquals(dataAtomicFactory.usedValues.size(), 6);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "collectTermId",
				"otherBookIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "collectTermValue",
				"book_someOtherBookId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "collectTermId",
				"otherBookSecondIndexTerm");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "collectTermValue",
				"book_someOtherBookId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "type", "book");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "id", "book1");
	}

}
