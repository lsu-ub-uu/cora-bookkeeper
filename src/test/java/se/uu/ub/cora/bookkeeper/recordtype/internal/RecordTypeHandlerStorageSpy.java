package se.uu.ub.cora.bookkeeper.recordtype.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collected.Link;
import se.uu.ub.cora.data.collected.StorageTerm;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class RecordTypeHandlerStorageSpy implements RecordStorage {
	private static final String METADATA = "metadata";
	public List<String> type;
	public Collection<List<String>> types = new ArrayList<>();
	public List<String> ids = new ArrayList<>();
	public String id;
	public int numberOfChildrenWithReadWriteConstraint = 0;
	public int numberOfChildrenWithWriteConstraint = 0;
	public int numberOfGrandChildrenWithReadWriteConstraint = 0;
	public String maxNoOfGrandChildren = "1";
	// public int numberOfAttributes = 0;
	public List<String> attributesIdsToAddToConstraint = new LinkedList<String>();
	public boolean addAttribute = false;
	public boolean useStandardMetadataGroupForNew = false;

	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataGroup read(List<String> types, String id) {
		MCR.addCall("types", types, "id", id);
		// this.type = type;
		this.types.add(types);
		ids.add(id);
		this.id = id;
		for (String type : types) {

			if ("recordType".equals(type)) {
				if ("organisation".equals(id) || "organisationChildWithAttribute".equals(id)
						|| "organisationRecursiveChild".equals(id)) {
					DataGroup returnedValue = DataCreatorSpider
							.createRecordTypeWithIdAndUserSuppliedId(id, "true");
					MCR.addReturned(returnedValue);
					return returnedValue;

				}
			}
			if (METADATA.equals(type) && "organisation".equals(id)) {
				// return createMetadataGroupForOrganisation();
				DataGroup metadata = createMetadataGroupForOrganisation();
				MCR.addReturned(metadata);
				return metadata;

			}

			if (METADATA.equals(type) && "organisationChildWithAttribute".equals(id)) {
				DataGroup metadata = createMetadataGroupForOrganisationWithChildWithAttribute();
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "organisationChildWithAttributeNew".equals(id)) {
				if (useStandardMetadataGroupForNew) {
					DataGroup metadata = createMetadataGroupForOrganisationWithChildWithAttribute();
					MCR.addReturned(metadata);
					return metadata;
				}
				DataGroup metadata = createMetadataGroupForOrganisationNewWithChildWithAttribute();
				MCR.addReturned(metadata);
				return metadata;
			}

			if (METADATA.equals(type) && "organisationRecursiveChild".equals(id)) {
				DataGroup returnedValue = createMetadataGroupForOrganisationRecursiveChild();
				MCR.addReturned(returnedValue);
				return returnedValue;

			}
			if (METADATA.equals(type) && "organisationNew".equals(id)) {
				if (useStandardMetadataGroupForNew) {
					DataGroup metadata = createMetadataGroupForOrganisation();
					MCR.addReturned(metadata);
					return metadata;
				}
				DataGroup metadata = createMetadataGroupForOrganisationNew();
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "divaOrganisationRoot".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("organisationRoot");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "showInPortalTextVar".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("showInPortal");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "showInDefenceTextVar".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("showInDefence");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "divaOrganisationRoot2".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("organisationRoot2");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "showInPortalTextVar2".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("showInPortal2");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "showInDefenceTextVar2".equals(id)) {
				DataGroup metadata = createMetadataTextVariableUsingNameInData("showInDefence2");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "greatGrandChildTextVar".equals(id)) {
				DataGroupSpiderOldSpy metadata = createMetadataTextVariableUsingNameInData(
						"greatGrandChild");
				MCR.addReturned(metadata);
				return metadata;
			}
			if (METADATA.equals(type) && "organisationAlternativeNameGroup".equals(id)) {
				DataGroupSpiderOldSpy metadataGroup = createMetadataGroupWithChildReferences(
						"organisationAlternativeName", "organisationAlternativeNameGroup");
				DataGroup childReferences = metadataGroup
						.getFirstGroupWithNameInData("childReferences");
				if (!attributesIdsToAddToConstraint.isEmpty()) {
					DataGroupSpiderOldSpy attributeReferences = new DataGroupSpiderOldSpy(
							"attributeReferences");
					for (String attributeId : attributesIdsToAddToConstraint) {
						DataGroupSpiderOldSpy ref = createAttributeReference(attributeId, "0");
						attributeReferences.addChild(ref);
					}
					metadataGroup.addChild(attributeReferences);
				}

				if (numberOfGrandChildrenWithReadWriteConstraint > 0) {
					DataGroupSpiderOldSpy grandChildWithReadConstraint = createChildReferenceWithConstraint(
							METADATA, "showInPortalTextVar", "readWrite", "0", "1");
					childReferences.addChild(grandChildWithReadConstraint);
				}
				if (numberOfGrandChildrenWithReadWriteConstraint > 1) {
					DataGroupSpiderOldSpy grandChildWithReadConstraint = createChildReference(
							METADATA, "grandChildGroup", "0", maxNoOfGrandChildren);
					childReferences.addChild(grandChildWithReadConstraint);
				}

				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			if (METADATA.equals(type) && "grandChildGroup".equals(id)) {
				DataGroupSpiderOldSpy metadataGroup = createMetadataGroupWithChildReferences(id,
						"grandChildGroup");

				DataGroupSpiderOldSpy greatGrandChildWithReadConstraint = createChildReferenceWithConstraint(
						METADATA, "greatGrandChildTextVar", "readWrite", "0", "1");
				DataGroup childReferences = metadataGroup
						.getFirstGroupWithNameInData("childReferences");
				childReferences.addChild(greatGrandChildWithReadConstraint);
				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			if (METADATA.equals(type) && "divaOrganisationNameGroup".equals(id)) {
				DataGroupSpiderOldSpy metadataGroup = createMetadataGroupWithChildReferences(
						"organisationName", "divaOrganisationNameGroup");

				DataGroupSpiderOldSpy grandChildWithReadConstraint = createChildReference(METADATA,
						"showInPortalTextVar", "0", "1");
				DataGroup childReferences = metadataGroup
						.getFirstGroupWithNameInData("childReferences");
				childReferences.addChild(grandChildWithReadConstraint);
				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			if (METADATA.equals(type) && "divaOrganisationRecursiveNameGroup".equals(id)) {
				// DataGroupSpy metadataGroup =
				// createMetadataGroupWithChildReferences("organisationName",
				// "divaOrganisationRecursiveNameGroup");

				DataGroupSpiderOldSpy metadataGroup = createBasicMetadataGroup(
						"divaOrganisationRecursiveNameGroup", "group");
				metadataGroup.addChild(
						createDataAtomicSpyUsingNameAndValue("nameInData", "organisationName"));
				DataGroupSpiderOldSpy childReferences = new DataGroupSpiderOldSpy(
						"childReferences");
				metadataGroup.addChild(childReferences);

				DataGroupSpiderOldSpy grandChildWithReadConstraint = createChildReference(METADATA,
						"showInPortalTextVar", "0", "1");
				childReferences.addChild(grandChildWithReadConstraint);

				DataGroupSpiderOldSpy recursiveChild = createChildReference(METADATA,
						"divaOrganisationRecursiveNameGroup", "0", "1");
				childReferences.addChild(recursiveChild);

				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			if ("metadataCollectionVariable".equals(type)
					&& "textPartTypeCollectionVar".equals(id)) {
				DataGroupSpiderOldSpy metadataGroup = createMetadataGroupWithNameInDataAndFinalValue(
						"type", "default");
				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			// TODO: if ("metadataCollectionVariable".equals(type) &&
			// "choosableAttributesCollectionVar".equals(id))
			// if ("metadataCollectionVariable".equals(type) &&
			// "choosableAttributesCollectionVar".equals(id)) {
			//
			// DataGroupSpy metadataGroup = new DataGroupSpy();
			// metadataGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
			// Supplier, "bla");
			// return metadataGroup;
			// }
			if ("metadataCollectionVariable".equals(type)
					&& "textPartLangCollectionVar".equals(id)) {
				DataGroupSpiderOldSpy metadataGroup = createMetadataGroupWithNameInDataAndFinalValue(
						"lang", "sv");
				MCR.addReturned(metadataGroup);
				return metadataGroup;
			}
			if ("metadataCollectionVariable".equals(type)
					&& "choosableAttributeCollectionVar".equals(id)) {
				DataGroupSpy collectionVar = new DataGroupSpy();

				DataGroupSpy refCollection = new DataGroupSpy();

				collectionVar.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
						(Supplier<Boolean>) () -> false, "finalValue");

				collectionVar.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
						(Supplier<DataGroupSpy>) () -> refCollection, "refCollection");
				String collectionId = "choosableCollection";
				refCollection.MRV.setSpecificReturnValuesSupplier(
						"getFirstAtomicValueWithNameInData", (Supplier<String>) () -> collectionId,
						"linkedRecordId");

				MCR.addReturned(collectionVar);
				return collectionVar;
			}

			if ("metadataItemCollection".equals(type) && "choosableCollection".equals(id)) {
				DataGroupSpy collection = new DataGroupSpy();

				DataGroupSpy collectionItemReferences = new DataGroupSpy();
				collection.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
						(Supplier<DataGroupSpy>) () -> collectionItemReferences,
						"collectionItemReferences");

				List<DataGroupSpy> refs = new ArrayList<>();
				refs.add(createCollectionItemWithId("choosableCollectionItem1"));
				refs.add(createCollectionItemWithId("choosableCollectionItem2"));
				collectionItemReferences.MRV.setSpecificReturnValuesSupplier(
						"getAllGroupsWithNameInData", (Supplier<List<DataGroupSpy>>) () -> refs,
						"ref");

				MCR.addReturned(collection);
				return collection;
			}
		}
		// if ("metadataCollectionItem".equals(type) && "choosableCollectionItem1".equals(id)) {
		if ("choosableCollectionItem1".equals(id)) {
			DataGroupSpy collectionItem = createCollectionItemWithValue("choosableItemValue1");
			MCR.addReturned(collectionItem);
			return collectionItem;
		}
		// if ("metadataCollectionItem".equals(type) && "choosableCollectionItem2".equals(id)) {
		if ("choosableCollectionItem2".equals(id)) {
			DataGroupSpy collectionItem = createCollectionItemWithValue("choosableItemValue2");
			MCR.addReturned(collectionItem);
			return collectionItem;
		}
		DataGroupSpiderOldSpy returnedValue = new DataGroupSpiderOldSpy(id);
		MCR.addReturned(returnedValue);
		return returnedValue;
	}

	private DataGroupSpy createCollectionItemWithValue(String itemValue) {
		DataGroupSpy collectionItem = new DataGroupSpy();

		// itemValue = "choosableItemValue1";
		// collectionItem.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
		// (Supplier<String>) () -> itemValue, "collectionItemReferences");
		collectionItem.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> itemValue, "nameInData");
		MCR.addReturned(collectionItem);
		return collectionItem;
	}

	private DataGroupSpy createCollectionItemWithId(String collectionItemId) {
		DataGroupSpy ref1 = new DataGroupSpy();
		ref1.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				(Supplier<String>) () -> collectionItemId, "linkedRecordId");
		return ref1;
	}

	private DataGroupSpiderOldSpy createMetadataGroupWithChildReferences(String nameInData,
			String groupId) {
		DataGroupSpiderOldSpy metadataGroup = createBasicMetadataGroup(groupId, "group");
		metadataGroup.addChild(createDataAtomicSpyUsingNameAndValue("nameInData", nameInData));
		DataGroupSpiderOldSpy childReferences = new DataGroupSpiderOldSpy("childReferences");
		metadataGroup.addChild(childReferences);
		return metadataGroup;
	}

	private DataAtomicSpy createDataAtomicSpyUsingNameAndValue(String name, String value) {
		DataAtomicSpy dataAtomicSpy = new DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		return dataAtomicSpy;
	}

	private DataGroup createMetadataGroupForOrganisationNew() {
		DataGroupSpiderOldSpy dataGroupSpy = createBasicMetadataGroup("organinsationNewGroup",
				"group");
		DataGroupSpiderOldSpy childReferences = new DataGroupSpiderOldSpy("childReferences");
		DataGroupSpiderOldSpy childReference = createChildReference(METADATA,
				"divaOrganisationNameGroup", "0", "1");
		childReferences.addChild(childReference);

		if (numberOfChildrenWithReadWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint = createChildReferenceWithConstraint(
					METADATA, "divaOrganisationRoot2", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint);

		}
		if (numberOfChildrenWithReadWriteConstraint > 1) {
			DataGroupSpiderOldSpy referenceWithConstraint2 = createChildReferenceWithConstraint(
					METADATA, "showInPortalTextVar2", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint2);

		}
		if (numberOfChildrenWithWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint3 = createChildReferenceWithConstraint(
					METADATA, "showInDefenceTextVar2", "write", "0", "1");
			childReferences.addChild(referenceWithConstraint3);

		}

		dataGroupSpy.addChild(childReferences);
		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;
	}

	private DataGroupSpiderOldSpy createMetadataGroupWithNameInDataAndFinalValue(String nameInData,
			String finalValue) {
		DataGroupSpiderOldSpy metadataGroup = createBasicMetadataGroup(nameInData + "Id", "group");

		metadataGroup.addChild(createDataAtomicSpyUsingNameAndValue("nameInData", nameInData));
		metadataGroup.addChild(createDataAtomicSpyUsingNameAndValue("finalValue", finalValue));
		metadataGroup.addAttributeByIdWithValue("type", "group");
		return metadataGroup;
	}

	private DataGroupSpiderOldSpy createBasicMetadataGroup(String id, String typeValue) {
		DataGroupSpiderOldSpy metadataGroup = new DataGroupSpiderOldSpy("metadata");
		metadataGroup.addAttributeByIdWithValue("type", typeValue);
		DataGroupSpiderOldSpy recordInfo = new DataGroupSpiderOldSpy("recordInfo");
		recordInfo.addChild(createDataAtomicSpyUsingNameAndValue("id", id));
		metadataGroup.addChild(recordInfo);
		return metadataGroup;
	}

	private DataGroupSpiderOldSpy createAttributeReference(String linkedRecordId, String repeatId) {
		DataGroupSpiderOldSpy ref = new DataGroupSpiderOldSpy("ref");
		ref.addChild(createDataAtomicSpyUsingNameAndValue("linkedRecordType",
				"metadataCollectionVariable"));
		ref.addChild(createDataAtomicSpyUsingNameAndValue("linkedRecordId", linkedRecordId));
		ref.setRepeatId(repeatId);
		return ref;
	}

	private DataGroupSpiderOldSpy createMetadataTextVariableUsingNameInData(String nameInData) {
		DataGroupSpiderOldSpy metadataTextVariable = createBasicMetadataGroup(nameInData + "Id",
				"text");
		metadataTextVariable
				.addChild(createDataAtomicSpyUsingNameAndValue("nameInData", nameInData));
		MCR.addReturned(metadataTextVariable);
		return metadataTextVariable;
	}

	private DataGroup createMetadataGroupForOrganisation() {
		DataGroupSpiderOldSpy dataGroupSpy = createBasicMetadataGroup("organisationGroup", "group");
		DataGroupSpiderOldSpy childReferences = new DataGroupSpiderOldSpy("childReferences");
		DataGroupSpiderOldSpy childReference = createChildReference(METADATA,
				"divaOrganisationNameGroup", "0", "1");
		childReferences.addChild(childReference);
		dataGroupSpy.addAttributeByIdWithValue("type", "group");

		if (numberOfChildrenWithReadWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint = createChildReferenceWithConstraint(
					METADATA, "divaOrganisationRoot", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint);

		}
		if (numberOfChildrenWithReadWriteConstraint > 1) {
			DataGroupSpiderOldSpy referenceWithConstraint2 = createChildReferenceWithConstraint(
					METADATA, "showInPortalTextVar", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint2);

		}
		if (numberOfChildrenWithWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint3 = createChildReferenceWithConstraint(
					METADATA, "showInDefenceTextVar", "write", "0", "1");
			childReferences.addChild(referenceWithConstraint3);

		}

		dataGroupSpy.addChild(childReferences);
		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;
	}

	private DataGroup createMetadataGroupForOrganisationRecursiveChild() {
		DataGroupSpiderOldSpy dataGroupSpy = createBasicMetadataGroup("recursiveChild", "group");
		DataGroupSpiderOldSpy childReferences = new DataGroupSpiderOldSpy("childReferences");
		DataGroupSpiderOldSpy childReference = createChildReference(METADATA,
				"divaOrganisationRecursiveNameGroup", "0", "1");
		childReferences.addChild(childReference);
		dataGroupSpy.addChild(childReferences);
		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;
	}

	private DataGroup createMetadataGroupForOrganisationWithChildWithAttribute() {
		DataGroup metadataGroup = createMetadataGroupForOrganisation();
		DataGroup childReferences = metadataGroup.getFirstGroupWithNameInData("childReferences");
		if (numberOfChildrenWithReadWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint = createChildReferenceWithConstraint(
					METADATA, "organisationAlternativeNameGroup", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint);
		}

		return metadataGroup;
	}

	private DataGroup createMetadataGroupForOrganisationNewWithChildWithAttribute() {
		DataGroup metadataGroup = createMetadataGroupForOrganisationNew();
		DataGroup childReferences = metadataGroup.getFirstGroupWithNameInData("childReferences");
		if (numberOfChildrenWithReadWriteConstraint > 0) {
			DataGroupSpiderOldSpy referenceWithConstraint = createChildReferenceWithConstraint(
					METADATA, "organisationAlternativeNameGroup", "readWrite", "0", "1");
			childReferences.addChild(referenceWithConstraint);
		}

		return metadataGroup;
	}

	private DataGroupSpiderOldSpy createChildReferenceWithConstraint(String linkedRecordType,
			String linkedRecordId, String constraint, String repeatMin, String repeatMax) {
		DataGroupSpiderOldSpy referenceWithConstraint = createChildReference(linkedRecordType,
				linkedRecordId, repeatMin, repeatMax);
		referenceWithConstraint
				.addChild(createDataAtomicSpyUsingNameAndValue("recordPartConstraint", constraint));
		return referenceWithConstraint;
	}

	private DataGroupSpiderOldSpy createChildReference(String linkedRecordType,
			String linkedRecordId, String repeatMin, String repeatMax) {
		DataGroupSpiderOldSpy childReference = new DataGroupSpiderOldSpy("childReference");
		DataGroupSpiderOldSpy ref = new DataGroupSpiderOldSpy("ref");
		ref.addChild(createDataAtomicSpyUsingNameAndValue("linkedRecordType", linkedRecordType));
		ref.addChild(createDataAtomicSpyUsingNameAndValue("linkedRecordId", linkedRecordId));
		childReference.addChild(ref);
		childReference.addChild(createDataAtomicSpyUsingNameAndValue("repeatMin", repeatMin));
		childReference.addChild(createDataAtomicSpyUsingNameAndValue("repeatMax", repeatMax));
		return childReference;
	}

	@Override
	public void create(String type, String id, DataGroup record, Set<StorageTerm> storageTerms,
			Set<Link> links, String dataDivider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(String type, String id, DataGroup record, Set<StorageTerm> collectedTerms,
			Set<Link> links, String dataDivider) {
		// TODO Auto-generated method stub

	}

	@Override
	public StorageReadResult readList(List<String> type, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Link> getLinksToRecord(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recordExists(List<String> types, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getTotalNumberOfRecordsForTypes(List<String> types, Filter filter) {
		// TODO Auto-generated method stub
		return 0;
	}
}
