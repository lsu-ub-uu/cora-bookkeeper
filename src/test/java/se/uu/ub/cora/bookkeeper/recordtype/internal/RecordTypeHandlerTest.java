/*
 * Copyright 2016, 2019, 2020, 2021, 2022 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.recordtype.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.StorageReadResult;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerTest {
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String RECORD_TYPE = "recordType";
	private static final String SOME_ID = "someId";
	private static final String SOME_RECORD_TYPE_ID = "someRecordTypeId";
	private DataFactorySpy dataFactorySpy;
	private static final String ABSTRACT = "abstract";
	private RecordTypeHandler recordTypeHandler;
	private RecordStorageSpy recordStorage;
	private RecordTypeHandlerFactorySpy recordTypeHandlerFactory;
	private RecordTypeHandlerStorageSpy storageSpy;
	private MetadataStorageViewSpy metadataStorageViewSpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		recordStorage = new RecordStorageSpy();
		recordTypeHandlerFactory = new RecordTypeHandlerFactorySpy();
		metadataStorageViewSpy = new MetadataStorageViewSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType("someTypeToValidate", "someCreateDefinitionId",
						"someUpdateDefinitionId")));

	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "ValidationType with id: someValidationTypeId, does not exist.")
	public void testInitWithValidationTypeNotFoundThrowsValidationException() throws Exception {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.empty());

		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);
	}

	private void setUpRecordTypeHandlerWithMetadataStorage(
			MetadataStorageViewSpy metadataStorageViewSpy) {
		recordTypeHandler = RecordTypeHandlerImp
				.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
						recordTypeHandlerFactory, recordStorage, metadataStorageViewSpy,
						"someValidationTypeId");
	}

	@Test
	public void testInitWithValidationTypeFoundReadsRecordTypeFromStorage() throws Exception {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		recordStorage.MCR.assertMethodWasCalled("read");
		List<String> listOfTypes = (List<String>) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("read", 0, "types");
		assertEquals(listOfTypes.size(), 1);
		assertEquals(listOfTypes.get(0), "recordType");
		recordStorage.MCR.assertParameter("read", 0, "id", "someTypeToValidate");

	}

	// @Test
	// public void testInitializeFromStorage() throws Exception {
	// RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndRecordTypeId(null, recordStorage, SOME_ID);
	//
	// List<?> types = (List<?>) recordStorage.MCR
	// .getValueForMethodNameAndCallNumberAndParameterName("read", 0, "types");
	// assertEquals(types.get(0), RECORD_TYPE);
	// recordStorage.MCR.assertParameter("read", 0, "id", SOME_ID);
	// assertEquals(recordTypeHandler.getRecordTypeId(), SOME_ID);
	// }
	//
	// @Test
	// public void testInitializeFromDataGroup() throws Exception {
	// DataGroupSpy dataGroup = createTopDataGroup();
	//
	// RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// recordStorage.MCR.assertMethodNotCalled("read");
	// assertLinkFetchedFromEnteredData(dataGroup, recordTypeHandler);
	// }

	private DataGroupSpy createTopDataGroup() {
		return createTopDataGroupWithId("");
	}

	private DataGroupSpy createTopDataGroupWithId(String id) {
		DataGroupSpy dataGroup = new DataGroupSpy();
		DataGroupSpy recordInfoGroup = new DataGroupSpy();
		recordInfoGroup.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> id);
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				(Supplier<DataGroupSpy>) () -> recordInfoGroup, "recordInfo");
		return dataGroup;
	}

	@Test
	public void testAbstract() {
		setupForStorageAtomicValue(ABSTRACT, "true");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroupMCR = getRecordTypeDataGroupReadFromStorage();
		assertAbstract(dataGroupMCR, true);
	}

	private void setUpHandlerUsingTypeId(String recordTypeId) {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType(recordTypeId, "someCreateDefinitionId",
						"someUpdateDefinitionId")));
		recordTypeHandler = RecordTypeHandlerImp
				.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
						recordTypeHandlerFactory, recordStorage, metadataStorageViewSpy,
						"someValidationTypeId");
	}

	private void assertAbstract(DataGroupSpy dataGroupMCR, boolean expected) {
		assertEquals(recordTypeHandler.isAbstract(), expected);
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, ABSTRACT);
	}

	private DataGroupSpy getRecordTypeDataGroupReadFromStorage() {
		DataGroupSpy dataGroup = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);
		return dataGroup;
	}

	private void setupForStorageAtomicValue(String nameInData, String value) {
		DataGroupSpy DataGroupSpy = new DataGroupSpy();
		DataGroupSpy.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(value),
				nameInData);
		// recordStorage.dataGroupForRead = DataGroupSpy;
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> DataGroupSpy);
	}

	private DataGroupSpy setupDataGroupWithAtomicValue(String nameInData, String value) {
		DataGroupSpy topDataGroup = createTopDataGroup();
		topDataGroup.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(value),
				nameInData);
		return topDataGroup;
	}

	// @Test
	// public void testAbstractFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue(ABSTRACT, "true");
	//
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertAbstract(dataGroup, true);
	// }

	@Test
	public void testAbstractIsFalse() {
		setupForStorageAtomicValue(ABSTRACT, "false");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertAbstract(dataGroup, false);
	}

	// @Test
	// public void testAbstractIsFalseFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue(ABSTRACT, "false");
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertAbstract(dataGroup, false);
	// }

	@Test
	public void testShouldAutoGenerateId() {
		setupForStorageAtomicValue("userSuppliedId", "false");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertShouldAutoGenerateId(dataGroup, true);
	}

	// @Test
	// public void testShouldAutoGenerateIdFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("userSuppliedId", "false");
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertShouldAutoGenerateId(dataGroup, true);
	// }

	private void assertShouldAutoGenerateId(DataGroupSpy dataGroupMCR, boolean expected) {
		assertEquals(recordTypeHandler.shouldAutoGenerateId(), expected);
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "userSuppliedId");
	}

	@Test
	public void testShouldAutoGenerateIdFalse() {
		setupForStorageAtomicValue("userSuppliedId", "true");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertShouldAutoGenerateId(dataGroup, false);
	}

	// @Test
	// public void testShouldAutoGenerateIdFalseFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("userSuppliedId", "true");
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertShouldAutoGenerateId(dataGroup, false);
	// }

	@Test
	public void testCreateDefinitionId() {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		String definitionId = recordTypeHandler.getCreateDefinitionId();

		assertEquals(definitionId, "someCreateDefinitionId");
	}

	@Test
	public void testGetUpdateDefinitionId() throws Exception {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		String definitionId = recordTypeHandler.getUpdateDefinitionId();

		assertEquals(definitionId, "someUpdateDefinitionId");
	}

	@Test
	public void testGetDefinitionId() {
		setupForLinkForStorageWithNameInDataAndRecordId("metadataId", "someMetadataId");
		setUpHandlerUsingTypeId("someRecordId");

		String metadataId = recordTypeHandler.getDefinitionId();

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertUsedLink(dataGroup, "metadataId", metadataId);
	}

	// @Test
	// public void testGetMetadataIdFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId("metadataId",
	// "someMetadataId");
	//
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// String metadataId = recordTypeHandler.getDefinitionId();
	//
	// assertUsedLinkFromDataGroup(dataGroup, "metadataId", metadataId);
	// }

	private void assertUsedLink(DataGroupSpy topGroup, String linkNameInData,
			String returnedLinkedRecordId) {
		int callNumber = 0;
		assertUsedLinkCallNo(topGroup, linkNameInData, returnedLinkedRecordId, callNumber);
	}

	private void assertUsedLinkCallNo(DataGroupSpy topGroup, String linkNameInData,
			String returnedLinkedRecordId, int callNumber) {
		topGroup.MCR.assertParameters("getFirstChildWithNameInData", callNumber, linkNameInData);

		DataRecordLinkSpy linkGroup = (DataRecordLinkSpy) topGroup.MCR
				.getReturnValue("getFirstChildWithNameInData", callNumber);
		String linkedRecordIdFromSpy = (String) linkGroup.MCR.getReturnValue("getLinkedRecordId",
				0);
		assertEquals(returnedLinkedRecordId, linkedRecordIdFromSpy);
	}

	private void setupForLinkForStorageWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		DataGroupSpy dataGroup = setupForLinkWithNameInDataAndRecordId(linkNameInData,
				linkedRecordId);
		// recordStorage.dataGroupForRead = dataGroup;
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
	}

	private DataGroupSpy setupForLinkWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		// DataGroupSpy dataGroup = new DataGroupSpy();
		DataGroupSpy dataGroup = createTopDataGroupWithId("someRecordType");
		DataRecordLinkSpy link = createLink(linkedRecordId);
		dataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(true), linkNameInData);
		dataGroup.MRV.setReturnValues("getFirstChildWithNameInData", List.of(link), linkNameInData);
		return dataGroup;
	}

	private DataRecordLinkSpy createLink(String value) {
		DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
		recordLink.MRV.setReturnValues("getLinkedRecordId", List.of(value));
		return recordLink;
	}

	private DataGroupSpy setupDataGroupWithLinkUsingNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		DataGroupSpy topDataGroup = createTopDataGroup();
		DataRecordLinkSpy link = createLink(linkedRecordId);
		topDataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(true),
				linkNameInData);
		topDataGroup.MRV.setReturnValues("getFirstChildWithNameInData", List.of(link),
				linkNameInData);
		return topDataGroup;
	}

	@Test
	public void testPublic() {
		setupForStorageAtomicValue("public", "true");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertIsPublicForRead(dataGroup, true);
	}

	private void assertIsPublicForRead(DataGroupSpy dataGroupMCR, boolean expected) {
		assertEquals(recordTypeHandler.isPublicForRead(), expected);
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "public");
	}

	// @Test
	// public void testPublicFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("public", "true");
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertIsPublicForRead(dataGroup, true);
	// }

	@Test
	public void testPublicFalse() {
		setupForStorageAtomicValue("public", "false");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertIsPublicForRead(dataGroup, false);
	}

	// @Test
	// public void testPublicFalseFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("public", "false");
	// recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// assertIsPublicForRead(dataGroup, false);
	// }

	@Test
	public void testGetMetadataGroup() {
		RecordTypeHandler recordTypeHandler = setUpRecordTypeWithMetadataIdForStorage();
		DataGroup metadataGroup = recordTypeHandler.getMetadataGroup();

		List<?> types = (List<?>) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("read", 0, "types");
		assertEquals(types.get(0), RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", SOME_RECORD_TYPE_ID);
		types = (List<?>) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("read", 1, "types");
		assertEquals(types.get(0), "metadataGroup");
		recordStorage.MCR.assertParameter("read", 1, "id", "someMetadataId");

		DataGroupSpy returnValue = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 1);
		assertSame(metadataGroup, returnValue);
	}

	private RecordTypeHandler setUpRecordTypeWithMetadataIdForStorage() {
		setupForLinkForStorageWithNameInDataAndRecordId("metadataId", "someMetadataId");
		setUpHandlerUsingTypeId(SOME_RECORD_TYPE_ID);
		return recordTypeHandler;
	}

	@Test
	public void testGetMetadataGroupTwiceReturnsSameInstance() {
		RecordTypeHandler recordTypeHandler = setUpRecordTypeWithMetadataIdForStorage();

		DataGroup metadataGroup = recordTypeHandler.getMetadataGroup();
		assertEquals(recordStorage.MCR.getNumberOfCallsToMethod("read"), 2);

		DataGroup metadataGroup2 = recordTypeHandler.getMetadataGroup();
		assertEquals(recordStorage.MCR.getNumberOfCallsToMethod("read"), 2);
		assertSame(metadataGroup, metadataGroup2);
	}

	// @Test
	// public void testGetMetadataGroupFromDataGroup() {
	// DataGroupSpy dataGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId("metadataId",
	// "someMetadataId");
	//
	// RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	//
	// DataGroup metadataGroup = recordTypeHandler.getMetadataGroup();
	//
	// List<?> type = (List<?>) recordStorage.MCR
	// .getValueForMethodNameAndCallNumberAndParameterName("read", 0, "types");
	// assertEquals(type.get(0), "metadataGroup");
	// recordStorage.MCR.assertParameter("read", 0, "id", "someMetadataId");
	// DataGroupSpy returnValue = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);
	// assertSame(metadataGroup, returnValue);
	//
	// DataGroup metadataGroup2 = recordTypeHandler.getMetadataGroup();
	// assertSame(metadataGroup, metadataGroup2);
	// }

	@Test
	public void testGetCombinedIdsUsingRecordIdNoParent() {
		DataGroupSpy dataGroup = createTopDataGroupWithId("someRecordType");
		dataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(false), "parentId");
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
		setUpHandlerUsingTypeId("someRecordType");

		List<String> ids = recordTypeHandler.getCombinedIdsUsingRecordId(SOME_RECORD_TYPE_ID);

		assertEquals(ids.size(), 1);
		assertEquals(ids.get(0), "someRecordType_someRecordTypeId");
	}

	@Test
	public void testGetCombinedIdsUsingRecordIdWithParent() {
		setupForLinkForStorageWithNameInDataAndRecordId("parentId", "parentRecordType");

		setUpHandlerUsingTypeId("someRecordType");
		List<String> ids = recordTypeHandler.getCombinedIdsUsingRecordId(SOME_RECORD_TYPE_ID);
		assertEquals(ids.size(), 2);
		assertEquals(ids.get(0), "someRecordType_someRecordTypeId");
		assertEquals(ids.get(1), "fakeCombinedIdFromRecordTypeHandlerSpy");
	}

	// @Test
	// public void testGetCombinedIdsUsingRecordIdFromDataGroupNoParent() {
	// DataGroupSpy dataGroup = createTopDataGroupWithId("someCoolId");
	// dataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(false), "parentId");
	// // recordStorage.dataGroupForRead = dataGroup;
	// recordStorage.MRV.setDefaultReturnValuesSupplier("read",
	// (Supplier<DataGroup>) () -> dataGroup);
	//
	// RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
	// .usingRecordStorageAndDataGroup(recordTypeHandlerFactory, recordStorage, dataGroup);
	// List<String> ids = recordTypeHandler.getCombinedIdsUsingRecordId(SOME_RECORD_TYPE_ID);
	// assertEquals(ids.size(), 1);
	// assertEquals(ids.get(0), "someCoolId_someRecordTypeId");
	// }

	@Test
	public void testGetCombinedIdsUsingRecordIdFromDataGroupSeveralParentAncestors()
			throws Exception {
		DataGroupSpy dataRecordGroup = createDataRecordGroupWithIdAndParentId(
				"implementingRecordType", "parentRecordType");

		setRecordTypeHandlerFactoryToReturnHandlerWithCombinedId("parentRecordType_someRecordId",
				"someRecordId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataRecordGroup, "parentRecordType");

		List<String> ids = recordTypeHandler.getCombinedIdsUsingRecordId("someRecordId");

		assertEquals(ids.get(0), "implementingRecordType_someRecordId");
		assertEquals(ids.get(1), "parentRecordType_someRecordId");
		assertEquals(ids.size(), 2);

		List<?> types = (List<?>) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("read", 0, "types");
		assertEquals(types.get(0), RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", "parentRecordType");

		var parentDataGroup = recordStorage.MCR.getReturnValue("read", 0);
		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0, parentDataGroup);
		RecordTypeHandlerSpy parentRecordTypeHandler = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 0);
		parentRecordTypeHandler.MCR.assertParameters("getCombinedIdsUsingRecordId", 0,
				"someRecordId");
		List<?> parentCombinedIds = (List<?>) parentRecordTypeHandler.MCR
				.getReturnValue("getCombinedIdsUsingRecordId", 0);
		assertEquals(parentCombinedIds.get(0), ids.get(1));
	}

	private void setRecordTypeHandlerFactoryToReturnHandlerWithCombinedId(String combinedId,
			String recordId) {
		List<RecordTypeHandlerSpy> list = new ArrayList<>();
		RecordTypeHandlerSpy parentRecordTypeHandler = createTypeHandlerSpy("parentRecordType",
				"grandpaRecordType", true);
		parentRecordTypeHandler.MRV.setReturnValues("getCombinedIdsUsingRecordId",
				List.of(List.of(combinedId)), recordId);
		list.add(parentRecordTypeHandler);

		Iterator<RecordTypeHandlerSpy> iterator = list.iterator();
		recordTypeHandlerFactory.MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup",
				(Supplier<RecordTypeHandlerSpy>) () -> iterator.next());
	}

	private DataGroupSpy createDataRecordGroupWithIdAndParentId(String id, String parentId) {
		DataGroupSpy topDataGroup = createTopDataGroupWithId(id);
		topDataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(true), "parentId");

		DataRecordLinkSpy linkToParent = new DataRecordLinkSpy();
		linkToParent.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				(Supplier<String>) () -> parentId);

		topDataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildWithNameInData",
				(Supplier<DataRecordLinkSpy>) () -> linkToParent, "parentId");
		return topDataGroup;
	}

	@Test
	public void testGetRecordPartReadConstraintsNOReadConstraint() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 3);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");

		assertTrue(recordPartReadConstraints.isEmpty());
		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertTrue(recordPartWriteConstraints.isEmpty());

		Set<Constraint> recordPartCreateWriteConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertTrue(recordPartCreateWriteConstraints.isEmpty());
	}

	private RecordTypeHandlerStorageSpy setUpHandlerWithStorageSpyUsingTypeId(String recordTypeId) {
		storageSpy = new RecordTypeHandlerStorageSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional
						.of(new ValidationType(recordTypeId, recordTypeId + "New", recordTypeId)));
		recordTypeHandler = RecordTypeHandlerImp
				.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
						recordTypeHandlerFactory, storageSpy, metadataStorageViewSpy,
						"someValidationTypeId");
		return storageSpy;
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraint() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 1);

		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "organisationRoot",
				0);

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 1);

		assertConstraintExistWithNumberOfAttributes(recordPartWriteConstraints, "organisationRoot",
				0);

		Set<Constraint> recordPartWriteCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartWriteCreateConstraints.size(), 1);

		assertConstraintExistWithNumberOfAttributes(recordPartWriteCreateConstraints,
				"organisationRoot2", 0);
	}

	private void assertConstraintExistWithNumberOfAttributes(
			Set<Constraint> recordPartReadConstraints, String constraintName, int numOfAttributes) {
		Constraint organisationReadConstraint = getConstraintByNameInData(recordPartReadConstraints,
				constraintName);
		// assertEquals(organisationReadConstraint.getDataAttributes().size(), numOfAttributes);
		DataChildFilterSpy childFilter = (DataChildFilterSpy) organisationReadConstraint
				.getDataChildFilter();
		childFilter.MCR.assertNumberOfCallsToMethod("addAttributeUsingNameInDataAndPossibleValues",
				numOfAttributes);
	}

	private Constraint getConstraintByNameInData(Set<Constraint> constraints, String nameInData) {
		for (Constraint constraint : constraints) {
			if (constraint.getNameInData().equals(nameInData)) {
				return constraint;
			}
		}
		return null;
	}

	// HERE
	@Test
	public void testGetRecordPartReadConstraintsTwoReadConstraintsOneWithAttributes() {
		// - Two read constraints organisationRoot, organisationAlternativeName
		// organisationAlternativeName constraint exists dependning on
		// storageSpy.numberOfChildrenWithReadWriteConstraint
		// - OrganisationRoot has no attributes
		// - OrganisationAlternativeName has 0 to 2 attributes depending on
		// storageSpy.numberOfAttributes

		storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");
		storageSpy.attributesIdsToAddToConstraint.add("textPartLangCollectionVar");

		Set<Constraint> constraints = recordTypeHandler.getReadRecordPartConstraints();

		assertEquals(constraints.size(), 2);

		assertThatTheFirstConstraintIsCorrectWithoutAttributes();
		assertTheSecondConstraintIsCorrectWithTwoAttributes();
	}

	private void assertThatTheFirstConstraintIsCorrectWithoutAttributes() {
		dataFactorySpy.MCR.assertNumberOfCallsToMethod("factorDataChildFilterUsingNameInData", 2);

		dataFactorySpy.MCR.assertParameters("factorDataChildFilterUsingNameInData", 0,
				"organisationRoot");
		DataChildFilterSpy firstConstraintChildFilter = (DataChildFilterSpy) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 0);
		firstConstraintChildFilter.MCR
				.assertMethodNotCalled("addAttributeUsingNameInDataAndPossibleValues");
	}

	private void assertTheSecondConstraintIsCorrectWithTwoAttributes() {
		assertConstraintWithAttribute(1, "organisationAlternativeName",
				"textPartTypeCollectionVar");
		assertConstraintAttribute(1, "organisationAlternativeName", "textPartTypeCollectionVar", 5,
				"default", 0);
		assertConstraintAttribute(1, "organisationAlternativeName", "textPartLangCollectionVar", 6,
				"sv", 1);
	}

	private void assertConstraintWithAttribute(int constraintNumber, String constraintNameInData,
			String metadataIdForAttribute) {
		dataFactorySpy.MCR.assertParameters("factorDataChildFilterUsingNameInData",
				constraintNumber, constraintNameInData);
	}

	private void assertConstraintAttribute(int constraintNumber, String constraintNameInData,
			String metadataIdForAttribute, int attributeReadNumber,
			String possibleValuesForAttribute, int attributeNumberInConstraint) {

		assertParementerFirstOnParameterTypesList(storageSpy, "read", attributeReadNumber,
				"metadataCollectionVariable");
		storageSpy.MCR.assertParameter("read", attributeReadNumber, "id", metadataIdForAttribute);

		DataGroupSpiderOldSpy secondAttributecollectionVarSpy = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", attributeReadNumber);
		secondAttributecollectionVarSpy.MCR.assertParameters("containsChildWithNameInData", 0,
				"finalValue");
		secondAttributecollectionVarSpy.MCR.assertParameters("getFirstAtomicValueWithNameInData", 1,
				"finalValue");

		DataChildFilterSpy childFilter = (DataChildFilterSpy) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", constraintNumber);
		Set<?> possibleValuesSecondAttrib = (Set<?>) childFilter.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"addAttributeUsingNameInDataAndPossibleValues", attributeNumberInConstraint,
						"possibleValues");
		assertEquals(possibleValuesSecondAttrib.size(), 1);
		assertEquals(possibleValuesSecondAttrib.toArray()[0], possibleValuesForAttribute);
	}

	@Test
	public void testConstraintWithAnAttributeWithSeveralPossibleValues() throws Exception {
		storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		Set<Constraint> constraints = recordTypeHandler.getReadRecordPartConstraints();

		// storageSpy.MCR.assertNumberOfCallsToMethod("read", 10);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, "metadata");
		storageSpy.MCR.assertParameter("read", 1, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, "metadata");
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, "metadataTextVariable");
		storageSpy.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 4, "metadata");
		storageSpy.MCR.assertParameter("read", 4, "id", "organisationAlternativeNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 5,
				"metadataCollectionVariable");
		storageSpy.MCR.assertParameter("read", 5, "id", "choosableAttributeCollectionVar");

		String collectionId = assertChossableAttributeCollectionVar();

		assertParementerFirstOnParameterTypesList(storageSpy, "read", 6, "metadataItemCollection");
		storageSpy.MCR.assertParameter("read", 6, "id", "choosableCollection");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 7, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 7, "id", "metadataCollectionItem");

		var metadataCollectionItemDataGroup = storageSpy.MCR.getReturnValue("read", 7);
		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0,
				metadataCollectionItemDataGroup);
		RecordTypeHandlerSpy metadataCollectionItemRecordTypeHandler = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 0);
		List<?> metadataCollectionItemTypes = (List<?>) metadataCollectionItemRecordTypeHandler.MCR
				.getReturnValue("getListOfImplementingRecordTypeIds", 0);

		storageSpy.MCR.assertParameters("read", 8, metadataCollectionItemTypes,
				"choosableCollectionItem1");
		storageSpy.MCR.assertParameters("read", 9, metadataCollectionItemTypes,
				"choosableCollectionItem2");

		List<DataGroupSpy> allItemRefs = assertCollectionItemReferences(collectionId);

		Collection<String> possibleValues = assertItemValuesAndGetAllPossibleValues(allItemRefs);

		DataChildFilterSpy childFilter = (DataChildFilterSpy) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 1);
		Set<?> possibleValuesSentToChildFilter = (Set<?>) childFilter.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"addAttributeUsingNameInDataAndPossibleValues", 0, "possibleValues");
		assertEquals(possibleValuesSentToChildFilter.size(), 2);
		assertEquals(possibleValuesSentToChildFilter.size(), possibleValues.size());
		assertTrue(possibleValuesSentToChildFilter.containsAll(possibleValues));

		assertEquals(constraints.size(), 2);
		assertEquals(((Constraint) constraints.toArray()[1]).getDataChildFilter(), childFilter);
	}

	private List<DataGroupSpy> assertCollectionItemReferences(String collectionId) {

		DataGroupSpy possibleAttributesCollection = (DataGroupSpy) storageSpy.MCR
				.getReturnValue("read", 6);
		possibleAttributesCollection.MCR.assertParameters("getFirstGroupWithNameInData", 0,
				"collectionItemReferences");
		DataGroupSpy collectionItemReferences = (DataGroupSpy) possibleAttributesCollection.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		collectionItemReferences.MCR.assertParameters("getAllGroupsWithNameInData", 0, "ref");
		var allItemRefs = collectionItemReferences.MCR.getReturnValue("getAllGroupsWithNameInData",
				0);
		return (List<DataGroupSpy>) allItemRefs;
	}

	private String assertChossableAttributeCollectionVar() {

		DataGroupSpy collectionVar = (DataGroupSpy) storageSpy.MCR.getReturnValue("read", 5);
		collectionVar.MCR.assertParameters("containsChildWithNameInData", 0, "finalValue");
		collectionVar.MCR.assertParameters("getFirstGroupWithNameInData", 0, "refCollection");
		DataGroupSpy refCollection = (DataGroupSpy) collectionVar.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		refCollection.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				LINKED_RECORD_ID);
		String collectionId = (String) refCollection.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		return collectionId;
	}

	private Collection<String> assertItemValuesAndGetAllPossibleValues(
			List<DataGroupSpy> allItemRefs) {
		assertEquals(allItemRefs.size(), 2);
		return assertItemValues(allItemRefs);
	}

	private Collection<String> assertItemValues(List<DataGroupSpy> allItemRefs) {
		Collection<String> possibleValues = new ArrayList<>();
		int nextReadCallNumber = 8;
		for (DataGroupSpy itemRef : allItemRefs) {
			assertItemValue(possibleValues, nextReadCallNumber, itemRef);
			nextReadCallNumber++;
		}
		return possibleValues;
	}

	private void assertItemValue(Collection<String> possibleValues, int nextReadCallNumber,
			DataGroupSpy itemRef) {
		itemRef.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, LINKED_RECORD_ID);
		String itemId = (String) itemRef.MCR.getReturnValue("getFirstAtomicValueWithNameInData", 0);

		storageSpy.MCR.assertParameter("read", nextReadCallNumber, "id", itemId);

		DataGroupSpy itemGroup = (DataGroupSpy) storageSpy.MCR.getReturnValue("read",
				nextReadCallNumber);
		itemGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "nameInData");
		String itemValue = (String) itemGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		possibleValues.add(itemValue);
	}

	// HERE
	@Test
	public void testGetRecordPartReadConstraintsWithAttributesListOfMetadataCollectionItemOnlyBuiltOnce() {
		storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 2;
		storageSpy.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");
		storageSpy.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		recordTypeHandler.getReadRecordPartConstraints();

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 15);
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraintWithAttributeOLD() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();

		assertCorrectConstraintsWithOneAttributeForOneChild(recordPartReadConstraints);
		assertCorrectWriteConstraintsWithOneAttributeForOneChild();
		assertCorrectCreateWriteConstraintsWithOneAttributeForOneChild();

		dataFactorySpy.MCR.assertMethodNotCalled("factorAttributeUsingNameInDataAndValue");
		dataFactorySpy.MCR.assertMethodWasCalled("factorDataChildFilterUsingNameInData");
		dataFactorySpy.MCR.assertNumberOfCallsToMethod("factorDataChildFilterUsingNameInData", 4);
	}

	private void assertCorrectConstraintsWithOneAttributeForOneChild(
			Set<Constraint> recordPartReadConstraints) {
		assertEquals(recordPartReadConstraints.size(), 2);

		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "organisationRoot",
				0);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints,
				"organisationAlternativeName", 1);
	}

	private void assertCorrectWriteConstraintsWithOneAttributeForOneChild() {
		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertCorrectConstraintsWithOneAttributeForOneChild(recordPartWriteConstraints);
	}

	private void assertCorrectCreateWriteConstraintsWithOneAttributeForOneChild() {
		Set<Constraint> recordPartCreateWriteConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateWriteConstraints.size(), 2);

		assertConstraintExistWithNumberOfAttributes(recordPartCreateWriteConstraints,
				"organisationRoot2", 0);
		assertConstraintExistWithNumberOfAttributes(recordPartCreateWriteConstraints,
				"organisationAlternativeName", 1);
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraintWithTwoAttributes() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		// storageSpy.numberOfAttributes = 2;
		storageSpy.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");
		storageSpy.attributesIdsToAddToConstraint.add("textPartLangCollectionVar");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 2);

		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "organisationRoot",
				0);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints,
				"organisationAlternativeName", 2);
	}

	// new stuff here, for children to max 1
	@Test
	public void testGetRecordPartReadConstraintsWithGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 1;

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartReadConstraints);
	}

	private void assertCorrectConstraintsIncludingFirstLevelChild(
			Set<Constraint> recordPartReadConstraints) {
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "organisationRoot",
				0);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints,
				"organisationAlternativeName", 0);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "showInPortal", 0);
	}

	@Test
	public void testGetRecordPartReadConstraintsWithGreatGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartReadConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartReadConstraintsWithGreatGrandChildNOTMax1() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;
		storageSpy.maxNoOfGrandChildren = "3";

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartReadConstraints);
	}

	/**********************************************************************/
	@Test
	public void testRecursiveChildOnlyTraversedOnce() {
		DataGroupSpiderOldSpy dataGroup = new DataGroupSpiderOldSpy("dataGroupNameInData");
		dataGroup.addChild(dataGroup);

		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationRecursiveChild");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(storageSpy.types.size(), 4);
		assertEquals(storageSpy.ids.size(), 4);
		assertEquals(storageSpy.ids.get(0), "organisationRecursiveChild");
		assertEquals(storageSpy.ids.get(1), "organisationRecursiveChild");
		assertEquals(storageSpy.ids.get(2), "divaOrganisationRecursiveNameGroup");
		assertEquals(storageSpy.ids.get(3), "divaOrganisationRecursiveNameGroup");

		DataGroupSpiderOldSpy returnValueNameGroup = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 2);
		int recordInfoAndChildRefsRequested = 2;
		assertEquals(returnValueNameGroup.requestedDataGroups.size(),
				recordInfoAndChildRefsRequested);

		DataGroupSpiderOldSpy returnValueNameGroup2 = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 3);
		int onlyRecordInfoRequested = 1;
		assertEquals(returnValueNameGroup2.requestedDataGroups.size(), onlyRecordInfoRequested);
		assertTrue(recordPartReadConstraints.isEmpty());
	}

	/**********************************************************************/
	@Test
	public void testGetRecordPartReadWriteConstraintsWithGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 1;

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGreatGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartWriteConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGreatGrandChildNOTMax1() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;
		storageSpy.maxNoOfGrandChildren = "3";

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 1;
		storageSpy.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGreatGrandChild() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;
		storageSpy.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartCreateConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGreatGrandChildNOTMax1() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfGrandChildrenWithReadWriteConstraint = 2;
		storageSpy.maxNoOfGrandChildren = "3";
		storageSpy.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
	}

	@Test
	public void testGetRecordPartReadConstraintsReturnsSameInstance() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(storageSpy);

	}

	private void assertReadStorageOnlyOnce(RecordTypeHandlerStorageSpy storageSpy) {
		storageSpy.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, "metadataTextVariable");
		storageSpy.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot");
	}

	@Test
	public void testGetRecordPartWriteConstraintsReturnsSameInstance() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(storageSpy);

	}

	@Test
	public void testGetRecordPartCreateConstraintsReturnsSameInstance() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		var constraints1 = recordTypeHandler.getCreateWriteRecordPartConstraints();
		var constraints2 = recordTypeHandler.getCreateWriteRecordPartConstraints();

		assertSame(constraints1, constraints2);

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 1, "id", "organisationNew");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, "metadataGroup");
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, "metadataTextVariable");
		storageSpy.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot2");
	}

	@Test
	public void testGetRecordPartReadConstraintsTwoReadConstraint() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");

		storageSpy.numberOfChildrenWithReadWriteConstraint = 2;
		Set<Constraint> recordPartReadForUpdateConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadForUpdateConstraints.size(), 2);

		assertTrue(containsConstraintWithNameInData(recordPartReadForUpdateConstraints,
				"organisationRoot"));
		assertTrue(containsConstraintWithNameInData(recordPartReadForUpdateConstraints,
				"showInPortal"));

		Set<Constraint> recordPartWriteForUpdateConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteForUpdateConstraints.size(), 2);

		assertTrue(containsConstraintWithNameInData(recordPartWriteForUpdateConstraints,
				"showInPortal"));
		assertTrue(containsConstraintWithNameInData(recordPartWriteForUpdateConstraints,
				"organisationRoot"));

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 2);
		assertTrue(
				containsConstraintWithNameInData(recordPartCreateConstraints, "organisationRoot2"));
		assertTrue(containsConstraintWithNameInData(recordPartCreateConstraints, "showInPortal2"));
	}

	@Test
	public void testGetRecordPartReadConstraintsOnlyReadWriteConstraintsAreAdded() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 2;
		storageSpy.numberOfChildrenWithWriteConstraint = 1;

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 2);

		assertTrue(containsConstraintWithNameInData(recordPartReadConstraints, "organisationRoot"));
		assertTrue(containsConstraintWithNameInData(recordPartReadConstraints, "showInPortal"));
		assertFalse(containsConstraintWithNameInData(recordPartReadConstraints, "showInDefence"));

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 3);

		assertTrue(containsConstraintWithNameInData(recordPartWriteConstraints, "showInPortal"));
		assertTrue(containsConstraintWithNameInData(recordPartWriteConstraints, "showInDefence"));
		assertTrue(
				containsConstraintWithNameInData(recordPartWriteConstraints, "organisationRoot"));

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 3);
		assertTrue(
				containsConstraintWithNameInData(recordPartCreateConstraints, "organisationRoot2"));
		assertTrue(containsConstraintWithNameInData(recordPartCreateConstraints, "showInPortal2"));
		assertTrue(containsConstraintWithNameInData(recordPartCreateConstraints, "showInDefence2"));
	}

	private boolean containsConstraintWithNameInData(Set<Constraint> constraints,
			String nameInData) {

		for (Constraint constraint : constraints) {
			if (constraint.getNameInData().equals(nameInData)) {
				return true;
			}
		}

		return false;
	}

	@Test
	public void testHasRecordPartReadConstraintsNoConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		assertFalse(recordTypeHandler.hasRecordPartReadConstraint());
	}

	@Test
	public void testHasRecordPartReadConstraintsOneConstraints() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		assertTrue(recordTypeHandler.hasRecordPartReadConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsNoConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		assertFalse(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsOneReadConstraint() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsOneWriteConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 0;
		storageSpy.numberOfChildrenWithWriteConstraint = 1;

		assertTrue(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsNoConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");

		assertFalse(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsOneReadConstraint() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
		storageSpy.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsOneWriteConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 0;
		storageSpy.numberOfChildrenWithWriteConstraint = 1;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasParent() {
		setupForStorageForLink("parentId", "someParentId");
		setUpHandlerUsingTypeId(SOME_ID);

		boolean hasParent = recordTypeHandler.hasParent();

		assertTrue(hasParent);
	}

	private void setupForStorageForLink(String nameInData, String value) {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				nameInData);

		DataRecordLinkSpy link = new DataRecordLinkSpy();
		dataGroupSpy.MRV.setSpecificReturnValuesSupplier("getFirstChildWithNameInData", () -> link,
				nameInData);
		link.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> value);

		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroupSpy);
	}

	@Test
	public void testHasNoParent() {
		setUpHandlerUsingTypeId(SOME_ID);

		assertFalse(recordTypeHandler.hasParent());
	}

	@Test
	public void testGetParentId() {
		setupForStorageForLink("parentId", "someParentId");
		setUpHandlerUsingTypeId(SOME_ID);

		String parentId = recordTypeHandler.getParentId();

		assertEquals(parentId, "someParentId");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to get parentId, no parents exists")
	public void testGetParentIdThrowsExceptionWhenMissing() {
		setUpHandlerUsingTypeId(SOME_ID);

		recordTypeHandler.getParentId();
	}

	@Test
	public void testIsChildOfBinaryNoParent() {
		setUpHandlerUsingTypeId(SOME_ID);

		boolean isChildOfBinary = recordTypeHandler.isChildOfBinary();

		assertFalse(isChildOfBinary);
	}

	@Test
	public void testIsChildOfBinaryHasParentButNotBinary() {
		setupForStorageForLink("parentId", "NOT_binary");
		setUpHandlerUsingTypeId(SOME_ID);

		assertFalse(recordTypeHandler.isChildOfBinary());
	}

	@Test
	public void testIsChildOfBinaryHasParentIsBinary() {
		setupForStorageForLink("parentId", "binary");
		setUpHandlerUsingTypeId(SOME_ID);

		assertTrue(recordTypeHandler.isChildOfBinary());
	}

	@Test
	public void testIsNotSearch() {
		setUpHandlerForRecordTypeUsingId("notSearch");

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertFalse(isSearchType);
	}

	private void setUpHandlerForRecordTypeUsingId(String recordId) {
		DataGroupSpy dataGroup = createTopDataGroupWithId(recordId);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
		setUpHandlerUsingTypeId("recordType");
	}

	private void assertIdIsReadFromRecordInfo(DataGroupSpy dataGroup) {
		dataGroup.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo = (DataGroupSpy) dataGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		recordInfo.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "id");
	}

	@Test
	public void testIsSearch() {
		setUpHandlerForRecordTypeUsingId("search");

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertTrue(isSearchType);
	}

	@Test
	public void testIsNotRecordType() {
		setUpHandlerForRecordTypeUsingId("NOT_recordType");

		boolean isRecordType = recordTypeHandler.representsTheRecordTypeDefiningRecordTypes();

		assertFalse(isRecordType);
	}

	@Test
	public void testIsRecordType() {
		setUpHandlerForRecordTypeUsingId("recordType");

		boolean isRecordType = recordTypeHandler.representsTheRecordTypeDefiningRecordTypes();

		assertTrue(isRecordType);
	}

	@Test
	public void testHasSearch() {
		DataGroupSpy dataGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId("search",
				"someSearchId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();
		assertTrue(hasSearch);

		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
	}

	private void setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(DataGroup dataGroup,
			String recordTypeId) {
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
		setUpHandlerUsingTypeId(recordTypeId);
	}

	@Test
	public void testHasNoSearch() {
		DataGroupSpy dataGroup = createTopDataGroup();

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();
		assertFalse(hasSearch);

		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
	}

	@Test
	public void testGetSearchId() {
		DataGroupSpy dataGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId("search",
				"someSearchId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		String searchId = recordTypeHandler.getSearchId();

		assertUsedLinkCallNo(dataGroup, "search", searchId, 0);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to get searchId, no search exists")
	public void testGetSearchIdThrowsExceptionWhenMissing() {
		DataGroupSpy dataGroup = createTopDataGroup();

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		recordTypeHandler.getSearchId();
	}

	@Test
	public void testGetImplentingRecordTypesNotAbstract() {
		setUpHandlerUsingTypeId(SOME_ID);

		List<RecordTypeHandler> recordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();
		assertTrue(recordTypeHandlers.isEmpty());
		recordStorage.MCR.assertMethodNotCalled("readList");
	}

	@Test
	public void testGetGroupGetImplentingRecordTypesNotAbstract() {
		DataGroupSpy dataGroup = createTopDataGroup();

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		List<RecordTypeHandler> recordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();
		assertTrue(recordTypeHandlers.isEmpty());
		recordStorage.MCR.assertMethodNotCalled("readList");
	}

	@Test
	public void testGetImplentingRecordTypesAbstractButNoImplementingChildren() {
		setupForStorageAtomicValue(ABSTRACT, "true");
		setUpHandlerUsingTypeId(SOME_ID);

		List<RecordTypeHandler> recordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();

		assertTrue(recordTypeHandlers.isEmpty());
		DataGroupSpy dataGroupMCR = getRecordTypeDataGroupReadFromStorage();
		assertCallMadeToStorageForAbstractRecordType(dataGroupMCR);
	}

	private void assertCallMadeToStorageForAbstractRecordType(DataGroupSpy dataGroupMCR) {
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, ABSTRACT);

		assertParementerFirstOnList(recordStorage, "readList", 0, RECORD_TYPE);

		Filter filter = (Filter) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readList", 0, "filter");
		assertFalse(filter.filtersResults());
	}

	private void assertParementerFirstOnList(RecordStorageSpy storage, String methodName,
			int callNumber, String recordType) {
		List<?> recordTypeList = (List<?>) storage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(methodName, callNumber,
						"types");
		assertEquals(recordTypeList.size(), 1);
		assertEquals(recordTypeList.get(0), recordType);
	}

	private void assertParementerFirstOnParameterTypesList(RecordTypeHandlerStorageSpy storage,
			String methodName, int callNumber, String recordType) {
		List<?> recordTypeList = (List<?>) storage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(methodName, callNumber,
						"types");
		assertEquals(recordTypeList.size(), 1);
		assertEquals(recordTypeList.get(0), recordType);
	}

	@Test
	public void testDataGroupGetImplentingRecordTypesAbstractButNoImplementingChildren() {
		DataGroupSpy dataGroup = setupDataGroupWithAtomicValue(ABSTRACT, "true");
		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		List<RecordTypeHandler> recordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();

		assertTrue(recordTypeHandlers.isEmpty());
		assertCallMadeToStorageForAbstractRecordType(dataGroup);
	}

	@Test
	public void testGetImplentingRecordTypesAbstractWithImplementingFirstLevelChildren() {
		setupForStorageAtomicValue(ABSTRACT, "true");

		List<RecordTypeHandlerSpy> list = new ArrayList<>();
		list.add(createTypeHandlerSpy(SOME_ID, "", true));
		list.add(createTypeHandlerSpy("id2", SOME_ID, false));
		list.add(createTypeHandlerSpy("id3", "someOtherId", false));

		Iterator<RecordTypeHandlerSpy> iterator = list.iterator();
		recordTypeHandlerFactory.MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup",
				(Supplier<RecordTypeHandlerSpy>) () -> iterator.next());

		createFakeGroupsInAnswerToList(list.size());

		RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
				.usingRecordStorageAndRecordTypeId(recordTypeHandlerFactory, recordStorage,
						SOME_ID);
		setUpHandlerForRecordTypeUsingId(SOME_ID);

		// setUpHandlerUsingTypeId(SOME_ID);

		List<RecordTypeHandler> returnedTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();

		assertRecordTypeHandlerFactoredForAllRecordTypesInStorage();

		RecordTypeHandlerSpy recordTypeHandlerSpy0 = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 0);
		recordTypeHandlerSpy0.MCR.assertMethodWasCalled("hasParent");
		recordTypeHandlerSpy0.MCR.assertMethodNotCalled("getParentId");

		RecordTypeHandlerSpy recordTypeHandlerSpy1 = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 1);
		recordTypeHandlerSpy1.MCR.assertMethodWasCalled("hasParent");
		recordTypeHandlerSpy1.MCR.assertMethodWasCalled("getParentId");

		RecordTypeHandlerSpy recordTypeHandlerSpy2 = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 2);
		recordTypeHandlerSpy2.MCR.assertMethodWasCalled("hasParent");
		recordTypeHandlerSpy2.MCR.assertMethodWasCalled("getParentId");

		assertEquals(returnedTypeHandlers.size(), 1);
		assertSame(returnedTypeHandlers.get(0), recordTypeHandlerSpy1);
	}

	private void createFakeGroupsInAnswerToList(int numberOfFakeGroups) {
		StorageReadResult result = new StorageReadResult();
		result.totalNumberOfMatches = numberOfFakeGroups;
		for (int i = 0; i < numberOfFakeGroups; i++) {
			result.listOfDataGroups.add(new DataGroupSpy());
		}
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> result);
	}

	private RecordTypeHandlerSpy createTypeHandlerSpy(String recordId, String parentId,
			boolean isAbstract) {

		RecordTypeHandlerSpy recordTypeHandler = new RecordTypeHandlerSpy();

		recordTypeHandler.id = recordId;
		if (!parentId.equals("")) {
			recordTypeHandler.hasParent = true;
		}
		recordTypeHandler.isAbstract = isAbstract;
		recordTypeHandler.parentId = parentId;
		return recordTypeHandler;
	}

	private void assertRecordTypeHandlerFactoredForAllRecordTypesInStorage() {
		StorageReadResult result = (StorageReadResult) recordStorage.MCR.getReturnValue("readList",
				0);
		List<DataGroup> fakeDataGroups = result.listOfDataGroups;
		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0,
				fakeDataGroups.get(0));
		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 1,
				fakeDataGroups.get(1));
		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 2,
				fakeDataGroups.get(2));
	}

	@Test
	public void testGetImplentingRecordTypesAbstractWithImplementingSeveralAbstarctLevels()
			throws Exception {

		setupForStorageAtomicValue(ABSTRACT, "true");
		String startRecordId = SOME_ID;

		List<RecordTypeHandlerSpy> list = new ArrayList<>();
		RecordTypeHandlerSpy id5 = createTypeHandlerSpy("id5", "otherId1", false);
		list.add(id5);

		RecordTypeHandlerSpy id4 = createTypeHandlerSpy("id4", "id2", false);
		list.add(id4);

		RecordTypeHandlerSpy id3 = createTypeHandlerSpy("id3", "otherId1", true);
		id3.MRV.setReturnValues("getImplementingRecordTypeHandlers", List.of(List.of(id5)));
		list.add(id3);

		RecordTypeHandlerSpy id2 = createTypeHandlerSpy("id2", startRecordId, true);
		id2.MRV.setReturnValues("getImplementingRecordTypeHandlers", List.of(List.of(id4)));
		list.add(id2);

		RecordTypeHandlerSpy id6 = createTypeHandlerSpy("id6", startRecordId, false);
		list.add(id6);

		RecordTypeHandlerSpy someId = createTypeHandlerSpy(startRecordId, "", true);
		someId.MRV.setReturnValues("getImplementingRecordTypeHandlers", List.of(List.of(id2, id6)));
		list.add(someId);

		Iterator<RecordTypeHandlerSpy> iterator = list.iterator();
		recordTypeHandlerFactory.MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup",
				(Supplier<RecordTypeHandlerSpy>) () -> iterator.next());

		createFakeGroupsInAnswerToList(list.size());

		RecordTypeHandler recordTypeHandler = RecordTypeHandlerImp
				.usingRecordStorageAndRecordTypeId(recordTypeHandlerFactory, recordStorage,
						startRecordId);
		// setUpHandlerForRecordTypeUsingId(startRecordId);
		// setUpHandlerUsingTypeId(startRecordId);

		List<RecordTypeHandler> returnedTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();

		assertEquals(returnedTypeHandlers.size(), 2);
		assertSame(returnedTypeHandlers.get(0), id4);
		assertSame(returnedTypeHandlers.get(1), id6);
	}

	// @Test
	// public void testGetListOfImplementingRecordTypeIds() {
	// RecordTypeHandlerExtendedForTest rthft = new RecordTypeHandlerExtendedForTest(2);
	//
	// List<String> listOfIds = rthft.getListOfImplementingRecordTypeIds();
	// assertEquals(listOfIds.size(), 2);
	// assertEquals(listOfIds.get(0), "fakeRecordTypeIdFromRecordTypeHandlerSpy");
	// assertEquals(listOfIds.get(1), "fakeRecordTypeIdFromRecordTypeHandlerSpy");
	// }

	@Test
	public void testGetListOfRecordTypeIdsToReadFromStorage_ImplementingType() {
		setUpHandlerForRecordTypeUsingId(SOME_ID);

		List<String> listOfIds = recordTypeHandler.getListOfRecordTypeIdsToReadFromStorage();

		assertEquals(listOfIds.size(), 1);
		assertEquals(listOfIds.get(0), "someId");
	}

	// @Test
	// public void testGetListOfRecordTypeIdsToReadFromStorage_AbstractTypeNoImplementing() {
	// setupForStorageAtomicValue(ABSTRACT, "true");
	// setUpHandlerUsingTypeId(SOME_ID);
	//
	// List<String> listOfIds = recordTypeHandler.getListOfRecordTypeIdsToReadFromStorage();
	//
	// assertEquals(listOfIds.size(), 0);
	// }

	// @Test
	// public void testGetListOfRecordTypeIdsToReadFromStorage_ImplementingTypesLowerDown() {
	// RecordTypeHandlerExtendedForTest recordTypeHandler = new RecordTypeHandlerExtendedForTest(
	// 2);
	// recordTypeHandler.isAbstract = true;
	//
	// List<String> listOfIds = recordTypeHandler.getListOfRecordTypeIdsToReadFromStorage();
	//
	// assertEquals(listOfIds.size(), 2);
	// assertEquals(listOfIds.get(0), "fakeRecordTypeIdFromRecordTypeHandlerSpy");
	// assertEquals(listOfIds.get(1), "fakeRecordTypeIdFromRecordTypeHandlerSpy");
	// }

	@Test
	public void testShouldStoreInArchive() {
		setupForStorageAtomicValue("storeInArchive", "false");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertShouldStoreInArchive(dataGroup, false);
	}

	private void assertShouldStoreInArchive(DataGroupSpy dataGroupMCR, boolean expected) {
		assertEquals(recordTypeHandler.storeInArchive(), expected);
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "storeInArchive");
	}

	@Test
	public void testStoreInArchiveFromDataGroup() {
		DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("storeInArchive", "true");
		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		assertShouldStoreInArchive(dataGroup, true);
	}

	@Test
	public void testStoreInArchiveTrue() {
		setupForStorageAtomicValue("storeInArchive", "true");
		setUpHandlerUsingTypeId(SOME_ID);

		DataGroupSpy dataGroup = getRecordTypeDataGroupReadFromStorage();
		assertShouldStoreInArchive(dataGroup, true);
	}

	@Test
	public void testShouldStoreInArchiveFalseFromDataGroup() {
		DataGroupSpy dataGroup = setupDataGroupWithAtomicValue("userSuppliedId", "false");
		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		assertShouldStoreInArchive(dataGroup, false);
	}
}

class RecordTypeHandlerExtendedForTest extends RecordTypeHandlerImp {
	public boolean isAbstract = false;
	List<RecordTypeHandler> implementingRecordTypesSpies = new ArrayList<>();

	public RecordTypeHandlerExtendedForTest(int numberOfSpies) {
		for (int j = 0; j < numberOfSpies; j++) {
			addRecordTypeHandlerSpyToList();
		}
	}

	private void addRecordTypeHandlerSpyToList() {
		RecordTypeHandlerSpy recordTypeHandlerSpy = new RecordTypeHandlerSpy();
		implementingRecordTypesSpies.add(recordTypeHandlerSpy);
	}

	@Override
	public List<RecordTypeHandler> getImplementingRecordTypeHandlers() {
		return implementingRecordTypesSpies;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
}
