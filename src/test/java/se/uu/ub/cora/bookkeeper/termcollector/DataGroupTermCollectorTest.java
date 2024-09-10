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
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.collected.IndexTerm;
import se.uu.ub.cora.data.collected.PermissionTerm;
import se.uu.ub.cora.data.collected.StorageTerm;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class DataGroupTermCollectorTest {

	private MetadataStorageForTermStub metadataStorage;
	private DataGroupTermCollectorImp collector;
	private DataRecordGroupSpy basicDataRecordGroup;
	private static final String INDEX_FIELD_NAME = "indexFieldNameForId:";
	private LoggerFactorySpy loggerFactory;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		setUpProviders();

		metadataStorage = new MetadataStorageForTermStub();

		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorage);
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		collector = new DataGroupTermCollectorImp();
		basicDataRecordGroup = createBookWithNoTitle();
	}

	private void setUpProviders() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
	}

	@Test
	public void testResultFromTermCollectorNoRecordIdAndRecordTypeAndCollectedTerms() {
		DataRecordGroup dataGroupWithoutIdAndType = createRecordWithoutIdAndType();

		CollectTerms collectedData = collector.collectTerms("bookGroup", dataGroupWithoutIdAndType);

		assertTrue(collectedData.recordType.isEmpty());
		assertTrue(collectedData.recordId.isEmpty());
		assertSizesPSI(collectedData, 0, 0, 0);
	}

	private DataRecordGroup createRecordWithoutIdAndType() {
		DataRecordGroupSpy dataRecordGroupSpy = new DataRecordGroupSpy();
		dataRecordGroupSpy.MRV.setAlwaysThrowException("getType",
				new RuntimeException("someSpyError"));
		dataRecordGroupSpy.MRV.setAlwaysThrowException("getId",
				new RuntimeException("someSpyError"));
		return dataRecordGroupSpy;
	}

	@Test
	public void testResultFromTermCollectorNoCollectedTerms() {
		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataRecordGroup);

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
		DataAtomicOldSpy child1 = new DataAtomicOldSpy("bookTitle", "Some title");
		addChildrenToDataRecordGroup(child1);

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 0, 1);

		IndexTerm indexTerm = collectedData.indexTerms.get(0);

		assertIndexTerm(indexTerm, "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
	}

	private void addChildrenToDataRecordGroup(DataChild... children) {
		DataGroupSpy dataGroup = new DataGroupSpy();
		dataFactorySpy.MRV.setDefaultReturnValuesSupplier("factorGroupFromDataRecordGroup",
				() -> dataGroup);
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren", () -> Arrays.asList(children));
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
		DataAtomicOldSpy child1 = new DataAtomicOldSpy("bookTitle", "Some title");
		addChildrenToDataRecordGroup(child1);

		CollectTerms collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup",
				basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 0, 2);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
		assertIndexTerm(indexTerms.get(1), "titleSecondIndexTerm", "Some title", "indexTypeString",
				"titleSecondIndexTerm");
	}

	@Test
	public void testIndexTermCollectorThreeCollectedTermTwoWithSameAtomicValue() {
		DataAtomicOldSpy child1 = new DataAtomicOldSpy("bookTitle", "Some title");
		DataAtomicOldSpy child2 = new DataAtomicOldSpy("bookSubTitle", "Some subtitle", "0");
		DataAtomicOldSpy child3 = new DataAtomicOldSpy("bookSubTitle", "Some other subtitle", "1");
		addChildrenToDataRecordGroup(child1, child2, child3);

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataRecordGroup);

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
		DataAtomicOldSpy child1 = new DataAtomicOldSpy("bookTitle", "Some title");
		DataAtomicOldSpy child2 = new DataAtomicOldSpy("bookSubTitle", "Some subtitle");
		DataGroup personRole = new DataGroupOldSpy("personRole");
		personRole.addChild(new DataAtomicOldSpy("name", "Kalle Kula"));
		personRole.setRepeatId("0");

		addChildrenToDataRecordGroup(child1, child2, personRole);

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataRecordGroup);

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
		addLinkToOtherBook();

		CollectTerms collectedData = collector.collectTerms("bookGroup", basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 0, 1);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "otherBookIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookIndexTerm");
	}

	@Test
	public void testTermCollectorTwoIndexTermsSameLink() {
		addLinkToOtherBook();

		CollectTerms collectedData = collector.collectTerms("bookWithMoreCollectTermsGroup",
				basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 0, 2);

		List<IndexTerm> indexTerms = collectedData.indexTerms;
		assertIndexTerm(indexTerms.get(0), "otherBookIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookIndexTerm");
		assertIndexTerm(indexTerms.get(1), "otherBookSecondIndexTerm", "book_someOtherBookId",
				"indexTypeId", "otherBookSecondIndexTerm");
	}

	@Test
	public void testCollectTermsCalledTwiceReturnsTheSameResult() {
		DataAtomicOldSpy child = new DataAtomicOldSpy("bookTitle", "Some title");
		addChildrenToDataRecordGroup(child);

		CollectTerms collectedData1 = collector.collectTerms("bookGroup", basicDataRecordGroup);

		List<IndexTerm> indexTerms1 = collectedData1.indexTerms;
		assertIndexTerm(indexTerms1.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");

		CollectTerms collectedData2 = collector.collectTerms("bookGroup", basicDataRecordGroup);

		List<IndexTerm> indexTerms2 = collectedData2.indexTerms;
		assertIndexTerm(indexTerms2.get(0), "titleIndexTerm", "Some title", "indexTypeString",
				"titleIndexTerm");
	}

	@Test
	public void testStorageTermCollectorTwoCollectedTermSameAtomicValue() {
		DataAtomicOldSpy child = new DataAtomicOldSpy("bookTitle", "Some title");
		addChildrenToDataRecordGroup(child);

		CollectTerms collectedData = collector.collectTerms("bookWithStorageCollectTermsGroup",
				basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 2, 0);

		Set<StorageTerm> storageTerms = collectedData.storageTerms;
		StorageTerm storageTerm0 = (StorageTerm) storageTerms.toArray()[0];
		StorageTerm storageTerm1 = (StorageTerm) storageTerms.toArray()[1];

		assertStorageTerm(storageTerm0, "titleStorageTerm", "Some title",
				"STORAGEKEY_titleStorageTerm");
		assertStorageTerm(storageTerm1, "titleSecondStorageTerm", "Some title",
				"STORAGEKEY_titleSecondStorageTerm");
	}

	private void assertStorageTerm(StorageTerm storageTerm, String id, String value,
			String storageKey) {
		assertEquals(storageTerm.storageTermId(), id);
		assertEquals(storageTerm.value(), value);
		assertEquals(storageTerm.storageKey(), storageKey);
	}

	@Test
	public void testTermCollectorTwoStorageTermsSameLink() {
		addLinkToOtherBook();

		CollectTerms collectedData = collector.collectTerms("bookWithStorageCollectTermsGroup",
				basicDataRecordGroup);

		assertSizesPSI(collectedData, 0, 2, 0);

		Set<StorageTerm> storageTerms = collectedData.storageTerms;
		StorageTerm storageTerm0 = (StorageTerm) storageTerms.toArray()[0];
		StorageTerm storageTerm1 = (StorageTerm) storageTerms.toArray()[1];
		assertStorageTerm(storageTerm0, "otherBookStorageTerm", "book_someOtherBookId",
				"STORAGEKEY_otherBookStorageTerm");
		assertStorageTerm(storageTerm1, "otherBookSecondStorageTerm", "book_someOtherBookId",
				"STORAGEKEY_otherBookSecondStorageTerm");
	}

	private DataRecordGroupSpy createBookWithNoTitle() {
		DataRecordGroupSpy book = new DataRecordGroupSpy();
		createRecordInfo(book);
		return book;
	}

	private void createRecordInfo(DataRecordGroupSpy book) {
		book.MRV.setDefaultReturnValuesSupplier("getType", () -> "book");
		book.MRV.setDefaultReturnValuesSupplier("getId", () -> "book1");

	}

	private void addLinkToOtherBook() {
		DataRecordLinkSpy otherBookLink = new DataRecordLinkSpy();
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "otherBook");
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordType", () -> "book");
		otherBookLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someOtherBookId");

		addChildrenToDataRecordGroup(otherBookLink);

	}

	@Test
	public void testMetadataHolderLoadOnlyOnce() throws Exception {
		collector.collectTerms("bookGroup", basicDataRecordGroup);
		collector.collectTerms("bookGroup", basicDataRecordGroup);
		collector.collectTerms("bookGroup", basicDataRecordGroup);
		metadataStorage.MCR.assertNumberOfCallsToMethod("getMetadataElements", 1);
	}
}
