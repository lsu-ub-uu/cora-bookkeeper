/*
 * Copyright 2016, 2019, 2020, 2021, 2024, 2025, 2026 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueIds;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueStorageKeys;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.RecordStorage;

public class RecordTypeHandlerImp implements RecordTypeHandler {
	private static final String RECORD_TYPE = "recordType";
	private static final String METADATA = "metadata";
	private static final String REPEAT_MAX_WHEN_NOT_REPEATABLE = "1";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String SEARCH = "search";
	private static final String RECORD_PART_CONSTRAINT = "recordPartConstraint";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private RecordType recordType;
	private RecordStorage recordStorage;
	private DataGroup metadataGroup;
	private Set<Constraint> readWriteConstraints = new LinkedHashSet<>();
	private Set<Constraint> createConstraints = new LinkedHashSet<>();
	private Set<Constraint> writeConstraints = new LinkedHashSet<>();
	private boolean constraintsForUpdateLoaded = false;
	private boolean constraintsForCreateLoaded = false;
	private Set<String> readChildren = new HashSet<>();
	private MetadataStorageView metadataStorageView;
	private String validationTypeId;
	private ValidationType validationType;
	private CollectTermHolder holder;
	private IdSourceFactory idSourceFactory;

	public static RecordTypeHandler usingRecordStorage(RecordType recordType,
			RecordStorage recordStorage, IdSourceFactory idSourceFactory) {
		return new RecordTypeHandlerImp(recordType, recordStorage, idSourceFactory);
	}

	private RecordTypeHandlerImp(RecordType recordType, RecordStorage recordStorage,
			IdSourceFactory idSourceFactory) {
		this.recordType = recordType;
		this.recordStorage = recordStorage;
		this.idSourceFactory = idSourceFactory;
	}

	public static RecordTypeHandler usingHandlerFactoryRecordStorageValidationTypeId(
			RecordType recordType, RecordStorage recordStorage, String validationTypeId,
			IdSourceFactory idSourceFactory) {
		return new RecordTypeHandlerImp(recordType, recordStorage, validationTypeId,
				idSourceFactory);
	}

	private RecordTypeHandlerImp(RecordType recordType, RecordStorage recordStorage,
			String validationTypeId, IdSourceFactory idSourceFactory) {
		this.recordType = recordType;
		this.recordStorage = recordStorage;
		this.metadataStorageView = MetadataStorageProvider.getStorageView();
		this.validationTypeId = validationTypeId;
		this.idSourceFactory = idSourceFactory;
		Optional<ValidationType> oValidationType = metadataStorageView
				.getValidationType(validationTypeId);
		if (oValidationType.isEmpty()) {
			throw DataValidationException.withMessage(
					"ValidationType with id: " + validationTypeId + ", does not exist.");
		}

		validationType = oValidationType.get();
	}

	@Override
	public boolean shouldAutoGenerateId() {
		String idSource = recordType.idSource();
		return !idSource.equals("userSupplied");
	}

	@Override
	public String getNextId() {
		if (timeStampIdSoure()) {
			return getNextIdUsingTimestamp();
		}
		return getNextIdUsingSequence();
	}

	private boolean timeStampIdSoure() {
		String idSource = recordType.idSource();
		return "timestamp".equals(idSource);
	}

	private String getNextIdUsingTimestamp() {
		IdSource idSource = idSourceFactory.factorTimestampIdSource(getRecordTypeId());
		return idSource.getId();
	}

	private String getNextIdUsingSequence() {
		String sequenceId = recordType.sequenceId().get();
		String definitionId = recordType.definitionId();
		IdSource idSource = idSourceFactory.factorSequenceIdSource(recordStorage, sequenceId,
				definitionId);
		return idSource.getId();
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
		return recordType.definitionId();
	}

	@Override
	public boolean isPublicForRead() {
		return recordType.isPublic();
	}

	@Override
	public String getRecordTypeId() {
		return recordType.id();
	}

	@Override
	public boolean useVisibility() {
		return recordType.useVisibility();
	}

	@Override
	public boolean useTrashBin() {
		return recordType.useTrashBin();
	}

	@Override
	public boolean usePermissionUnit() {
		return recordType.usePermissionUnit();
	}

	@Override
	public boolean storeInArchive() {
		return recordType.storeInArchive();
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

	private DataGroup getMetadataGroup() {
		if (metadataGroup == null) {
			metadataGroup = recordStorage.read(List.of(METADATA), getDefinitionId());
		}
		return metadataGroup;
	}

	private void collectConstraintsForChildReferences(List<DataGroup> allChildReferences,
			Set<Constraint> tempSet) {
		for (DataGroup childReference : allChildReferences) {
			collectConstraintForChildReference(childReference, tempSet);
		}
	}

	private void collectConstraintForChildReference(DataGroup childReference,
			Set<Constraint> tempSet) {
		DataGroup child = null;
		child = readChildRefFromStorage(childReference);
		if (hasConstraints(childReference)) {
			addWriteAndReadWriteConstraints(childReference, child, tempSet);
		}
		possiblyCollectConstraintsFromChildrenToChildReference(childReference, child, tempSet);
	}

	private void possiblyCollectConstraintsFromChildrenToChildReference(DataGroup childReference,
			DataGroup child, Set<Constraint> tempSet) {
		if (isGroup(child) && notRepetable(childReference)) {
			List<DataGroup> allChildReferences = getAllChildReferences(child);
			collectConstraintsForChildReferences(allChildReferences, tempSet);
		}
	}

	private boolean isGroup(DataGroup child) {
		DataAttribute type = child.getAttribute("type");
		return "group".equals(type.getValue());
	}

	private boolean notRepetable(DataGroup childReference) {
		String repeatMax = childReference.getFirstAtomicValueWithNameInData("repeatMax");
		return REPEAT_MAX_WHEN_NOT_REPEATABLE.equals(repeatMax);
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

	private void addWriteAndReadWriteConstraints(DataGroup childReference, DataGroup child,
			Set<Constraint> constraints) {
		String constraintType = getRecordPartConstraintType(childReference);

		Constraint constraint = createConstraintPossibyAddAttributes(child);
		constraint.setType(ConstraintType.fromString(constraintType));
		constraints.add(constraint);
	}

	private String getRecordPartConstraintType(DataGroup childReference) {
		return childReference.getFirstAtomicValueWithNameInData(RECORD_PART_CONSTRAINT);
	}

	private Constraint createConstraintPossibyAddAttributes(DataGroup child) {
		Constraint constraint = createConstraint(child);
		possiblyAddAttributes(child, constraint);
		return constraint;
	}

	private DataGroup readChildRefFromStorage(DataGroup childReference) {
		DataGroup ref = childReference.getFirstGroupWithNameInData("ref");
		String linkedRecordType = ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_TYPE);
		String linkedRecordId = ref.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
		return recordStorage.read(List.of(linkedRecordType), linkedRecordId);
	}

	private Constraint createConstraint(DataGroup child) {
		String refNameInData = child.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		return new Constraint(refNameInData);
	}

	private void possiblyAddAttributes(DataGroup child, Constraint constraint) {
		if (child.containsChildWithNameInData("attributeReferences")) {
			addAttributes(child, constraint);
		}
	}

	private void addAttributes(DataGroup child, Constraint constraint) {
		DataGroup attributeReferences = child.getFirstGroupWithNameInData("attributeReferences");
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
		DataGroup possibleAttributesCollection = recordStorage.read(List.of(METADATA),
				collectionId);
		DataGroup collectionItemReferences = possibleAttributesCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<DataGroup> allItemRefs = collectionItemReferences.getAllGroupsWithNameInData("ref");

		Set<String> possibleValues = new LinkedHashSet<>();
		for (DataGroup itemRef : allItemRefs) {
			String itemId = itemRef.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
			DataGroup itemGroup = recordStorage.read(List.of(METADATA), itemId);
			String itemValue = itemGroup.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
			possibleValues.add(itemValue);
		}
		return possibleValues;
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
	public boolean representsTheRecordTypeDefiningSearches() {
		String id = recordType.id();
		return SEARCH.equals(id);
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		String id = recordType.id();
		return RECORD_TYPE.equals(id);
	}

	@Override
	public boolean hasLinkedSearch() {
		return recordType.searchId().isPresent();
	}

	@Override
	public String getSearchId() {
		throwErrorIfNoSearch();
		return recordType.searchId().get();
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
		return recordStorage.read(List.of(METADATA), getCreateDefinitionId());
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		return !getCreateWriteRecordPartConstraints().isEmpty();
	}

	@Override
	public List<UniqueStorageKeys> getUniqueDefinitions() {
		if (empuniqueDefinitionNotPresent()) {
			return Collections.emptyList();
		}
		return getUniquesFromRecordType();
	}

	private boolean empuniqueDefinitionNotPresent() {
		return recordType.uniqueIds().isEmpty();
	}

	private List<UniqueStorageKeys> getUniquesFromRecordType() {
		ensureCollectTermHolderIsPopulatedFromStorage();
		return convertUniqueDataGroupsToUniques();
	}

	private void ensureCollectTermHolderIsPopulatedFromStorage() {
		if (holder == null) {
			holder = metadataStorageView.getCollectTermHolder();
		}
	}

	private List<UniqueStorageKeys> convertUniqueDataGroupsToUniques() {
		List<UniqueStorageKeys> list = new ArrayList<>();
		for (UniqueIds uniqueIds : recordType.uniqueIds()) {
			list.add(convertDataGroupToUnique(uniqueIds));
		}
		return list;
	}

	private UniqueStorageKeys convertDataGroupToUnique(UniqueIds uniqueIds) {
		String uniqueTermStorageKey = getUniqueTermStorageKeyForId(uniqueIds.uniqueId());
		Set<String> combineStorageKeys = getCombineTermStorageKeys(uniqueIds);
		return new UniqueStorageKeys(uniqueTermStorageKey, combineStorageKeys);
	}

	private Set<String> getCombineTermStorageKeys(UniqueIds uniqueIds) {
		Set<String> combineStorageKeys = new LinkedHashSet<>();
		for (String combineTerm : uniqueIds.combineTermId()) {
			String storageKeyForLink = getUniqueTermStorageKeyForId(combineTerm);
			combineStorageKeys.add(storageKeyForLink);
		}
		return combineStorageKeys;
	}

	private String getUniqueTermStorageKeyForId(String id) {
		StorageTerm storageTerm = (StorageTerm) holder.getCollectTermById(id);
		return storageTerm.storageKey;
	}

	public RecordType onlyForTestGetRecordType() {
		return recordType;
	}

	public IdSourceFactory onlyForTestGetIdSourceFactory() {
		return idSourceFactory;
	}

	public RecordStorage onlyForTestGetRecordStorage() {
		return recordStorage;
	}

	public String onlyForTestGetValidationTypeId() {
		return validationTypeId;
	}
}
