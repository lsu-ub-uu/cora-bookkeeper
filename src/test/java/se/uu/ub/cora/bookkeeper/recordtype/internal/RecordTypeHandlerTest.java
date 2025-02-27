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
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerTest {
	private static final String METADATA = "metadata";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String RECORD_TYPE = "recordType";
	private static final String SOME_ID = "someId";
	private DataFactorySpy dataFactorySpy;
	private RecordTypeHandler recordTypeHandler;
	private RecordStorageSpy recordStorage;
	private RecordTypeHandlerFactorySpy recordTypeHandlerFactory;
	// private RecordTypeHandlerStorageOldSpy recordStorage;
	private MetadataStorageViewSpy metadataStorageViewSpy;
	private DataRecordGroupSpy recordTypeAsDataRecordGroup;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		recordStorage = new RecordStorageSpy();
		recordTypeAsDataRecordGroup = new DataRecordGroupSpy();
		recordTypeAsDataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> SOME_ID);
		recordTypeAsDataRecordGroup.MRV.setDefaultReturnValuesSupplier("getFirstChildOfTypeAndName",
				DataRecordLinkSpy::new);
		recordTypeAsDataRecordGroup.MRV.setDefaultReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> List.of(new DataRecordLinkSpy()));
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> recordTypeAsDataRecordGroup);

		recordTypeHandlerFactory = new RecordTypeHandlerFactorySpy();
		metadataStorageViewSpy = new MetadataStorageViewSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType("someTypeToValidate", "someCreateDefinitionId",
						"someUpdateDefinitionId")));
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "ValidationType with id: someValidationTypeId, does not exist.")
	public void testInitWithValidationTypeNotFoundThrowsValidationException() {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				Optional::empty);

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
	public void testInitWithValidationTypeFoundReadsRecordTypeFromStorage() {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		recordStorage.MCR.assertParameters("read", 0, "recordType", "someTypeToValidate");
	}

	private DataRecordGroupSpy createDataRecordGroupWithId(String id) {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> id);
		return dataRecordGroup;
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

	private DataRecordGroupSpy getRecordTypeDataRecordGroupReadFromStorage() {
		return (DataRecordGroupSpy) recordStorage.MCR.getReturnValue("read", 0);
	}

	private void setupForStorageAtomicValue(String nameInData, String value) {
		DataRecordGroupSpy dataRecordGroupSpy = new DataRecordGroupSpy();
		dataRecordGroupSpy.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(value),
				nameInData);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataRecordGroupSpy);
	}

	private DataRecordGroupSpy setupDataGroupWithAtomicValue(String nameInData, String value) {
		DataRecordGroupSpy topDataGroup = createDataRecordGroupWithId("someId");
		topDataGroup.MRV.setReturnValues("getFirstAtomicValueWithNameInData", List.of(value),
				nameInData);
		return topDataGroup;
	}

	@Test
	public void testShouldAutoGenerateId() {
		setupForStorageAtomicValue("userSuppliedId", "false");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertShouldAutoGenerateId(dataRecordGroup, true);
	}

	private void assertShouldAutoGenerateId(DataRecordGroupSpy dataRecordGroup, boolean expected) {
		assertEquals(recordTypeHandler.shouldAutoGenerateId(), expected);
		dataRecordGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"userSuppliedId");
	}

	@Test
	public void testShouldAutoGenerateIdFalse() {
		setupForStorageAtomicValue("userSuppliedId", "true");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertShouldAutoGenerateId(dataRecordGroup, false);
	}

	@Test
	public void testCreateDefinitionId() {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		String definitionId = recordTypeHandler.getCreateDefinitionId();

		assertEquals(definitionId, "someCreateDefinitionId");
	}

	@Test
	public void testGetUpdateDefinitionId() {
		setUpRecordTypeHandlerWithMetadataStorage(metadataStorageViewSpy);

		String definitionId = recordTypeHandler.getUpdateDefinitionId();

		assertEquals(definitionId, "someUpdateDefinitionId");
	}

	@Test
	public void testGetDefinitionId() {
		setupForLinkForStorageWithNameInDataAndRecordId("metadataId", "someMetadataId");
		setUpRecordTypeHandlerUsingTypeId("someRecordId");

		String metadataId = recordTypeHandler.getDefinitionId();

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertUsedLink(dataRecordGroup, "metadataId", metadataId);
	}

	private void assertUsedLink(DataRecordGroupSpy dataRecordGroup, String linkNameInData,
			String returnedLinkedRecordId) {

		DataRecordLinkSpy linkGroup = (DataRecordLinkSpy) dataRecordGroup.MCR
				.assertCalledParametersReturn("getFirstChildOfTypeAndName", DataRecordLink.class,
						linkNameInData);
		String linkedRecordIdFromSpy = (String) linkGroup.MCR.getReturnValue("getLinkedRecordId",
				0);
		assertEquals(returnedLinkedRecordId, linkedRecordIdFromSpy);
	}

	private void setupForLinkForStorageWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		DataRecordGroupSpy dataGroup = setupForLinkWithNameInDataAndRecordId(linkNameInData,
				linkedRecordId);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroup);
	}

	private DataRecordGroupSpy setupForLinkWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		DataRecordGroupSpy dataGroup = createDataRecordGroupWithId("someRecordType");
		DataRecordLinkSpy link = createLink(linkedRecordId);
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName", () -> link,
				DataRecordLink.class, linkNameInData);

		return dataGroup;
	}

	private DataRecordLinkSpy createLink(String value) {
		DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
		recordLink.MRV.setReturnValues("getLinkedRecordId", List.of(value));
		return recordLink;
	}

	private DataRecordGroupSpy setupDataGroupWithLinkUsingNameInDataAndRecordId(
			String linkNameInData, String linkedRecordId) {
		DataRecordGroupSpy topDataGroup = createDataRecordGroupWithId("someId");
		DataRecordLinkSpy link = createLink(linkedRecordId);
		topDataGroup.MRV.setReturnValues("containsChildWithNameInData", List.of(true),
				linkNameInData);
		topDataGroup.MRV.setReturnValues("getFirstChildWithNameInData", List.of(link),
				linkNameInData);

		topDataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName", () -> link,
				DataRecordLink.class, linkNameInData);
		return topDataGroup;
	}

	@Test
	public void testPublic() {
		setupForStorageAtomicValue("public", "true");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertIsPublicForRead(dataRecordGroup, true);
	}

	private void assertIsPublicForRead(DataRecordGroupSpy dataRecordGroup, boolean expected) {
		assertEquals(recordTypeHandler.isPublicForRead(), expected);
		dataRecordGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "public");
	}

	@Test
	public void testPublicFalse() {
		setupForStorageAtomicValue("public", "false");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertIsPublicForRead(dataRecordGroup, false);
	}

	@Test
	public void testGetRecordPartReadConstraintsNOReadConstraint() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();

		recordStorage.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList("read", 0, RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList("read", 1, METADATA);
		recordStorage.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList("read", 2, METADATA);
		recordStorage.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");

		assertTrue(recordPartReadConstraints.isEmpty());
		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertTrue(recordPartWriteConstraints.isEmpty());

		Set<Constraint> recordPartCreateWriteConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertTrue(recordPartCreateWriteConstraints.isEmpty());
	}

	private void setUpHandlerWithrecordStorageUsingTypeId(String recordTypeId) {
		// recordStorage = new RecordTypeHandlerStorageOldSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional
						.of(new ValidationType(recordTypeId, recordTypeId + "New", recordTypeId)));
		recordTypeHandler = RecordTypeHandlerImp
				.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
						recordTypeHandlerFactory, recordStorage, metadataStorageViewSpy,
						"someValidationTypeId");
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraint() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;

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
		// recordStorage.numberOfChildrenWithReadWriteConstraint
		// - OrganisationRoot has no attributes
		// - OrganisationAlternativeName has 0 to 2 attributes depending on
		// recordStorage.numberOfAttributes

		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");
		recordStorage.attributesIdsToAddToConstraint.add("textPartLangCollectionVar");

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
		assertParementerFirstOnParameterTypesList("read", attributeReadNumber,
				"metadataCollectionVariable");
		recordStorage.MCR.assertParameter("read", attributeReadNumber, "id",
				metadataIdForAttribute);

		DataGroupSpiderOldSpy secondAttributecollectionVarSpy = (DataGroupSpiderOldSpy) recordStorage.MCR
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
	public void testConstraintWithAnAttributeWithSeveralPossibleValues() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		Set<Constraint> constraints = recordTypeHandler.getReadRecordPartConstraints();

		recordStorage.MCR.assertNumberOfCallsToMethod("read", 10);
		assertParementerFirstOnParameterTypesList("read", 0, RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList("read", 1, METADATA);
		recordStorage.MCR.assertParameter("read", 1, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList("read", 2, METADATA);
		recordStorage.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");

		assertParementerFirstOnParameterTypesList("read", 3, METADATA);
		recordStorage.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");

		assertParementerFirstOnParameterTypesList("read", 4, METADATA);
		recordStorage.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot");
		assertParementerFirstOnParameterTypesList("read", 5, METADATA);
		recordStorage.MCR.assertParameter("read", 5, "id", "organisationAlternativeNameGroup");
		assertParementerFirstOnParameterTypesList("read", 6, "metadataCollectionVariable");
		recordStorage.MCR.assertParameter("read", 6, "id", "choosableAttributeCollectionVar");

		String collectionId = assertChossableAttributeCollectionVar();

		assertParementerFirstOnParameterTypesList("read", 7, METADATA);
		recordStorage.MCR.assertParameter("read", 7, "id", "choosableCollection");
		recordStorage.MCR.assertParameter("read", 8, "id", "choosableCollectionItem1");
		recordStorage.MCR.assertParameterAsEqual("read", 8, "types", List.of("metadata"));
		recordStorage.MCR.assertParameter("read", 9, "id", "choosableCollectionItem2");
		recordStorage.MCR.assertParameterAsEqual("read", 9, "types", List.of("metadata"));

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
		DataGroupSpy possibleAttributesCollection = (DataGroupSpy) recordStorage.MCR
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
		DataGroupSpy collectionVar = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 6);
		collectionVar.MCR.assertParameters("containsChildWithNameInData", 0, "finalValue");
		collectionVar.MCR.assertParameters("getFirstGroupWithNameInData", 0, "refCollection");
		DataGroupSpy refCollection = (DataGroupSpy) collectionVar.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		refCollection.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				LINKED_RECORD_ID);
		return (String) refCollection.MCR.getReturnValue("getFirstAtomicValueWithNameInData", 0);
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

		recordStorage.MCR.assertParameter("read", nextReadCallNumber, "id", itemId);

		DataGroupSpy itemGroup = (DataGroupSpy) recordStorage.MCR.getReturnValue("read",
				nextReadCallNumber);
		itemGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "nameInData");
		String itemValue = (String) itemGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		possibleValues.add(itemValue);
	}

	@Test
	public void testGetRecordPartReadConstraintsWithAttributesListOfMetadataCollectionItemOnlyBuiltOnce() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 2;
		recordStorage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");
		recordStorage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		recordTypeHandler.getReadRecordPartConstraints();

		recordStorage.MCR.assertNumberOfCallsToMethod("read", 15);
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraintWithAttributeOLD() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");

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
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		// recordStorage.numberOfAttributes = 2;
		recordStorage.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");
		recordStorage.attributesIdsToAddToConstraint.add("textPartLangCollectionVar");

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
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 1;

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
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartReadConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartReadConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartReadConstraintsWithGreatGrandChildNOTMax1() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;
		recordStorage.maxNoOfGrandChildren = "3";

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordPartReadConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartReadConstraints);
	}

	@Test
	public void testRecursiveChildOnlyTraversedOnce() {
		DataGroupSpiderOldSpy dataGroup = new DataGroupSpiderOldSpy("dataGroupNameInData");
		dataGroup.addChild(dataGroup);

		setUpHandlerWithrecordStorageUsingTypeId("organisationRecursiveChild");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(recordStorage.types.size(), 5);
		assertEquals(recordStorage.ids.size(), 5);
		assertEquals(recordStorage.ids.get(0), "organisationRecursiveChild");
		assertEquals(recordStorage.ids.get(1), "organisationRecursiveChild");
		assertEquals(recordStorage.ids.get(2), "divaOrganisationRecursiveNameGroup");
		assertEquals(recordStorage.ids.get(3), "showInPortalTextVar");
		assertEquals(recordStorage.ids.get(4), "divaOrganisationRecursiveNameGroup");

		DataGroupSpiderOldSpy returnValueNameGroup = (DataGroupSpiderOldSpy) recordStorage.MCR
				.getReturnValue("read", 2);
		int recordInfoAndChildRefsRequested = 2;
		assertEquals(returnValueNameGroup.requestedDataGroups.size(),
				recordInfoAndChildRefsRequested);

		DataGroupSpiderOldSpy returnValueNameGroup2 = (DataGroupSpiderOldSpy) recordStorage.MCR
				.getReturnValue("read", 4);
		int onlyRecordInfoRequested = 1;
		assertEquals(returnValueNameGroup2.requestedDataGroups.size(), onlyRecordInfoRequested);
		assertTrue(recordPartReadConstraints.isEmpty());
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGrandChild() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 1;

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGreatGrandChild() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartWriteConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGreatGrandChildNOTMax1() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;
		recordStorage.maxNoOfGrandChildren = "3";

		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertEquals(recordPartWriteConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartWriteConstraints);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGrandChild() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 1;
		recordStorage.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGreatGrandChild() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;
		recordStorage.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 4);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
		assertConstraintExistWithNumberOfAttributes(recordPartCreateConstraints, "greatGrandChild",
				0);
	}

	@Test
	public void testGetRecordPartCreateWriteConstraintsWithGreatGrandChildNOTMax1() {
		setUpHandlerWithrecordStorageUsingTypeId("organisationChildWithAttribute");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfGrandChildrenWithReadWriteConstraint = 2;
		recordStorage.maxNoOfGrandChildren = "3";
		recordStorage.useStandardMetadataGroupForNew = true;

		Set<Constraint> recordPartCreateConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertEquals(recordPartCreateConstraints.size(), 3);

		assertCorrectConstraintsIncludingFirstLevelChild(recordPartCreateConstraints);
	}

	@Test
	public void testGetRecordPartReadConstraintsReturnsSameInstance() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(recordStorage);
	}

	private void assertReadStorageOnlyOnce(RecordTypeHandlerStorageOldSpy recordStorage) {
		recordStorage.MCR.assertNumberOfCallsToMethod("read", 5);
		assertParementerFirstOnParameterTypesList("read", 0, RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList("read", 1, METADATA);
		recordStorage.MCR.assertParameter("read", 1, "id", "organisation");
		assertParementerFirstOnParameterTypesList("read", 2, METADATA);
		recordStorage.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList("read", 3, METADATA);
		recordStorage.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList("read", 4, METADATA);
		recordStorage.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot");
	}

	@Test
	public void testGetRecordPartWriteConstraintsReturnsSameInstance() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(recordStorage);
	}

	@Test
	public void testGetRecordPartCreateConstraintsReturnsSameInstance() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;

		var constraints1 = recordTypeHandler.getCreateWriteRecordPartConstraints();
		var constraints2 = recordTypeHandler.getCreateWriteRecordPartConstraints();

		assertSame(constraints1, constraints2);

		recordStorage.MCR.assertNumberOfCallsToMethod("read", 5);
		assertParementerFirstOnParameterTypesList("read", 0, RECORD_TYPE);
		recordStorage.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList("read", 1, METADATA);
		recordStorage.MCR.assertParameter("read", 1, "id", "organisationNew");
		assertParementerFirstOnParameterTypesList("read", 2, METADATA);
		recordStorage.MCR.assertParameter("read", 2, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList("read", 3, METADATA);
		recordStorage.MCR.assertParameter("read", 3, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList("read", 4, METADATA);
		recordStorage.MCR.assertParameter("read", 4, "id", "divaOrganisationRoot2");
	}

	@Test
	public void testGetRecordPartReadConstraintsTwoReadConstraint() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");

		recordStorage.numberOfChildrenWithReadWriteConstraint = 2;
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
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 2;
		recordStorage.numberOfChildrenWithWriteConstraint = 1;

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
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		assertFalse(recordTypeHandler.hasRecordPartReadConstraint());
	}

	@Test
	public void testHasRecordPartReadConstraintsOneConstraints() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		assertTrue(recordTypeHandler.hasRecordPartReadConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsNoConstraints() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		assertFalse(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsOneReadConstraint() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsOneWriteConstraints() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 0;
		recordStorage.numberOfChildrenWithWriteConstraint = 1;

		assertTrue(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsNoConstraints() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");

		assertFalse(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsOneReadConstraint() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 1;
		recordStorage.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsOneWriteConstraints() {
		setUpHandlerWithrecordStorageUsingTypeId("organisation");
		recordStorage.numberOfChildrenWithReadWriteConstraint = 0;
		recordStorage.numberOfChildrenWithWriteConstraint = 1;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testIsNotSearch() {
		setUpHandlerForRecordTypeUsingId("notSearch");

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertFalse(isSearchType);
	}

	private void setUpHandlerForRecordTypeUsingId(String recordId) {
		DataRecordGroupSpy dataGroup = createDataRecordGroupWithId(recordId);
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroup);
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
		DataRecordGroupSpy dataGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId("search",
				"someSearchId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();
		assertTrue(hasSearch);

		dataGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
	}

	private void setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(DataRecordGroup dataGroup,
			String recordTypeId) {
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> dataGroup);
		setUpRecordTypeHandlerUsingTypeId(recordTypeId);
	}

	@Test
	public void testHasNoSearch() {
		DataRecordGroupSpy dataRecordGroup = createDataRecordGroupWithId("someId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataRecordGroup, "recordType");

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();
		assertFalse(hasSearch);

		dataRecordGroup.MCR.assertParameters("containsChildWithNameInData", 0, "search");
	}

	@Test
	public void testGetSearchId() {
		DataRecordGroupSpy dataRecordGroup = setupDataGroupWithLinkUsingNameInDataAndRecordId(
				"search", "someSearchId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataRecordGroup, "recordType");

		String searchId = recordTypeHandler.getSearchId();

		assertUsedLink(dataRecordGroup, "search", searchId);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to get searchId, no search exists")
	public void testGetSearchIdThrowsExceptionWhenMissing() {
		DataRecordGroupSpy dataGroup = createDataRecordGroupWithId("someId");

		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		recordTypeHandler.getSearchId();
	}

	private void assertParementerFirstOnParameterTypesList(String methodName, int callNumber,
			String recordType) {
		List<?> recordTypeList = (List<?>) recordStorage.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, callNumber, "types");
		assertEquals(recordTypeList.size(), 1);
		assertEquals(recordTypeList.get(0), recordType);
	}

	@Test
	public void testShouldStoreInArchive() {
		setupForStorageAtomicValue("storeInArchive", "false");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertShouldStoreInArchive(dataGroup, false);
	}

	private void assertShouldStoreInArchive(DataRecordGroupSpy dataRecordGroup, boolean expected) {
		assertEquals(recordTypeHandler.storeInArchive(), expected);
		dataRecordGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"storeInArchive");
	}

	@Test
	public void testStoreInArchiveFromDataGroup() {
		DataRecordGroupSpy dataRecordGroup = setupDataGroupWithAtomicValue("storeInArchive",
				"true");
		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataRecordGroup, "recordType");

		assertShouldStoreInArchive(dataRecordGroup, true);
	}

	@Test
	public void testStoreInArchiveTrue() {
		setupForStorageAtomicValue("storeInArchive", "true");
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		DataRecordGroupSpy dataRecordGroup = getRecordTypeDataRecordGroupReadFromStorage();
		assertShouldStoreInArchive(dataRecordGroup, true);
	}

	@Test
	public void testShouldStoreInArchiveFalseFromDataGroup() {
		DataRecordGroupSpy dataGroup = setupDataGroupWithAtomicValue("userSuppliedId", "false");
		setUpHandlerForRecordTypeUsingGroupAndRecordTypeId(dataGroup, "recordType");

		assertShouldStoreInArchive(dataGroup, false);
	}

	@Test
	public void testGetUniqueDefinitions_NoDefinitionsExists() {
		setUpRecordTypeHandlerUsingTypeId(SOME_ID);

		List<Unique> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions, Collections.emptyList());
		metadataStorageViewSpy.MCR.assertMethodNotCalled("getCollectTermHolder");
	}

	@Test
	public void testGetUniqueDefinitions_GetCollectTermsIfUniqueExists() {
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
	public void testGetUniqueDefinitions_OneDefinitionWithOutCombinesExists() {
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
	public void testGetUniqueDefinitions_OneDefinitionWithTwoCombinesExists() {
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
	public void testGetUniqueDefinitions_TwoDefinitionWithTwoCombinesExists() {
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

}
