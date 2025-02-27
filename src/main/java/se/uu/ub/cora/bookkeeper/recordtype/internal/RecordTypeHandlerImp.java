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

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactory;
import se.uu.ub.cora.bookkeeper.recordtype.Unique;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.validator.DataValidationException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.storage.RecordStorage;

public class RecordTypeHandlerImp implements RecordTypeHandler {
	private static final String METADATA = "metadata";
	private static final String REPEAT_MAX_WHEN_NOT_REPEATABLE = "1";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String SEARCH = "search";
	private static final String RECORD_PART_CONSTRAINT = "recordPartConstraint";
	private static final String RECORD_TYPE = "recordType";
	private DataRecordGroup recordType;
	private String recordTypeId;
	private RecordStorage recordStorage;
	private DataRecordGroup metadataGroup;
	private Set<Constraint> readWriteConstraints = new LinkedHashSet<>();
	private Set<Constraint> createConstraints = new LinkedHashSet<>();
	private Set<Constraint> writeConstraints = new LinkedHashSet<>();
	private boolean constraintsForUpdateLoaded = false;
	private boolean constraintsForCreateLoaded = false;
	private RecordTypeHandlerFactory recordTypeHandlerFactory;
	private Set<String> readChildren = new HashSet<>();
	private MetadataStorageView metadataStorageView;
	private String validationTypeId;
	private ValidationType validationType;
	private CollectTermHolder holder;

	RecordTypeHandlerImp() {
		// only for test
	}

	public static RecordTypeHandler usingRecordStorageAndRecordTypeId(
			RecordTypeHandlerFactory recordTypeHandlerFactory, RecordStorage recordStorage,
			String recordTypeId) {
		return new RecordTypeHandlerImp(recordTypeHandlerFactory, recordStorage, recordTypeId);
	}

	private RecordTypeHandlerImp(RecordTypeHandlerFactory recordTypeHandlerFactory,
			RecordStorage recordStorage, String recordTypeId) {
		this.recordTypeHandlerFactory = recordTypeHandlerFactory;
		this.recordStorage = recordStorage;
		this.recordTypeId = recordTypeId;
		recordType = recordStorage.read(RECORD_TYPE, recordTypeId);
	}

	public static RecordTypeHandler usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
			RecordTypeHandlerFactory recordTypeHandlerFactory, RecordStorage recordStorage,
			MetadataStorageView metadataStorageView, String validationTypeId) {
		return new RecordTypeHandlerImp(recordTypeHandlerFactory, recordStorage,
				metadataStorageView, validationTypeId);
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
		recordType = recordStorage.read(RECORD_TYPE, validationType.validatesRecordTypeId());
		recordTypeId = recordType.getId();
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
		DataRecordLink metadataLink = recordType.getFirstChildOfTypeAndName(DataRecordLink.class,
				"metadataId");
		return metadataLink.getLinkedRecordId();
	}

	@Override
	public boolean isPublicForRead() {
		String isPublic = recordType.getFirstAtomicValueWithNameInData("public");
		return "true".equals(isPublic);
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
		List<DataGroup> allChildReferences = getAllChildReferences(readDefinition());
		Set<Constraint> collectedConstraints = new LinkedHashSet<>();
		collectConstraintsForChildReferences(allChildReferences, collectedConstraints);
		for (Constraint constraint : collectedConstraints) {
			writeConstraints.add(constraint);
			possiblyAddReadWriteConstraint(constraint);
		}
	}

	private DataRecordGroup readDefinition() {
		if (metadataGroup == null) {
			metadataGroup = recordStorage.read(METADATA, getDefinitionId());
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
		DataRecordGroup child = readChildRefFromStorage(childReference);
		if (hasConstraints(childReference)) {
			addWriteAndReadWriteConstraints(childReference, child, tempSet);
		}
		possiblyCollectConstraintsFromChildrenToChildReference(childReference, child, tempSet);
	}

	private void possiblyCollectConstraintsFromChildrenToChildReference(DataGroup childReference,
			DataRecordGroup child, Set<Constraint> tempSet) {
		if (isGroup(child) && notRepetable(childReference)) {
			List<DataGroup> allChildReferences = getAllChildReferences(child);
			collectConstraintsForChildReferences(allChildReferences, tempSet);
		}
	}

	private boolean isGroup(DataRecordGroup child) {
		DataAttribute type = child.getAttribute("type");
		return "group".equals(type.getValue());
	}

	private boolean notRepetable(DataGroup childReference) {
		String repeatMax = childReference.getFirstAtomicValueWithNameInData("repeatMax");
		return REPEAT_MAX_WHEN_NOT_REPEATABLE.equals(repeatMax);
	}

	private List<DataGroup> getAllChildReferences(DataRecordGroup metadataGroupForMetadata) {
		String id = metadataGroupForMetadata.getId();

		if (childrenToGroupHasAlreadyBeenChecked(id)) {
			return Collections.emptyList();
		}
		return addGroupToCheckedAndGetChildReferences(metadataGroupForMetadata, id);
	}

	private boolean childrenToGroupHasAlreadyBeenChecked(String id) {
		return readChildren.contains(id);
	}

	private List<DataGroup> addGroupToCheckedAndGetChildReferences(
			DataRecordGroup metadataGroupForMetadata, String id) {
		readChildren.add(id);
		DataGroup childReferences = metadataGroupForMetadata
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference");
	}

	private boolean hasConstraints(DataGroup childReference) {
		return childReference.containsChildWithNameInData(RECORD_PART_CONSTRAINT);
	}

	private void addWriteAndReadWriteConstraints(DataGroup childReference, DataRecordGroup child,
			Set<Constraint> constraints) {
		String constraintType = getRecordPartConstraintType(childReference);

		Constraint constraint = createConstraintPossibyAddAttributes(child);
		constraint.setType(ConstraintType.fromString(constraintType));
		constraints.add(constraint);
	}

	private String getRecordPartConstraintType(DataGroup childReference) {
		return childReference.getFirstAtomicValueWithNameInData(RECORD_PART_CONSTRAINT);
	}

	private Constraint createConstraintPossibyAddAttributes(DataRecordGroup child) {
		Constraint constraint = createConstraint(child);
		possiblyAddAttributes(child, constraint);
		return constraint;
	}

	private DataRecordGroup readChildRefFromStorage(DataGroup childReference) {
		DataRecordLink refLink = childReference.getFirstChildOfTypeAndName(DataRecordLink.class,
				"ref");
		return recordStorage.read(METADATA, refLink.getLinkedRecordId());
	}

	private Constraint createConstraint(DataRecordGroup child) {
		String refNameInData = child.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		return new Constraint(refNameInData);
	}

	private void possiblyAddAttributes(DataRecordGroup child, Constraint constraint) {
		if (child.containsChildWithNameInData("attributeReferences")) {
			addAttributes(child, constraint);
		}
	}

	private void addAttributes(DataRecordGroup child, Constraint constraint) {
		DataGroup attributeReferences = child.getFirstGroupWithNameInData("attributeReferences");
		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");

		for (DataRecordLink attributeRef : attributeRefs) {
			addAttributeToConstraintForAttributeRef(constraint, attributeRef);
		}
	}

	private void addAttributeToConstraintForAttributeRef(Constraint constraint,
			DataRecordLink attributeRef) {
		DataRecordGroup collectionVar = getCollectionVarFromStorageForAttributeRef(attributeRef);
		if (collectionVar.containsChildWithNameInData("finalValue")) {
			addFinalValueAttribute(constraint, collectionVar);
		} else {
			addMultivalueAttribute(constraint, collectionVar);
		}
	}

	private DataRecordGroup getCollectionVarFromStorageForAttributeRef(
			DataRecordLink attributeRef) {
		return recordStorage.read(METADATA, attributeRef.getLinkedRecordId());
	}

	private void addFinalValueAttribute(Constraint constraint, DataRecordGroup collectionVar) {
		String attributeName = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		String attributeValue = collectionVar.getFirstAtomicValueWithNameInData("finalValue");
		constraint.addAttributeUsingNameInDataAndPossibleValues(attributeName,
				Set.of(attributeValue));
	}

	private void addMultivalueAttribute(Constraint constraint, DataRecordGroup collectionVar) {
		String attributeName = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		Set<String> possibleAttributeValues = getPossibleAttributeValues(collectionVar);
		constraint.addAttributeUsingNameInDataAndPossibleValues(attributeName,
				possibleAttributeValues);
	}

	private Set<String> getPossibleAttributeValues(DataRecordGroup collectionVar) {
		DataRecordLink refCollectionLink = collectionVar
				.getFirstChildOfTypeAndName(DataRecordLink.class, "refCollection");
		String collectionId = refCollectionLink.getLinkedRecordId();
		DataRecordGroup possibleAttributesCollection = recordStorage.read(METADATA, collectionId);
		DataGroup collectionItemReferences = possibleAttributesCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<DataRecordLink> allItemRefs = collectionItemReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");

		Set<String> possibleValues = new LinkedHashSet<>();
		for (DataRecordLink itemRef : allItemRefs) {
			String itemId = itemRef.getLinkedRecordId();
			DataRecordGroup itemGroup = recordStorage.read(METADATA, itemId);
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
	public String getRecordTypeId() {
		return recordTypeId;
	}

	@Override
	public boolean representsTheRecordTypeDefiningSearches() {
		String id = recordType.getId();
		return SEARCH.equals(id);
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		String id = recordType.getId();
		return RECORD_TYPE.equals(id);
	}

	@Override
	public boolean hasLinkedSearch() {
		return recordType.containsChildWithNameInData(SEARCH);
	}

	@Override
	public String getSearchId() {
		throwErrorIfNoSearch();
		DataRecordLink searchLink = recordType.getFirstChildOfTypeAndName(DataRecordLink.class,
				SEARCH);
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
		DataRecordGroup metadataGroupForMetadata = getCreateDefinitionGroup();
		DataGroup childReferences = metadataGroupForMetadata
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference");
	}

	private DataRecordGroup getCreateDefinitionGroup() {
		return recordStorage.read(METADATA, getCreateDefinitionId());
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		return !getCreateWriteRecordPartConstraints().isEmpty();
	}

	public RecordTypeHandlerFactory getRecordTypeHandlerFactory() {
		return recordTypeHandlerFactory;
	}

	@Override
	public boolean storeInArchive() {
		String storeInArchive = recordType.getFirstAtomicValueWithNameInData("storeInArchive");
		return "true".equals(storeInArchive);
	}

	@Override
	public List<Unique> getUniqueDefinitions() {
		if (uniqueDefinitionDoNotExistsInRecordType()) {
			return Collections.emptyList();
		}
		return getUniquesFromRecordType();
	}

	private boolean uniqueDefinitionDoNotExistsInRecordType() {
		return !recordType.containsChildWithNameInData("unique");
	}

	private List<Unique> getUniquesFromRecordType() {
		ensureCollectTermHolderIsPopulatedFromStorage();
		List<DataGroup> allUniqueDefinitions = recordType.getChildrenOfTypeAndName(DataGroup.class,
				"unique");
		return convertUniqueDataGroupsToUniques(allUniqueDefinitions);
	}

	private void ensureCollectTermHolderIsPopulatedFromStorage() {
		if (holder == null) {
			holder = metadataStorageView.getCollectTermHolder();
		}
	}

	private List<Unique> convertUniqueDataGroupsToUniques(List<DataGroup> allUniqueDefinitions) {
		List<Unique> list = new ArrayList<>();
		for (DataGroup uniqueDG : allUniqueDefinitions) {
			list.add(convertDataGroupToUnique(uniqueDG));
		}
		return list;
	}

	private Unique convertDataGroupToUnique(DataGroup uniqueDG) {
		String uniqueTermStorageKey = getUniqueTermStorageKey(uniqueDG);
		Set<String> combineStorageKeys = getCombineTermStorageKeys(uniqueDG);
		return new Unique(uniqueTermStorageKey, combineStorageKeys);
	}

	private String getUniqueTermStorageKey(DataGroup uniqueDG) {
		DataRecordLink uniqueTermLink = uniqueDG.getFirstChildOfTypeAndName(DataRecordLink.class,
				"uniqueTerm");
		return getStorageKeyForLink(uniqueTermLink);
	}

	private String getStorageKeyForLink(DataRecordLink collectTermLink) {
		String collectTermLinkId = collectTermLink.getLinkedRecordId();
		return getStorageKeyUsingCollectTermId(collectTermLinkId);
	}

	private String getStorageKeyUsingCollectTermId(String collectTermId) {
		StorageTerm storageTerm = (StorageTerm) holder.getCollectTermById(collectTermId);
		return storageTerm.storageKey;
	}

	private Set<String> getCombineTermStorageKeys(DataGroup uniqueDG) {
		List<DataRecordLink> combineTermLinks = uniqueDG
				.getChildrenOfTypeAndName(DataRecordLink.class, "combineTerm");
		return getCombineStorageKeysFromLinks(combineTermLinks);
	}

	private Set<String> getCombineStorageKeysFromLinks(List<DataRecordLink> combineTermLinks) {
		Set<String> combineStorageKeys = new LinkedHashSet<>();
		for (DataRecordLink dataRecordLink : combineTermLinks) {
			String storageKeyForLink = getStorageKeyForLink(dataRecordLink);
			combineStorageKeys.add(storageKeyForLink);
		}
		return combineStorageKeys;
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
