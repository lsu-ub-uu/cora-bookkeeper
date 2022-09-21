/*
 * Copyright 2017, 2018, 2019, 2022 Uppsala University Library
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
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.collected.IndexTerm;
import se.uu.ub.cora.data.collected.PermissionTerm;
import se.uu.ub.cora.data.collected.StorageTerm;
import se.uu.ub.cora.testspies.data.DataRecordLinkSpy;

public class DataGroupTermCollectorTest {

	private MetadataStorageForTermStub metadataStorage;
	private DataGroupTermCollectorImp collector;
	private DataGroup basicDataGroup;
	private static final String INDEX_FIELD_NAME = "indexFieldNameForId:";

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForTermStub();
		collector = new DataGroupTermCollectorImp(metadataStorage);
		basicDataGroup = createBookWithNoTitle();
	}

	@Test
	public void testGetMetadataStorage() {
		assertSame(collector.onlyForTestGetMetadataStorage(), metadataStorage);
	}

	@Test
	public void testResultFromTermCollectorNoRecordIdAndRecordTypeAndCollectedTerms() {
		DataGroup dataGroupWithoutIdAndType = createBookWithWithoutIdAndType();

		CollectTerms collectedData = collector.collectTerms("bookGroup", dataGroupWithoutIdAndType);

		assertTrue(collectedData.recordType.isEmpty());
		assertTrue(collectedData.recordId.isEmpty());
		assertSizesPSI(collectedData, 0, 0, 0);
	}

	@Test
	public void testResultFromTermCollectorNoRecordIdAndRecordTypePartsAndCollectedTerms() {
		DataGroup dataGroupWithoutIdAndType = createBookWithWithoutIdAndPartsOfType();

		CollectTerms collectedData = collector.collectTerms("bookGroup", dataGroupWithoutIdAndType);

		assertTrue(collectedData.recordType.isEmpty());
		assertTrue(collectedData.recordId.isEmpty());
		assertSizesPSI(collectedData, 0, 0, 0);
	}

	@Test
	public void testResultFromTermCollectorNoRecordInfoAndCollectedTerms() {
		DataGroup dataGroupWithoutIdAndType = new DataGroupOldSpy("book");

		CollectTerms collectedData = collector.collectTerms("bookGroup", dataGroupWithoutIdAndType);

		assertTrue(collectedData.recordType.isEmpty());
		assertTrue(collectedData.recordId.isEmpty());
		assertSizesPSI(collectedData, 0, 0, 0);
	}

	@Test
	public void testResultFromTermCollectorNoCollectedTerms() {
		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		assertEquals(collectedData.recordType.get(), "book");
		assertEquals(collectedData.recordId.get(), "book1");
		assertSizesPSI(collectedData, 0, 0, 0);
	}

	private void assertSizesPSI(CollectTerms collectedData, int permissionSize, int storageSize,
			int indexSize) {
		assertEquals(collectedData.permissionTerms.size(), permissionSize);
		assertEquals(collectedData.storageTerms.size(), storageSize);
		assertEquals(collectedData.indexTerms.size(), indexSize);
	}

	@Test
	public void testIndexTermCollectorOneCollectedTermAtomicValue() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		assertSizesPSI(collectedData, 0, 0, 1);

		IndexTerm indexTerm = collectedData.indexTerms.get(0);

		assertIndexTerm(indexTerm, "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
	}

	private void assertIndexTerm(IndexTerm indexTerm, String id, String value, String indexType,
			String indexFieldName) {
		assertEquals(indexTerm.id(), id);
		assertEquals(indexTerm.value(), value);
		assertEquals(indexTerm.indexType(), indexType);
		assertEquals(indexTerm.indexFieldName(), INDEX_FIELD_NAME + indexFieldName);
	}

	@Test
	public void testIndexTermCollectorTwoCollectedTermSameAtomicValue() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		CollectTerms collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup",
				basicDataGroup);

		assertSizesPSI(collectedData, 0, 0, 2);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
		assertIndexTerm(indexTerms.get(1), "titleSecondIndexTerm", "Some title", "indexTypeString",
				"titleSecondIndexTerm");
	}

	@Test
	public void testIndexTermCollectorThreeCollectedTermTwoWithSameAtomicValue() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		basicDataGroup.addChild(new DataAtomicSpy("bookSubTitle", "Some subtitle", "0"));
		basicDataGroup.addChild(new DataAtomicSpy("bookSubTitle", "Some other subtitle", "1"));

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		assertSizesPSI(collectedData, 0, 0, 3);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
		assertIndexTerm(indexTerms.get(1), "subTitleIndexTerm", "Some subtitle", "indexTypeString",
				"subTitleIndexTerm");
		assertIndexTerm(indexTerms.get(2), "subTitleIndexTerm", "Some other subtitle",
				"indexTypeString", "subTitleIndexTerm");
	}

	@Test
	public void testPermissionTermCollectorOnePermissionAndThreeIndexTermAtomicValues() {
		addChildrenToBook(basicDataGroup);

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		assertSizesPSI(collectedData, 1, 0, 3);

		List<PermissionTerm> permissionTerms = collectedData.permissionTerms;
		assertPermissionTerm(permissionTerms.get(0), "namePermissionTerm", "Kalle Kula",
				"PERMISSIONFORNAME");

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
		assertIndexTerm(indexTerms.get(1), "nameIndexTerm", "Kalle Kula", "indexTypeString",
				"nameIndexTerm");
		assertIndexTerm(indexTerms.get(2), "subTitleIndexTerm", "Some subtitle", "indexTypeString",
				"subTitleIndexTerm");
	}

	private void assertPermissionTerm(PermissionTerm permissionTerm, String id, String value,
			String permissionKey) {
		assertEquals(permissionTerm.id(), id);
		assertEquals(permissionTerm.value(), value);
		assertEquals(permissionTerm.permissionKey(), permissionKey);
	}

	@Test
	public void testTermCollectorOneIndexTermsLink() {
		addLinkToOtherBook(basicDataGroup);

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataGroup);

		assertSizesPSI(collectedData, 0, 0, 1);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "otherBookIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookIndexTerm");
	}

	@Test
	public void testTermCollectorTwoIndexTermsSameLink() {
		addLinkToOtherBook(basicDataGroup);

		CollectTerms collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup",
				basicDataGroup);

		assertSizesPSI(collectedData, 0, 0, 2);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "otherBookIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookIndexTerm");
		assertIndexTerm(indexTerms.get(1), "otherBookSecondIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookSecondIndexTerm");
	}

	@Test
	public void testCollectTermsCalledTwiceReturnsTheSameResult() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		CollectTerms collectedData1 = collector.collectTerms("bookGroup", basicDataGroup);

		List<IndexTerm> indexTerms1 = collectedData1.indexTerms;
		assertIndexTerm(indexTerms1.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");

		CollectTerms collectedData2 = collector.collectTerms("bookGroup", basicDataGroup);

		List<IndexTerm> indexTerms2 = collectedData2.indexTerms;
		assertIndexTerm(indexTerms2.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
	}

	@Test
	public void testStorageTermCollectorTwoCollectedTermSameAtomicValue() {
		basicDataGroup.addChild(new DataAtomicSpy("bookTitle", "Some title"));

		CollectTerms collectedData = collector.collectTerms("bookWithStorageCollectTermsGroup",
				basicDataGroup);

		assertSizesPSI(collectedData, 0, 2, 0);

		List<StorageTerm> storageTerms = collectedData.storageTerms;
		assertStorageTerm(storageTerms.get(0), "titleStorageTerm", "Some title",
				"STORAGEKEY_titleStorageTerm");
		assertStorageTerm(storageTerms.get(1), "titleSecondStorageTerm", "Some title",
				"STORAGEKEY_titleSecondStorageTerm");
	}

	private void assertStorageTerm(StorageTerm storageTerm, String id, String value,
			String storageKey) {
		assertEquals(storageTerm.id(), id);
		assertEquals(storageTerm.value(), value);
		assertEquals(storageTerm.storageKey(), storageKey);
	}

	@Test
	public void testTermCollectorTwoStorageTermsSameLink() {
		addLinkToOtherBook(basicDataGroup);

		CollectTerms collectedData = collector.collectTerms("bookWithStorageCollectTermsGroup",
				basicDataGroup);

		assertSizesPSI(collectedData, 0, 2, 0);

		List<StorageTerm> storageTerms = collectedData.storageTerms;
		assertStorageTerm(storageTerms.get(0), "otherBookStorageTerm", "book_someOtherBookId",
				"STORAGEKEY_otherBookStorageTerm");
		assertStorageTerm(storageTerms.get(1), "otherBookSecondStorageTerm", "book_someOtherBookId",
				"STORAGEKEY_otherBookSecondStorageTerm");
	}

	private DataGroup createBookWithNoTitle() {
		DataGroup book = new DataGroupOldSpy("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private void addChildrenToBook(DataGroup book) {
		book.addChild(new DataAtomicSpy("bookTitle", "Some title"));
		book.addChild(new DataAtomicSpy("bookSubTitle", "Some subtitle"));
		DataGroup personRole = new DataGroupOldSpy("personRole");
		personRole.addChild(new DataAtomicSpy("name", "Kalle Kula"));
		personRole.setRepeatId("0");
		book.addChild(personRole);
	}

	private void addLinkToOtherBook(DataGroup book) {
		DataRecordLinkSpy otherBookLink = new DataRecordLinkSpy();
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> "otherBook");
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordType",
				(Supplier<String>) () -> "book");
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				(Supplier<String>) () -> "someOtherBookId");

		book.addChild(otherBookLink);
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
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

	private DataGroup createBookWithWithoutIdAndType() {
		DataGroup book = new DataGroupOldSpy("book");
		DataGroup recordInfo = createRecordInfoWithOutIdAndType();
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfoWithOutIdAndType() {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		DataGroup dataDivider = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("dataDivider",
						"system", "testSystem");
		recordInfo.addChild(dataDivider);
		return recordInfo;
	}

	private DataGroup createBookWithWithoutIdAndPartsOfType() {
		DataGroup book = new DataGroupOldSpy("book");
		DataGroup recordInfo = createRecordInfoWithOutIdAndPartsOfType();
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfoWithOutIdAndPartsOfType() {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		DataGroup dataDivider = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("dataDivider",
						"system", "testSystem");
		recordInfo.addChild(dataDivider);
		DataGroup type = new DataGroupOldSpy("type");
		recordInfo.addChild(type);
		return recordInfo;
	}

	@Test
	public void testMetadataHolderLoadOnlyOnce() throws Exception {
		collector.collectTerms("bookGroup", basicDataGroup);
		collector.collectTerms("bookGroup", basicDataGroup);
		collector.collectTerms("bookGroup", basicDataGroup);
		metadataStorage.MCR.assertNumberOfCallsToMethod("getMetadataElements", 1);
	}
}
