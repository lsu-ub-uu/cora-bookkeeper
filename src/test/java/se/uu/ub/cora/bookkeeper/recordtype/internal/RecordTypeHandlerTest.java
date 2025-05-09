/*
 * Copyright 2016, 2019, 2020, 2021, 2022, 2024, 2025 Uppsala University Library
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.Unique;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerTest {
	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private static final String METADATA = "metadata";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String RECORD_TYPE = "recordType";
	private static final String SOME_ID = "someId";
	private DataFactorySpy dataFactorySpy;
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
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", DataGroupSpy::new);

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
				.getParameterForMethodAndCallNumberAndParameter("read", 0, "types");
		assertEquals(listOfTypes.size(), 1);
		assertEquals(listOfTypes.get(0), "recordType");
		recordStorage.MCR.assertParameter("read", 0, "id", "someTypeToValidate");
	}

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

	private void setUpRecordTypeHandlerUsingTypeId(String recordTypeId) {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType(recordTypeId, "someCreateDefinitionId",
						"someUpdateDefinitionId")));
		recordTypeHandler = RecordTypeHandlerImp
				.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
						recordTypeHandlerFactory, recordStorage, metadataStorageViewSpy,
						"someValidationTypeId");
	}

	@Test
	public void testShouldAutoGenerateId_NotExisting() {
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean shouldAutoGenerateId = recordTypeHandler.shouldAutoGenerateId();

		assertEquals(shouldAutoGenerateId, true);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "userSuppliedId");
	}

	@Test
	public void testShouldAutoGenerateId() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("userSuppliedId", FALSE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean shouldAutoGenerateId = recordTypeHandler.shouldAutoGenerateId();

		assertEquals(shouldAutoGenerateId, true);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "userSuppliedId");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "userSuppliedId");
	}

	private void setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue(String nameInData,
			String value) {
		DataGroupSpy dataGroupWithAtomic = new DataGroupSpy();
		dataGroupWithAtomic.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
		dataGroupWithAtomic.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, nameInData);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroupWithAtomic);
	}

	private DataGroupSpy getDataGroupFromStorage() {
		return (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);
	}

	@Test
	public void testShouldAutoGenerateIdFalse() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("userSuppliedId", TRUE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean shouldAutoGenerateId = recordTypeHandler.shouldAutoGenerateId();

		assertEquals(shouldAutoGenerateId, false);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "userSuppliedId");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "userSuppliedId");
	}

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
		setUpRecordTypeHandlerUsingTypeId("someRecordId");

		String metadataId = recordTypeHandler.getDefinitionId();
		DataGroupSpy dataGroup1 = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);

		DataGroupSpy dataGroup = dataGroup1;
		assertUsedLink(dataGroup, "metadataId", metadataId);
	}

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
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
	}

	private DataGroupSpy setupForLinkWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
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
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("public", TRUE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		assertIsPublicForRead(dataGroup, true);
	}

	private void assertIsPublicForRead(DataGroupSpy dataGroupMCR, boolean expected) {
		assertEquals(recordTypeHandler.isPublicForRead(), expected);
		dataGroupMCR.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "public");
	}

	@Test
	public void testPublicFalse() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("public", FALSE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		assertIsPublicForRead(dataGroup, false);
	}

	@Test
	public void testGetRecordPartReadConstraintsNOReadConstraint() {
		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisation");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
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
		assertConstraintAttribute(1, "organisationAlternativeName", "textPartTypeCollectionVar", 6,
				"default", 0);
		assertConstraintAttribute(1, "organisationAlternativeName", "textPartLangCollectionVar", 7,
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
				.getParameterForMethodAndCallNumberAndParameter(
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

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 10);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");

		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, METADATA);
		storageSpy.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");

		assertParementerFirstOnParameterTypesList(storageSpy, "read", 4, METADATA);
		storageSpy.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 5, METADATA);
		storageSpy.MCR.assertParameter("read", 5, "id", "organisationAlternativeNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 6,
				"metadataCollectionVariable");
		storageSpy.MCR.assertParameter("read", 6, "id", "choosableAttributeCollectionVar");

		String collectionId = assertChossableAttributeCollectionVar();

		assertParementerFirstOnParameterTypesList(storageSpy, "read", 7, METADATA);
		storageSpy.MCR.assertParameter("read", 7, "id", "choosableCollection");
		storageSpy.MCR.assertParameter("read", 8, "id", "choosableCollectionItem1");
		storageSpy.MCR.assertParameterAsEqual("read", 8, "types", List.of("metadata"));
		storageSpy.MCR.assertParameter("read", 9, "id", "choosableCollectionItem2");
		storageSpy.MCR.assertParameterAsEqual("read", 9, "types", List.of("metadata"));

		List<DataGroupSpy> allItemRefs = assertCollectionItemReferences(collectionId);

		Collection<String> possibleValues = assertItemValuesAndGetAllPossibleValues(allItemRefs);

		DataChildFilterSpy childFilter = (DataChildFilterSpy) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 1);
		Set<?> possibleValuesSentToChildFilter = (Set<?>) childFilter.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"addAttributeUsingNameInDataAndPossibleValues", 0, "possibleValues");
		assertEquals(possibleValuesSentToChildFilter.size(), 2);
		assertEquals(possibleValuesSentToChildFilter.size(), possibleValues.size());
		assertTrue(possibleValuesSentToChildFilter.containsAll(possibleValues));

		assertEquals(constraints.size(), 2);
		assertEquals(((Constraint) constraints.toArray()[1]).getDataChildFilter(), childFilter);
	}

	private List<DataGroupSpy> assertCollectionItemReferences(String collectionId) {
		DataGroupSpy possibleAttributesCollection = (DataGroupSpy) storageSpy.MCR
				.getReturnValue("read", 7);
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
		DataGroupSpy collectionVar = (DataGroupSpy) storageSpy.MCR.getReturnValue("read", 6);
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

	@Test
	public void testRecursiveChildOnlyTraversedOnce() {
		DataGroupSpiderOldSpy dataGroup = new DataGroupSpiderOldSpy("dataGroupNameInData");
		dataGroup.addChild(dataGroup);

		RecordTypeHandlerStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationRecursiveChild");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(storageSpy.types.size(), 5);
		assertEquals(storageSpy.ids.size(), 5);
		assertEquals(storageSpy.ids.get(0), "organisationRecursiveChild");
		assertEquals(storageSpy.ids.get(1), "organisationRecursiveChild");
		assertEquals(storageSpy.ids.get(2), "divaOrganisationRecursiveNameGroup");
		assertEquals(storageSpy.ids.get(3), "showInPortalTextVar");
		assertEquals(storageSpy.ids.get(4), "divaOrganisationRecursiveNameGroup");

		DataGroupSpiderOldSpy returnValueNameGroup = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 2);
		int recordInfoAndChildRefsRequested = 2;
		assertEquals(returnValueNameGroup.requestedDataGroups.size(),
				recordInfoAndChildRefsRequested);

		DataGroupSpiderOldSpy returnValueNameGroup2 = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 4);
		int onlyRecordInfoRequested = 1;
		assertEquals(returnValueNameGroup2.requestedDataGroups.size(), onlyRecordInfoRequested);
		assertTrue(recordPartReadConstraints.isEmpty());
	}

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
		storageSpy.MCR.assertNumberOfCallsToMethod("read", 5);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, METADATA);
		storageSpy.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 4, METADATA);
		storageSpy.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot");
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

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 5);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, RECORD_TYPE);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "organisationNew");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
		storageSpy.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, METADATA);
		storageSpy.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 4, METADATA);
		storageSpy.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot2");
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
	public void testIsNotSearch() {
		setUpHandlerForRecordTypeUsingId("notSearch");

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertFalse(isSearchType);
	}

	private void setUpHandlerForRecordTypeUsingId(String recordId) {
		DataGroupSpy dataGroup = createTopDataGroupWithId(recordId);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read",
				(Supplier<DataGroup>) () -> dataGroup);
		setUpRecordTypeHandlerUsingTypeId("recordType");
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
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroup);
		setUpRecordTypeHandlerUsingTypeId(recordTypeId);
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

	private void assertParementerFirstOnParameterTypesList(RecordTypeHandlerStorageSpy storage,
			String methodName, int callNumber, String recordType) {
		List<?> recordTypeList = (List<?>) storage.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, callNumber, "types");
		assertEquals(recordTypeList.size(), 1);
		assertEquals(recordTypeList.get(0), recordType);
	}

	@Test
	public void testStoreInArchive_NotExists_False() {
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean storeInArchive = recordTypeHandler.storeInArchive();

		assertEquals(storeInArchive, false);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "storeInArchive");
	}

	@Test
	public void testShouldStoreInArchive() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("storeInArchive", FALSE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean storeInArchive = recordTypeHandler.storeInArchive();

		assertEquals(storeInArchive, false);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "storeInArchive");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "storeInArchive");
	}

	@Test
	public void testStoreInArchiveTrue() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("storeInArchive", TRUE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean storeInArchive = recordTypeHandler.storeInArchive();

		assertEquals(storeInArchive, true);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "storeInArchive");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "storeInArchive");
	}

	@Test
	public void testGetUniqueDefinitions_NoDefinitionsExists() throws Exception {
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		List<Unique> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions, Collections.emptyList());
		metadataStorageViewSpy.MCR.assertMethodNotCalled("getCollectTermHolder");
	}

	@Test
	public void testGetUniqueDefinitions_GetCollectTermsIfUniqueExists() throws Exception {
		UniqueStorageTermIds uniqueStorageTermIds = new UniqueStorageTermIds("uniqueTermLinkId",
				Collections.emptySet());
		DataGroupSpy recordType = setUpRecordStorageWithUniqueDefinition(uniqueStorageTermIds);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		recordTypeHandler.getUniqueDefinitions();

		recordType.MCR.assertCalledParameters("containsChildWithNameInData", "unique");
		metadataStorageViewSpy.MCR.assertMethodWasCalled("getCollectTermHolder");
	}

	private DataGroupSpy setUpRecordStorageWithUniqueDefinition(
			UniqueStorageTermIds... uniqueStorageTermIds) {
		DataGroupSpy recordType = createRecorTypeWithUnique(uniqueStorageTermIds);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> recordType);
		return recordType;
	}

	@Test
	public void testGetUniqueDefinitions_OneDefinitionWithOutCombinesExists() throws Exception {
		UniqueStorageTermIds uniqueStorageTermIds = new UniqueStorageTermIds("uniqueTermLinkId",
				Collections.emptySet());
		setUpRecordStorageWithUniqueDefinition(uniqueStorageTermIds);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermLinkId");

		List<Unique> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions.size(), 1);
		assertEquals(uniqueDefinitions.get(0).uniqueTermStorageKey(), "uniqueTermLinkIdStorageKey");
		assertEquals(uniqueDefinitions.get(0).combineTermStorageKeys(), Collections.emptySet());
	}

	@Test
	public void testGetUniqueDefinitions_OneDefinitionWithTwoCombinesExists() throws Exception {
		UniqueStorageTermIds uniqueStorageTermIds = new UniqueStorageTermIds("uniqueTermLinkId",
				Set.of("combineTermId1", "combineTermId2"));
		setUpRecordStorageWithUniqueDefinition(uniqueStorageTermIds);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermLinkId", "combineTermId1",
				"combineTermId2");

		List<Unique> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions.size(), 1);
		assertEquals(uniqueDefinitions.get(0).uniqueTermStorageKey(), "uniqueTermLinkIdStorageKey");
		assertEquals(uniqueDefinitions.get(0).combineTermStorageKeys(),
				Set.of("combineTermId1StorageKey", "combineTermId2StorageKey"));
	}

	@Test
	public void testGetUniqueDefinitions_TwoDefinitionWithTwoCombinesExists() throws Exception {
		UniqueStorageTermIds uniqueStorageTermIdsA = new UniqueStorageTermIds("uniqueTermIdA",
				Set.of("combineTermIdA1", "combineTermIdA2"));
		UniqueStorageTermIds uniqueStorageTermIdsB = new UniqueStorageTermIds("uniqueTermIdB",
				Set.of("combineTermIdB1", "combineTermIdB2"));
		setUpRecordStorageWithUniqueDefinition(uniqueStorageTermIdsA, uniqueStorageTermIdsB);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermIdA", "combineTermIdA1",
				"combineTermIdA2", "uniqueTermIdB", "combineTermIdB1", "combineTermIdB2");

		List<Unique> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions.size(), 2);
		assertEquals(uniqueDefinitions.get(0).uniqueTermStorageKey(), "uniqueTermIdAStorageKey");
		assertEquals(uniqueDefinitions.get(0).combineTermStorageKeys(),
				Set.of("combineTermIdA1StorageKey", "combineTermIdA2StorageKey"));
		assertEquals(uniqueDefinitions.get(1).uniqueTermStorageKey(), "uniqueTermIdBStorageKey");
		assertEquals(uniqueDefinitions.get(1).combineTermStorageKeys(),
				Set.of("combineTermIdB1StorageKey", "combineTermIdB2StorageKey"));

		metadataStorageViewSpy.MCR.assertNumberOfCallsToMethod("getCollectTermHolder", 1);
	}

	record UniqueStorageTermIds(String uniqueTermId, Set<String> combineTermIds) {
	}

	private DataGroupSpy createRecorTypeWithUnique(UniqueStorageTermIds... uniqueStorageTermIdss) {
		DataGroupSpy recordType = new DataGroupSpy();
		recordType.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				"unique");

		Collection<DataGroupSpy> uniques = new ArrayList<>();
		for (UniqueStorageTermIds uniqueStorageTermIds : uniqueStorageTermIdss) {
			DataGroupSpy uniqueDG = createUniqueDefinition(uniqueStorageTermIds);
			uniques.add(uniqueDG);
		}

		recordType.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName", () -> uniques,
				DataGroup.class, "unique");
		return recordType;
	}

	private DataGroupSpy createUniqueDefinition(UniqueStorageTermIds uniqueStorageTermIds) {
		DataGroupSpy uniqueDG = new DataGroupSpy();

		DataRecordLinkSpy uniqueTermLink = new DataRecordLinkSpy();
		uniqueDG.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> uniqueTermLink, DataRecordLink.class, "uniqueTerm");
		uniqueTermLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> uniqueStorageTermIds.uniqueTermId);

		Collection<DataRecordLinkSpy> combineLinks = new ArrayList<>();
		for (String combineTermId : uniqueStorageTermIds.combineTermIds) {
			DataRecordLinkSpy combineTermLink = new DataRecordLinkSpy();
			combineTermLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
					() -> combineTermId);
			combineLinks.add(combineTermLink);
		}
		uniqueDG.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName", () -> combineLinks,
				DataRecordLink.class, "combineTerm");
		return uniqueDG;
	}

	private void setUpMetadataStorageViewWithCollectTermsList(String... collectTermIds) {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getCollectTermHolder",
				() -> createCollecTermsAsTermCollectHolder(collectTermIds));
	}

	private CollectTermHolderSpy createCollecTermsAsTermCollectHolder(String... collectTermIds) {
		CollectTermHolderSpy holder = new CollectTermHolderSpy();

		for (String collectTermId : collectTermIds) {
			StorageTerm storageTerm = StorageTerm.usingIdAndStorageKey("someId",
					collectTermId + "StorageKey");
			holder.MRV.setSpecificReturnValuesSupplier("getCollectTermById", () -> storageTerm,
					collectTermId);
		}
		return holder;
	}

	@Test
	public void testUseVisibilityNotSet() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> false,
				"useVisibility");
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroupSpy);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean useVisibility = recordTypeHandler.useVisibility();

		assertEquals(useVisibility, false);
	}

	@Test
	public void testUseVisibilityTrue() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(TRUE),
				"useVisibility");
		dataGroupSpy.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				"useVisibility");
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroupSpy);

		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		DataGroupSpy dataGroup = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);

		boolean useVisibility = recordTypeHandler.useVisibility();

		assertEquals(useVisibility, true);
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "useVisibility");
	}

	@Test
	public void testUseVisibilityFalse() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(FALSE),
				"useVisibility");
		dataGroupSpy.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData", () -> true,
				"useVisibility");
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroupSpy);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);
		DataGroupSpy dataGroup = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);

		boolean useVisibility = recordTypeHandler.useVisibility();

		assertEquals(useVisibility, false);
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "useVisibility");
	}

	@Test
	public void testUsePermissionUnit_IfDoNotExistsThenFalse() {
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean usePermissionUnit = recordTypeHandler.usePermissionUnit();

		assertEquals(usePermissionUnit, false);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "usePermissionUnit");
	}

	@Test
	public void testUsePermissionUnit_GetValueAsBooleanFromDataGroup() {
		setupForStorageReadReturnsGroupWithAtomicUsingNameInDataAndValue("usePermissionUnit",
				FALSE);
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		boolean usePermissionUnit = recordTypeHandler.usePermissionUnit();

		assertEquals(usePermissionUnit, false);
		DataGroupSpy dataGroup = getDataGroupFromStorage();
		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "usePermissionUnit");
		dataGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "usePermissionUnit");
		dataGroup.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0,
				Boolean.valueOf(usePermissionUnit).toString());
	}
}
