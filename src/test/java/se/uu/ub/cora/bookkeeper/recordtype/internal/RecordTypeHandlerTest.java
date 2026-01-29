/*
 * Copyright 2016, 2019, 2020, 2021, 2022, 2024, 2025, 2026 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.idsource.internal.IdSourceProvider;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueIds;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueStorageKeys;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.initialize.spies.InitializedTypesSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerTest {
	private static final String METADATA = "metadata";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String RECORD_TYPE_ID = "someRecordTypeId";
	private DataFactorySpy dataFactorySpy;
	private RecordTypeHandler recordTypeHandler;
	private RecordStorageSpy recordStorageUsingDeprecatedRead;
	private OldRecordStorageSpy storage;
	private MetadataStorageViewSpy metadataStorageViewSpy;
	private RecordType recordType;
	private InitializedTypesSpy<IdSourceInstanceProvider> initializedTypes;
	private IdSourceSpy idSource;

	@BeforeMethod
	public void setUp() {
		setUpLoggerProvider();
		setUpDataFactoryProvider();
		setUpMetadataStorageProvider();
		setUpIdSourceProvider();

		recordStorageUsingDeprecatedRead = new RecordStorageSpy();
		recordStorageUsingDeprecatedRead.MRV.setDefaultReturnValuesSupplier("read",
				DataGroupSpy::new);

		createRecordType();
	}

	private void setUpLoggerProvider() {
		LoggerFactory loggerSpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerSpy);
	}

	private void setUpDataFactoryProvider() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
	}

	private void setUpIdSourceProvider() {
		initializedTypes = new InitializedTypesSpy<IdSourceInstanceProvider>();

		idSource = new IdSourceSpy();
		IdSourceInstanceProviderSpy idSourceInstanceProvider = new IdSourceInstanceProviderSpy();
		idSourceInstanceProvider.MRV.setDefaultReturnValuesSupplier("getIdSource", () -> idSource);

		initializedTypes.MRV.setDefaultReturnValuesSupplier("getImplementationByType",
				() -> idSourceInstanceProvider);
		IdSourceProvider.onlyForTestSetInitializedTypes(initializedTypes);
	}

	private void setUpMetadataStorageProvider() {
		metadataStorageViewSpy = new MetadataStorageViewSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType("someTypeToValidate", "someCreateDefinitionId",
						"someUpdateDefinitionId")));

		MetadataStorageViewInstanceProviderSpy metadataStorageProviderSpy = new MetadataStorageViewInstanceProviderSpy();
		metadataStorageProviderSpy.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageViewSpy);

		MetadataStorageProvider
				.onlyForTestSetMetadataStorageViewInstanceProvider(metadataStorageProviderSpy);
	}

	private void createRecordType() {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeId(String id) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(id, "someDefinitionId", Optional.empty(), "userSupplied",
				Optional.of("sequenceId"), Collections.emptyList(), isPublic, usePermissionUnit,
				useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeIdSource(String idSource) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(), idSource,
				Optional.of("sequenceId"), Collections.emptyList(), isPublic, usePermissionUnit,
				useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeUniqueIds(Collection<UniqueIds> uniqueIds) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), uniqueIds, isPublic, usePermissionUnit,
				useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeSearchId(Optional<String> searchId) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", searchId, "userSupplied",
				Optional.of("sequenceId"), Collections.emptyList(), isPublic, usePermissionUnit,
				useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeIsPublic(boolean isPublic) {
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeUsePermissionUnit(boolean usePermissionUnit) {
		boolean isPublic = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeUseVisibility(boolean useVisibility) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeUseTrashBin(boolean useTrashBin) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean storeInArchive = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void createRecordTypeStoreInArchive(boolean storeInArchive) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		recordType = new RecordType(RECORD_TYPE_ID, "someDefinitionId", Optional.empty(),
				"userSupplied", Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "ValidationType with id: someValidationTypeId, does not exist.")
	public void testInitWithValidationTypeNotFoundThrowsValidationException() {
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				Optional::empty);

		setUpRecordTypeHandlerWithValidationType();
	}

	private void setUpRecordTypeHandlerWithValidationType() {
		recordTypeHandler = RecordTypeHandlerImp.usingHandlerFactoryRecordStorageValidationTypeId(
				recordType, recordStorageUsingDeprecatedRead, "someValidationTypeId");
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
		recordTypeHandler = RecordTypeHandlerImp.usingHandlerFactoryRecordStorageValidationTypeId(
				recordType, recordStorageUsingDeprecatedRead, "someValidationTypeId");
	}

	@Test
	public void testShouldAutoGenerateId() {
		createRecordTypeIdSource("timestamp");
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		boolean shouldAutoGenerateId = recordTypeHandler.shouldAutoGenerateId();

		assertEquals(shouldAutoGenerateId, true);
	}

	@Test
	public void testShouldAutoGenerateIdFalse() {
		createRecordTypeIdSource("userSupplied");
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		boolean shouldAutoGenerateId = recordTypeHandler.shouldAutoGenerateId();

		assertEquals(shouldAutoGenerateId, false);
	}

	@Test
	public void testGetNextId() {
		createRecordTypeIdSource("someType");
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		String id = recordTypeHandler.getNextId();

		initializedTypes.MCR.assertCalledParametersReturn("getImplementationByType",
				recordType.idSource());
		idSource.MCR.assertReturn("getId", 0, id);
	}

	@Test
	public void testCreateDefinitionId() {
		setUpRecordTypeHandlerWithValidationType();

		String definitionId = recordTypeHandler.getCreateDefinitionId();

		assertEquals(definitionId, "someCreateDefinitionId");
	}

	@Test
	public void testGetUpdateDefinitionId() {
		setUpRecordTypeHandlerWithValidationType();

		String definitionId = recordTypeHandler.getUpdateDefinitionId();

		assertEquals(definitionId, "someUpdateDefinitionId");
	}

	@Test
	public void testGetDefinitionId() {
		setupForLinkForStorageWithNameInDataAndRecordId("metadataId", "someMetadataId");
		setUpRecordTypeHandlerUsingTypeId("someRecordId");

		String metadataId = recordTypeHandler.getDefinitionId();

		assertEquals(metadataId, "someDefinitionId");
	}

	private void setupForLinkForStorageWithNameInDataAndRecordId(String linkNameInData,
			String linkedRecordId) {
		DataGroupSpy dataGroup = setupForLinkWithNameInDataAndRecordId(linkNameInData,
				linkedRecordId);
		recordStorageUsingDeprecatedRead.MRV.setDefaultReturnValuesSupplier("read",
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

	@Test
	public void testPublic() {
		createRecordTypeIsPublic(true);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.isPublicForRead(), true);
	}

	@Test
	public void testPublicFalse() {
		createRecordTypeIsPublic(false);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.isPublicForRead(), false);
	}

	@Test
	public void testUseVisibilityTrue() {
		createRecordTypeUseVisibility(true);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.useVisibility(), true);
	}

	@Test
	public void testUseVisibilityFalse() {
		createRecordTypeUseVisibility(false);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.useVisibility(), false);
	}

	@Test
	public void testUseTrashBinTrue() {
		createRecordTypeUseTrashBin(true);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.useTrashBin(), true);
	}

	@Test
	public void testUseTrashBinFalse() {
		createRecordTypeUseTrashBin(false);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.useTrashBin(), false);
	}

	@Test
	public void testUsePermissionUnitTrue() {
		createRecordTypeUsePermissionUnit(true);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.usePermissionUnit(), true);
	}

	@Test
	public void testUsePermissionUnitFalse() {
		createRecordTypeUsePermissionUnit(false);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.usePermissionUnit(), false);
	}

	@Test
	public void testShouldStoreInArchive() {
		createRecordTypeStoreInArchive(true);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.storeInArchive(), true);
	}

	@Test
	public void testStoreInArchiveTrue() {
		createRecordTypeStoreInArchive(false);
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		assertEquals(recordTypeHandler.storeInArchive(), false);
	}

	@Test
	public void testGetRecordPartReadConstraintsNOReadConstraint() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 3);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, METADATA);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "divaOrganisationNameGroup");

		assertTrue(recordPartReadConstraints.isEmpty());
		Set<Constraint> recordPartWriteConstraints = recordTypeHandler
				.getUpdateWriteRecordPartConstraints();
		assertTrue(recordPartWriteConstraints.isEmpty());

		Set<Constraint> recordPartCreateWriteConstraints = recordTypeHandler
				.getCreateWriteRecordPartConstraints();
		assertTrue(recordPartCreateWriteConstraints.isEmpty());
	}

	private void assertParementerFirstOnParameterTypesList(OldRecordStorageSpy storage,
			String methodName, int callNumber, String recordType) {
		List<?> recordTypeList = (List<?>) storage.MCR
				.getParameterForMethodAndCallNumberAndParameter(methodName, callNumber, "types");
		assertEquals(recordTypeList.size(), 1);
		assertEquals(recordTypeList.get(0), recordType);
	}

	private OldRecordStorageSpy setUpHandlerWithStorageSpyUsingTypeId(String recordTypeId) {
		// TODO: not the best way to set definitionId
		recordType = new RecordType(RECORD_TYPE_ID, recordTypeId, Optional.empty(),
				recordType.idSource(), recordType.sequenceId(), Collections.emptyList(),
				recordType.isPublic(), recordType.usePermissionUnit(), recordType.useVisibility(),
				recordType.useTrashBin(), recordType.storeInArchive());

		storage = new OldRecordStorageSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional
						.of(new ValidationType(recordTypeId, recordTypeId + "New", recordTypeId)));
		recordTypeHandler = RecordTypeHandlerImp.usingHandlerFactoryRecordStorageValidationTypeId(
				recordType, storage, "someValidationTypeId");
		return storage;
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraint() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
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

		storage = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storage.numberOfChildrenWithReadWriteConstraint = 1;
		storage.attributesIdsToAddToConstraint.add("textPartTypeCollectionVar");
		storage.attributesIdsToAddToConstraint.add("textPartLangCollectionVar");

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
		assertConstraintWithAttribute(1, "organisationAlternativeName");
		assertConstraintAttribute(1, "textPartTypeCollectionVar", 5, "default", 0);
		assertConstraintAttribute(1, "textPartLangCollectionVar", 6, "sv", 1);
	}

	private void assertConstraintWithAttribute(int constraintNumber, String constraintNameInData) {
		dataFactorySpy.MCR.assertParameters("factorDataChildFilterUsingNameInData",
				constraintNumber, constraintNameInData);
	}

	private void assertConstraintAttribute(int constraintNumber, String metadataIdForAttribute,
			int attributeReadNumber, String possibleValuesForAttribute,
			int attributeNumberInConstraint) {
		assertParementerFirstOnParameterTypesList(storage, "read", attributeReadNumber,
				"metadataCollectionVariable");
		storage.MCR.assertParameter("read", attributeReadNumber, "id", metadataIdForAttribute);

		DataGroupSpiderOldSpy secondAttributecollectionVarSpy = (DataGroupSpiderOldSpy) storage.MCR
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
		storage = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storage.numberOfChildrenWithReadWriteConstraint = 1;
		storage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		Set<Constraint> constraints = recordTypeHandler.getReadRecordPartConstraints();

		storage.MCR.assertNumberOfCallsToMethod("read", 9);
		assertParementerFirstOnParameterTypesList(storage, "read", 0, METADATA);
		storage.MCR.assertParameter("read", 0, "id", "organisationChildWithAttribute");
		assertParementerFirstOnParameterTypesList(storage, "read", 1, METADATA);
		storage.MCR.assertParameter("read", 1, "id", "divaOrganisationNameGroup");

		assertParementerFirstOnParameterTypesList(storage, "read", 2, METADATA);
		storage.MCR.assertParameter("read", 2, "id", "showInPortalTextVar");

		assertParementerFirstOnParameterTypesList(storage, "read", 3, METADATA);
		storage.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot");
		assertParementerFirstOnParameterTypesList(storage, "read", 4, METADATA);
		storage.MCR.assertParameter("read", 4, "id", "organisationAlternativeNameGroup");
		assertParementerFirstOnParameterTypesList(storage, "read", 5, "metadataCollectionVariable");
		storage.MCR.assertParameter("read", 5, "id", "choosableAttributeCollectionVar");

		assertParementerFirstOnParameterTypesList(storage, "read", 6, METADATA);
		storage.MCR.assertParameter("read", 6, "id", "choosableCollection");
		storage.MCR.assertParameter("read", 7, "id", "choosableCollectionItem1");
		storage.MCR.assertParameterAsEqual("read", 7, "types", List.of("metadata"));
		storage.MCR.assertParameter("read", 8, "id", "choosableCollectionItem2");
		storage.MCR.assertParameterAsEqual("read", 8, "types", List.of("metadata"));

		List<DataGroupSpy> allItemRefs = assertCollectionItemReferences();

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

	private List<DataGroupSpy> assertCollectionItemReferences() {
		DataGroupSpy possibleAttributesCollection = (DataGroupSpy) storage.MCR
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

	private Collection<String> assertItemValuesAndGetAllPossibleValues(
			List<DataGroupSpy> allItemRefs) {
		assertEquals(allItemRefs.size(), 2);
		return assertItemValues(allItemRefs);
	}

	private Collection<String> assertItemValues(List<DataGroupSpy> allItemRefs) {
		Collection<String> possibleValues = new ArrayList<>();
		int nextReadCallNumber = 7;
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

		storage.MCR.assertParameter("read", nextReadCallNumber, "id", itemId);

		DataGroupSpy itemGroup = (DataGroupSpy) storage.MCR.getReturnValue("read",
				nextReadCallNumber);
		itemGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "nameInData");
		String itemValue = (String) itemGroup.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		possibleValues.add(itemValue);
	}

	@Test
	public void testGetRecordPartReadConstraintsWithAttributesListOfMetadataCollectionItemOnlyBuiltOnce() {
		storage = setUpHandlerWithStorageSpyUsingTypeId("organisationChildWithAttribute");
		storage.numberOfChildrenWithReadWriteConstraint = 2;
		storage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");
		storage.attributesIdsToAddToConstraint.add("choosableAttributeCollectionVar");

		recordTypeHandler.getReadRecordPartConstraints();

		storage.MCR.assertNumberOfCallsToMethod("read", 14);
	}

	@Test
	public void testGetRecordPartReadConstraintsOneReadConstraintWithAttributeOLD() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationChildWithAttribute");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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

		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
				"organisationRecursiveChild");

		Set<Constraint> recordPartReadConstraints = recordTypeHandler
				.getReadRecordPartConstraints();
		assertEquals(storageSpy.types.size(), 4);
		assertEquals(storageSpy.ids.size(), 4);
		assertEquals(storageSpy.ids.get(0), "organisationRecursiveChild");
		assertEquals(storageSpy.ids.get(1), "divaOrganisationRecursiveNameGroup");
		assertEquals(storageSpy.ids.get(2), "showInPortalTextVar");
		assertEquals(storageSpy.ids.get(3), "divaOrganisationRecursiveNameGroup");

		DataGroupSpiderOldSpy returnValueNameGroup = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 1);
		int recordInfoAndChildRefsRequested = 2;
		assertEquals(returnValueNameGroup.requestedDataGroups.size(),
				recordInfoAndChildRefsRequested);

		DataGroupSpiderOldSpy returnValueNameGroup2 = (DataGroupSpiderOldSpy) storageSpy.MCR
				.getReturnValue("read", 3);
		int onlyRecordInfoRequested = 1;
		assertEquals(returnValueNameGroup2.requestedDataGroups.size(), onlyRecordInfoRequested);
		assertTrue(recordPartReadConstraints.isEmpty());
	}

	@Test
	public void testGetRecordPartReadWriteConstraintsWithGrandChild() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId(
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getReadRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(storageSpy);
	}

	private void assertReadStorageOnlyOnce(OldRecordStorageSpy storageSpy) {
		storageSpy.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, METADATA);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisation");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
		storageSpy.MCR.assertParameter("read", 2, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, METADATA);
		storageSpy.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot");
	}

	@Test
	public void testGetRecordPartWriteConstraintsReturnsSameInstance() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		recordTypeHandler.getUpdateWriteRecordPartConstraints();
		recordTypeHandler.getUpdateWriteRecordPartConstraints();

		assertReadStorageOnlyOnce(storageSpy);
	}

	@Test
	public void testGetRecordPartCreateConstraintsReturnsSameInstance() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storageSpy.numberOfChildrenWithReadWriteConstraint = 1;

		var constraints1 = recordTypeHandler.getCreateWriteRecordPartConstraints();
		var constraints2 = recordTypeHandler.getCreateWriteRecordPartConstraints();

		assertSame(constraints1, constraints2);

		storageSpy.MCR.assertNumberOfCallsToMethod("read", 4);
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 0, METADATA);
		storageSpy.MCR.assertParameter("read", 0, "id", "organisationNew");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 1, METADATA);
		storageSpy.MCR.assertParameter("read", 1, "id", "divaOrganisationNameGroup");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 2, METADATA);
		storageSpy.MCR.assertParameter("read", 2, "id", "showInPortalTextVar");
		assertParementerFirstOnParameterTypesList(storageSpy, "read", 3, METADATA);
		storageSpy.MCR.assertParameter("read", 3, "id", "divaOrganisationRoot2");
	}

	@Test
	public void testGetRecordPartReadConstraintsTwoReadConstraint() {
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");

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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
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
		OldRecordStorageSpy storageSpy = setUpHandlerWithStorageSpyUsingTypeId("organisation");
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
		storage.numberOfChildrenWithReadWriteConstraint = 1;
		storage.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartWriteConstraint());
	}

	@Test
	public void testHasRecordPartWriteConstraintsOneWriteConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storage.numberOfChildrenWithReadWriteConstraint = 0;
		storage.numberOfChildrenWithWriteConstraint = 1;

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
		storage.numberOfChildrenWithReadWriteConstraint = 1;
		storage.numberOfChildrenWithWriteConstraint = 0;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testHasRecordPartCreateConstraintsOneWriteConstraints() {
		setUpHandlerWithStorageSpyUsingTypeId("organisation");
		storage.numberOfChildrenWithReadWriteConstraint = 0;
		storage.numberOfChildrenWithWriteConstraint = 1;

		assertTrue(recordTypeHandler.hasRecordPartCreateConstraint());
	}

	@Test
	public void testIsNotSearch() {
		createRecordTypeId("notSearch");
		setUpRecordTypeHandlerWithValidationType();

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertFalse(isSearchType);
	}

	@Test
	public void testIsSearch() {
		createRecordTypeId("search");
		setUpRecordTypeHandlerWithValidationType();

		boolean isSearchType = recordTypeHandler.representsTheRecordTypeDefiningSearches();

		assertTrue(isSearchType);
	}

	@Test
	public void testIsNotRecordType() {
		createRecordTypeId("NOT_recordType");
		setUpRecordTypeHandlerWithValidationType();

		boolean isRecordType = recordTypeHandler.representsTheRecordTypeDefiningRecordTypes();

		assertFalse(isRecordType);
	}

	@Test
	public void testIsRecordType() {
		createRecordTypeId("recordType");
		setUpRecordTypeHandlerWithValidationType();

		boolean isRecordType = recordTypeHandler.representsTheRecordTypeDefiningRecordTypes();

		assertTrue(isRecordType);
	}

	@Test
	public void testHasSearch() {
		createRecordTypeSearchId(Optional.of("someSearchId"));
		setUpRecordTypeHandlerWithValidationType();

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();
		assertTrue(hasSearch);

	}

	@Test
	public void testHasNoSearch() {
		createRecordTypeSearchId(Optional.empty());
		setUpRecordTypeHandlerWithValidationType();

		boolean hasSearch = recordTypeHandler.hasLinkedSearch();

		assertFalse(hasSearch);
	}

	@Test
	public void testGetSearchId() {
		createRecordTypeSearchId(Optional.of("someSearchId"));
		setUpRecordTypeHandlerWithValidationType();

		String searchId = recordTypeHandler.getSearchId();

		assertEquals(searchId, "someSearchId");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to get searchId, no search exists")
	public void testGetSearchIdThrowsExceptionWhenMissing() {
		createRecordTypeSearchId(Optional.empty());
		setUpRecordTypeHandlerWithValidationType();

		recordTypeHandler.getSearchId();
	}

	@Test
	public void testGetUniqueDefinitions_NoDefinitionsExists() {
		createRecordTypeUniqueIds(Collections.emptyList());
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		List<UniqueStorageKeys> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions, Collections.emptyList());
	}

	@Test
	public void testGetUniqueDefinitions_OneDefinitionWithOutCombinesExists() {
		UniqueIds uniqueIds = new UniqueIds("uniqueTermLinkId", Collections.emptySet());
		createRecordTypeUniqueIds(List.of(uniqueIds));
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermLinkId");

		List<UniqueStorageKeys> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions.size(), 1);
		assertEquals(uniqueDefinitions.get(0).uniqueTermStorageKey(), "uniqueTermLinkIdStorageKey");
		assertEquals(uniqueDefinitions.get(0).combineTermStorageKeys(), Collections.emptySet());
	}

	@Test
	public void testGetUniqueDefinitions_OneDefinitionWithTwoCombinesExists() {
		UniqueIds uniqueIds = new UniqueIds("uniqueTermLinkId",
				Set.of("combineTermId1", "combineTermId2"));
		createRecordTypeUniqueIds(List.of(uniqueIds));
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermLinkId", "combineTermId1",
				"combineTermId2");

		List<UniqueStorageKeys> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

		assertEquals(uniqueDefinitions.size(), 1);
		assertEquals(uniqueDefinitions.get(0).uniqueTermStorageKey(), "uniqueTermLinkIdStorageKey");
		assertEquals(uniqueDefinitions.get(0).combineTermStorageKeys(),
				Set.of("combineTermId1StorageKey", "combineTermId2StorageKey"));
	}

	@Test
	public void testGetUniqueDefinitions_TwoDefinitionWithTwoCombinesExists() {
		UniqueIds uniqueIdsA = new UniqueIds("uniqueTermIdA",
				Set.of("combineTermIdA1", "combineTermIdA2"));
		UniqueIds uniqueIdsB = new UniqueIds("uniqueTermIdB",
				Set.of("combineTermIdB1", "combineTermIdB2"));

		createRecordTypeUniqueIds(List.of(uniqueIdsA, uniqueIdsB));
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);
		setUpMetadataStorageViewWithCollectTermsList("uniqueTermIdA", "combineTermIdA1",
				"combineTermIdA2", "uniqueTermIdB", "combineTermIdB1", "combineTermIdB2");

		List<UniqueStorageKeys> uniqueDefinitions = recordTypeHandler.getUniqueDefinitions();

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
	public void testOnlyForTests() {
		setUpRecordTypeHandlerUsingTypeId(RECORD_TYPE_ID);

		RecordTypeHandlerImp recordTypeHandlerImp = (RecordTypeHandlerImp) recordTypeHandler;
		assertEquals(recordTypeHandlerImp.onlyForTestGetRecordType(), recordType);
	}
}
