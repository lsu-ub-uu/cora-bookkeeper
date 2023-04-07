/*
 * Copyright 2016, 2019, 2020, 2021 Uppsala University Library
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactory;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class RecordTypeHandlerImp implements RecordTypeHandler {
	private static final String METADATA_GROUP = "metadataGroup";
	private static final String REPEAT_MAX_WHEN_NOT_REPEATEBLE = "1";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String SEARCH = "search";
	private static final String PARENT_ID = "parentId";
	private static final String RECORD_PART_CONSTRAINT = "recordPartConstraint";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String RECORD_TYPE = "recordType";
	private DataGroup recordType;
	private String recordTypeId;
	private RecordStorage recordStorage;
	private DataGroup metadataGroup;
	private Set<Constraint> readWriteConstraints = new LinkedHashSet<>();
	private Set<Constraint> createConstraints = new LinkedHashSet<>();
	private Set<Constraint> writeConstraints = new LinkedHashSet<>();
	private boolean constraintsForUpdateLoaded = false;
	private boolean constraintsForCreateLoaded = false;
	private RecordTypeHandlerFactory recordTypeHandlerFactory;
	private Set<String> readChildren = new HashSet<>();
	private List<String> metadataCollectionItemTypes;
	private MetadataStorageView metadataStorageView;
	private String validationTypeId;
	private ValidationType validationType;

	RecordTypeHandlerImp() {
		// only for test
	}

	/**
	 * @Deprecated use usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId instead
	 */
	@Deprecated(forRemoval = true)
	public static RecordTypeHandler usingRecordStorageAndRecordTypeId(
			RecordTypeHandlerFactory recordTypeHandlerFactory, RecordStorage recordStorage,
			String recordTypeId) {
		return new RecordTypeHandlerImp(recordTypeHandlerFactory, recordStorage, recordTypeId);
	}

	/**
	 * @Deprecated use usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId instead
	 */
	@Deprecated(forRemoval = true)
	public static RecordTypeHandler usingRecordStorageAndDataGroup(
			RecordTypeHandlerFactory recordTypeHandlerFactory, RecordStorage recordStorage,
			DataGroup dataGroup) {
		return new RecordTypeHandlerImp(recordTypeHandlerFactory, recordStorage, dataGroup);
	}

	public static RecordTypeHandler usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
			RecordTypeHandlerFactory recordTypeHandlerFactory, RecordStorage recordStorage,
			MetadataStorageView metadataStorageView, String validationTypeId) {

		return new RecordTypeHandlerImp(recordTypeHandlerFactory, recordStorage,
				metadataStorageView, validationTypeId);
	}

	private RecordTypeHandlerImp(RecordTypeHandlerFactory recordTypeHandlerFactory,
			RecordStorage recordStorage, String recordTypeId) {
		this.recordTypeHandlerFactory = recordTypeHandlerFactory;
		this.recordStorage = recordStorage;
		this.recordTypeId = recordTypeId;
		recordType = recordStorage.read(List.of(RECORD_TYPE), recordTypeId);
	}

	private RecordTypeHandlerImp(RecordTypeHandlerFactory recordTypeHandlerFactory,
			RecordStorage recordStorage, DataGroup dataGroup) {
		this.recordTypeHandlerFactory = recordTypeHandlerFactory;
		this.recordStorage = recordStorage;
		recordType = dataGroup;
		recordTypeId = getIdFromMetadatagGroup(dataGroup);
	}

	public RecordTypeHandlerImp(RecordTypeHandlerFactory recordTypeHandlerFactory,
			RecordStorage recordStorage, MetadataStorageView metadataStorageView,
			String validationTypeId) {
		this.recordTypeHandlerFactory = recordTypeHandlerFactory;
		this.recordStorage = recordStorage;
		this.metadataStorageView = metadataStorageView;
		this.validationTypeId = validationTypeId;
		Optional<ValidationType> oValidationType = metadataStorageView
				.getValidationType(validationTypeId);
		if (oValidationType.isEmpty()) {
			throw DataValidationException.withMessage(
					"ValidationType with id: " + validationTypeId + ", does not exist.");
		}

		validationType = oValidationType.get();
		recordType = recordStorage.read(List.of(RECORD_TYPE),
				validationType.validatesRecordTypeId());
		recordTypeId = getIdFromMetadatagGroup(recordType);

	}

	@Override
	public boolean isAbstract() {
		String abstractInRecordTypeDefinition = getAbstractFromRecordTypeDefinition();
		return "true".equals(abstractInRecordTypeDefinition);
	}

	private String getAbstractFromRecordTypeDefinition() {
		return recordType.getFirstAtomicValueWithNameInData("abstract");
	}

	@Override
	public boolean shouldAutoGenerateId() {
		String userSuppliedId = recordType.getFirstAtomicValueWithNameInData("userSuppliedId");
		return "false".equals(userSuppliedId);
	}

	@Override
	public String getCreateDefinitionId() {
		// TODO: exception not tested should be removed during this big change when depricated
		// constructors are removed
		if (null == validationType) {
			throw new RuntimeException(
					"Validation type probably loaded through depricated constructor,"
							+ " this method is not expected to be called when loaded that way");
		}
		return validationType.createDefinitionId();
	}

	@Override
	public String getUpdateDefinitionId() {
		// TODO: exception not tested should be removed during this big change when depricated
		// constructors are removed
		if (null == validationType) {
			throw new RuntimeException(
					"Validation type probably loaded through depricated constructor,"
							+ " this method is not expected to be called when loaded that way");
		}
		return validationType.updateDefinitionId();
	}

	@Override
	public String getDefinitionId() {
		DataRecordLink metadataLink = (DataRecordLink) recordType
				.getFirstChildWithNameInData("metadataId");
		return metadataLink.getLinkedRecordId();
	}

	@Override
	public List<String> getCombinedIdsUsingRecordId(String recordId) {
		List<String> ids = new ArrayList<>();
		ids.add(recordTypeId + "_" + recordId);
		possiblyCreateIdForAbstractType(recordId, recordType, ids);
		return ids;
	}

	private void possiblyCreateIdForAbstractType(String recordId, DataGroup recordTypeDefinition,
			List<String> ids) {
		if (hasParent()) {
			createIdAsAbstractType(recordId, recordTypeDefinition, ids);
		}
	}

	private void createIdAsAbstractType(String recordId, DataGroup recordTypeDefinition,
			List<String> ids) {
		String abstractParentType = getParentRecordType(recordTypeDefinition);
		DataGroup parentGroup = recordStorage.read(List.of(RECORD_TYPE), abstractParentType);
		RecordTypeHandler recordTypeHandlerParent = recordTypeHandlerFactory
				.factorUsingDataGroup(parentGroup);
		ids.addAll(recordTypeHandlerParent.getCombinedIdsUsingRecordId(recordId));
	}

	private String getParentRecordType(DataGroup recordTypeDefinition) {
		DataRecordLink metadataLink = (DataRecordLink) recordTypeDefinition
				.getFirstChildWithNameInData(PARENT_ID);
		return metadataLink.getLinkedRecordId();
	}

	@Override
	public boolean isPublicForRead() {
		String isPublic = recordType.getFirstAtomicValueWithNameInData("public");
		return "true".equals(isPublic);
	}

	@Override
	public DataGroup getMetadataGroup() {
		if (metadataGroup == null) {
			metadataGroup = recordStorage.read(List.of(METADATA_GROUP), getDefinitionId());
		}
		return metadataGroup;
	}

	@Override
	public Set<Constraint> getReadRecordPartConstraints() {
		if (constraintsForUpdateNotLoaded()) {
			collectAllConstraintsForUpdate();
		}
		return readWriteConstraints;
	}

	private boolean constraintsForUpdateNotLoaded() {
		return !constraintsForUpdateLoaded;
	}

	private void collectAllConstraintsForUpdate() {
		constraintsForUpdateLoaded = true;
		List<DataGroup> allChildReferences = getAllChildReferences(getMetadataGroup());
		Set<Constraint> collectedConstraints = new LinkedHashSet<>();
		collectConstraintsForChildReferences(allChildReferences, collectedConstraints);
		for (Constraint constraint : collectedConstraints) {
			writeConstraints.add(constraint);
			possiblyAddReadWriteConstraint(constraint);
		}
	}

	private void collectConstraintsForChildReferences(List<DataGroup> allChildReferences,
			Set<Constraint> tempSet) {
		for (DataGroup childReference : allChildReferences) {
			collectConstraintForChildReference(childReference, tempSet);
		}
	}

	private void collectConstraintForChildReference(DataGroup childReference,
			Set<Constraint> tempSet) {
		DataGroup childRef = null;
		if (hasConstraints(childReference)) {
			childRef = readChildRefFromStorage(childReference);
			addWriteAndReadWriteConstraints(childReference, childRef, tempSet);
		}
		possiblyCollectConstraintsFromChildrenToChildReference(childReference, childRef, tempSet);
	}

	private void possiblyCollectConstraintsFromChildrenToChildReference(DataGroup childReference,
			DataGroup childRef, Set<Constraint> tempSet) {
		String repeatMax = getRepeatMax(childReference);
		String linkedRecordType = getLinkedRecordType(childReference);
		if (isGroup(linkedRecordType) && notRepetable(repeatMax)) {
			childRef = ensureChildRefReadFromStorage(childReference, childRef);
			List<DataGroup> allChildReferences = getAllChildReferences(childRef);
			collectConstraintsForChildReferences(allChildReferences, tempSet);
		}
	}

	private String getRepeatMax(DataGroup childReference) {
		return childReference.getFirstAtomicValueWithNameInData("repeatMax");
	}

	private String getLinkedRecordType(DataGroup childReference) {
		DataGroup ref = childReference.getFirstGroupWithNameInData("ref");
		return ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
	}

	private boolean isGroup(String linkedRecordType) {
		return METADATA_GROUP.equals(linkedRecordType);
	}

	private boolean notRepetable(String repeatMax) {
		return REPEAT_MAX_WHEN_NOT_REPEATEBLE.equals(repeatMax);
	}

	private DataGroup ensureChildRefReadFromStorage(DataGroup childReference, DataGroup childRef) {
		if (childRef == null) {
			childRef = readChildRefFromStorage(childReference);
		}
		return childRef;
	}

	private List<DataGroup> getAllChildReferences(DataGroup metadataGroupForMetadata) {
		String id = getIdFromMetadatagGroup(metadataGroupForMetadata);
		if (childrenToGroupHasAlreadyBeenChecked(id)) {
			return Collections.emptyList();
		}
		return addGroupToCheckedAndGetChildReferences(metadataGroupForMetadata, id);
	}

	private boolean childrenToGroupHasAlreadyBeenChecked(String id) {
		return readChildren.contains(id);
	}

	private List<DataGroup> addGroupToCheckedAndGetChildReferences(
			DataGroup metadataGroupForMetadata, String id) {
		readChildren.add(id);
		DataGroup childReferences = metadataGroupForMetadata
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference");
	}

	private String getIdFromMetadatagGroup(DataGroup metadataGroupForMetadata) {
		DataGroup recordInfo = metadataGroupForMetadata.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private boolean hasConstraints(DataGroup childReference) {
		return childReference.containsChildWithNameInData(RECORD_PART_CONSTRAINT);
	}

	private void addWriteAndReadWriteConstraints(DataGroup childReference, DataGroup childRef,
			Set<Constraint> constraints) {
		String constraintType = getRecordPartConstraintType(childReference);

		Constraint constraint = createConstraintPossibyAddAttributes(childRef);
		constraint.setType(ConstraintType.fromString(constraintType));
		constraints.add(constraint);
	}

	private String getRecordPartConstraintType(DataGroup childReference) {
		return childReference.getFirstAtomicValueWithNameInData(RECORD_PART_CONSTRAINT);
	}

	private Constraint createConstraintPossibyAddAttributes(DataGroup childRef) {
		Constraint constraint = createConstraint(childRef);
		possiblyAddAttributes(childRef, constraint);
		return constraint;
	}

	private DataGroup readChildRefFromStorage(DataGroup childReference) {
		DataGroup ref = childReference.getFirstGroupWithNameInData("ref");
		String linkedRecordType = ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		String linkedRecordId = ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		return recordStorage.read(List.of(linkedRecordType), linkedRecordId);
	}

	private Constraint createConstraint(DataGroup childRef) {
		String refNameInData = childRef.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		return new Constraint(refNameInData);
	}

	private void possiblyAddAttributes(DataGroup childRef, Constraint constraint) {
		if (childRef.containsChildWithNameInData("attributeReferences")) {
			addAttributes(childRef, constraint);
		}
	}

	private void addAttributes(DataGroup childRef, Constraint constraint) {
		DataGroup attributeReferences = childRef.getFirstGroupWithNameInData("attributeReferences");
		List<DataGroup> attributeRefs = attributeReferences.getAllGroupsWithNameInData("ref");

		for (DataGroup attributeRef : attributeRefs) {
			addAttributeToConstraintForAttributeRef(constraint, attributeRef);
		}
	}

	private void addAttributeToConstraintForAttributeRef(Constraint constraint,
			DataGroup attributeRef) {
		DataGroup collectionVar = getCollectionVarFromStorageForAttributeRef(attributeRef);
		if (collectionVar.containsChildWithNameInData("finalValue")) {
			addFinalValueAttribute(constraint, collectionVar);
		} else {
			addMultivalueAttribute(constraint, collectionVar);
		}
	}

	private DataGroup getCollectionVarFromStorageForAttributeRef(DataGroup attribute) {
		return recordStorage.read(
				List.of(attribute.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE)),
				attribute.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID));
	}

	private void addFinalValueAttribute(Constraint constraint, DataGroup collectionVar) {
		String attributeName = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		String attributeValue = collectionVar.getFirstAtomicValueWithNameInData("finalValue");
		constraint.addAttributeUsingNameInDataAndPossibleValues(attributeName,
				Set.of(attributeValue));
	}

	private void addMultivalueAttribute(Constraint constraint, DataGroup collectionVar) {
		String attributeName = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		Set<String> possibleAttributeValues = getPossibleAttributeValues(collectionVar);
		constraint.addAttributeUsingNameInDataAndPossibleValues(attributeName,
				possibleAttributeValues);
	}

	private Set<String> getPossibleAttributeValues(DataGroup collectionVar) {
		DataGroup refCollectionLink = collectionVar.getFirstGroupWithNameInData("refCollection");
		String collectionId = refCollectionLink.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		DataGroup possibleAttributesCollection = recordStorage
				.read(List.of("metadataItemCollection"), collectionId);
		DataGroup collectionItemReferences = possibleAttributesCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<DataGroup> allItemRefs = collectionItemReferences.getAllGroupsWithNameInData("ref");

		Set<String> possibleValues = new LinkedHashSet<>();
		loadTypesForMetadataCollectionItemGroup();
		for (DataGroup itemRef : allItemRefs) {
			String itemId = itemRef.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
			DataGroup itemGroup = recordStorage.read(metadataCollectionItemTypes, itemId);
			String itemValue = itemGroup.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
			possibleValues.add(itemValue);
		}
		return possibleValues;
	}

	private void loadTypesForMetadataCollectionItemGroup() {
		if (metadataCollectionItemTypes == null) {
			DataGroup metadataCollectionItemGroup = recordStorage.read(List.of(RECORD_TYPE),
					"metadataCollectionItem");
			RecordTypeHandler recordTypeHandlerMetadataCollectionItem = recordTypeHandlerFactory
					.factorUsingDataGroup(metadataCollectionItemGroup);
			metadataCollectionItemTypes = recordTypeHandlerMetadataCollectionItem
					.getListOfImplementingRecordTypeIds();
		}
	}

	private void possiblyAddReadWriteConstraint(Constraint constraint) {
		if (isReadWriteConstraint(constraint.getType())) {
			readWriteConstraints.add(constraint);
		}
	}

	private boolean isReadWriteConstraint(ConstraintType constraintType) {
		return ConstraintType.READ_WRITE == constraintType;
	}

	@Override
	public boolean hasRecordPartReadConstraint() {
		return !getReadRecordPartConstraints().isEmpty();
	}

	@Override
	public boolean hasRecordPartWriteConstraint() {
		return hasRecordPartReadConstraint() || !getUpdateWriteRecordPartConstraints().isEmpty();
	}

	@Override
	public Set<Constraint> getUpdateWriteRecordPartConstraints() {
		if (constraintsForUpdateNotLoaded()) {
			collectAllConstraintsForUpdate();
		}
		return writeConstraints;
	}

	@Override
	public String getRecordTypeId() {
		// needed for test
		return recordTypeId;
	}

	@Override
	public boolean hasParent() {
		return recordType.containsChildWithNameInData(PARENT_ID);
	}

	@Override
	public String getParentId() {
		throwErrorIfNoParent();
		return extractParentId();
	}

	private String extractParentId() {
		DataRecordLink parentLink = (DataRecordLink) recordType
				.getFirstChildWithNameInData(PARENT_ID);
		return parentLink.getLinkedRecordId();
	}

	private void throwErrorIfNoParent() {
		if (!hasParent()) {
			throw new DataMissingException("Unable to get parentId, no parents exists");
		}
	}

	@Override
	public boolean isChildOfBinary() {
		return hasParent() && parentIsBinary();
	}

	private boolean parentIsBinary() {
		String parentId = extractParentId();
		return "binary".equals(parentId);
	}

	@Override
	public boolean representsTheRecordTypeDefiningSearches() {
		String id = extractIdFromRecordInfo();
		return SEARCH.equals(id);
	}

	private String extractIdFromRecordInfo() {
		DataGroup recordInfo = recordType.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		String id = extractIdFromRecordInfo();
		return RECORD_TYPE.equals(id);
	}

	@Override
	public boolean hasLinkedSearch() {
		return recordType.containsChildWithNameInData(SEARCH);
	}

	@Override
	public String getSearchId() {
		throwErrorIfNoSearch();
		DataRecordLink searchLink = (DataRecordLink) recordType.getFirstChildWithNameInData(SEARCH);
		return searchLink.getLinkedRecordId();
	}

	private void throwErrorIfNoSearch() {
		if (!hasLinkedSearch()) {
			throw new DataMissingException("Unable to get searchId, no search exists");
		}
	}

	@Override
	public Set<Constraint> getCreateWriteRecordPartConstraints() {
		if (constraintsForCreateNotLoaded()) {
			collectAllConstraintsForCreate();
		}
		return createConstraints;
	}

	private boolean constraintsForCreateNotLoaded() {
		return !constraintsForCreateLoaded;
	}

	private void collectAllConstraintsForCreate() {
		constraintsForCreateLoaded = true;

		List<DataGroup> allChildReferences = getAllChildReferencesForCreate();
		Set<Constraint> collectedConstraints = new HashSet<>();
		collectConstraintsForChildReferences(allChildReferences, collectedConstraints);
		createConstraints.addAll(collectedConstraints);
	}

	private List<DataGroup> getAllChildReferencesForCreate() {
		DataGroup metadataGroupForMetadata = getCreateDefinitionGroup();
		DataGroup childReferences = metadataGroupForMetadata
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference");
	}

	private DataGroup getCreateDefinitionGroup() {
		return recordStorage.read(List.of(METADATA_GROUP), getCreateDefinitionId());
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		return !getCreateWriteRecordPartConstraints().isEmpty();
	}

	@Override
	public List<RecordTypeHandler> getImplementingRecordTypeHandlers() {
		if (isAbstract()) {
			return createListOfImplementingRecordTypeHandlers();
		}
		return Collections.emptyList();
	}

	private List<RecordTypeHandler> createListOfImplementingRecordTypeHandlers() {
		List<RecordTypeHandler> list = new ArrayList<>();
		StorageReadResult recordTypeList = getRecordTypeListFromStorage();
		for (DataGroup dataGroup : recordTypeList.listOfDataGroups) {
			addIfChildToCurrent(list, dataGroup);
		}
		return list;
	}

	private StorageReadResult getRecordTypeListFromStorage() {
		return recordStorage.readList(List.of(RECORD_TYPE), new Filter());
	}

	private void addIfChildToCurrent(List<RecordTypeHandler> list, DataGroup dataGroup) {
		RecordTypeHandler recordTypeHandler = recordTypeHandlerFactory
				.factorUsingDataGroup(dataGroup);
		if (currentRecordTypeIsParentTo(recordTypeHandler)) {
			if (recordTypeHandler.isAbstract()) {
				list.addAll(recordTypeHandler.getImplementingRecordTypeHandlers());
			} else {
				list.add(recordTypeHandler);
			}
		}
	}

	private boolean currentRecordTypeIsParentTo(RecordTypeHandler recordTypeHandler) {
		return recordTypeHandler.hasParent()
				&& recordTypeHandler.getParentId().equals(recordTypeId);
	}

	@Override
	public List<String> getListOfImplementingRecordTypeIds() {
		int aSlightlyLargerNumberThanHowManyRecordTypesExist = 60;
		List<String> ids = new ArrayList<>(aSlightlyLargerNumberThanHowManyRecordTypesExist);
		List<RecordTypeHandler> implementingHandlers = getImplementingRecordTypeHandlers();
		for (RecordTypeHandler recordTypeHandler : implementingHandlers) {
			ids.add(recordTypeHandler.getRecordTypeId());
		}
		return ids;
	}

	public RecordTypeHandlerFactory getRecordTypeHandlerFactory() {
		return recordTypeHandlerFactory;
	}

	@Override
	public List<String> getListOfRecordTypeIdsToReadFromStorage() {
		if (isAbstract()) {
			return getListOfImplementingRecordTypeIds();
		}
		return List.of(recordTypeId);
	}

	@Override
	public boolean storeInArchive() {
		String storeInArchive = recordType.getFirstAtomicValueWithNameInData("storeInArchive");
		return "true".equals(storeInArchive);
	}

	public RecordStorage onlyForTestGetRecordStorage() {
		return recordStorage;
	}

	public String onlyForTestGetRecordTypeId() {
		return recordTypeId;
	}

	public MetadataStorageView onlyForTestGetMetadataStorage() {
		return metadataStorageView;
	}

	public String onlyForTestGetValidationTypeId() {
		return validationTypeId;
	}

}
