package epc.metadataformat.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.storage.MetadataStorage;

public class MetadataStorageStub implements MetadataStorage {

	@Override
	public Collection<DataGroup> getMetadataElements() {
		List<DataGroup> dataGroups = new ArrayList<>();

		// textVar2
		DataGroup textVar2 = DataGroup.withDataId("metadata");
		textVar2.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVar2RecordInfo = DataGroup.withDataId("recordInfo");
		textVar2RecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "textVar2"));
		textVar2.addChild(textVar2RecordInfo);

		textVar2.addChild(DataAtomic.withDataIdAndValue("dataId", "textVar2"));
		textVar2.addChild(DataAtomic.withDataIdAndValue("textId", "textVarText"));
		textVar2.addChild(DataAtomic.withDataIdAndValue("defTextId", "textVarDefText"));
		textVar2.addChild(DataAtomic.withDataIdAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));
		dataGroups.add(textVar2);

		// groupTypeVar
		DataGroup groupTypeVar = DataGroup.withDataId("metadata");
		groupTypeVar.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup groupTypeVarRecordInfo = DataGroup.withDataId("recordInfo");
		groupTypeVarRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "groupTypeVar"));
		groupTypeVar.addChild(groupTypeVarRecordInfo);

		groupTypeVar.addChild(DataAtomic.withDataIdAndValue("dataId", "groupTypeVar"));
		groupTypeVar.addChild(DataAtomic.withDataIdAndValue("textId", "groupTypeVarText"));
		groupTypeVar.addChild(DataAtomic.withDataIdAndValue("defTextId", "groupTypeVarDefText"));
		groupTypeVar.addChild(DataAtomic.withDataIdAndValue("refCollectionId",
				"groupTypeCollection"));
		dataGroups.add(groupTypeVar);

		// groupType1
		DataGroup groupType1 = DataGroup.withDataId("metadata");
		groupType1.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType1RecordInfo = DataGroup.withDataId("recordInfo");
		groupType1RecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "groupType1"));
		groupType1.addChild(groupType1RecordInfo);

		groupType1.addChild(DataAtomic.withDataIdAndValue("dataId", "groupType1"));
		groupType1.addChild(DataAtomic.withDataIdAndValue("textId", "groupType1Text"));
		groupType1.addChild(DataAtomic.withDataIdAndValue("defTextId", "groupType1DefText"));
		dataGroups.add(groupType1);

		// groupType2
		DataGroup groupType2 = DataGroup.withDataId("metadata");
		groupType2.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType2RecordInfo = DataGroup.withDataId("recordInfo");
		groupType2RecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "groupType2"));
		groupType2.addChild(groupType2RecordInfo);

		groupType2.addChild(DataAtomic.withDataIdAndValue("dataId", "groupType2"));
		groupType2.addChild(DataAtomic.withDataIdAndValue("textId", "groupType2Text"));
		groupType2.addChild(DataAtomic.withDataIdAndValue("defTextId", "groupType2DefText"));
		dataGroups.add(groupType2);

		// groupTypeCollection
		DataGroup groupTypeCollection = DataGroup.withDataId("metadata");
		groupTypeCollection.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup groupTypeCollectionRecordInfo = DataGroup.withDataId("recordInfo");
		groupTypeCollectionRecordInfo.addChild(DataAtomic.withDataIdAndValue("id",
				"groupTypeCollection"));
		groupTypeCollection.addChild(groupTypeCollectionRecordInfo);

		groupTypeCollection
				.addChild(DataAtomic.withDataIdAndValue("dataId", "groupTypeCollection"));
		groupTypeCollection.addChild(DataAtomic.withDataIdAndValue("textId",
				"groupTypeCollectionText"));
		groupTypeCollection.addChild(DataAtomic.withDataIdAndValue("defTextId",
				"groupTypeCollectionDefText"));

		DataGroup collectionItemReferences = DataGroup.withDataId("collectionItemReferences");
		collectionItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "groupType1"));
		collectionItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "groupType2"));
		groupTypeCollection.addChild(collectionItemReferences);

		dataGroups.add(groupTypeCollection);

		// child (textVar)
		DataGroup textVar = DataGroup.withDataId("metadata");
		textVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVarRecordInfo = DataGroup.withDataId("recordInfo");
		textVarRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "textVarId"));
		textVar.addChild(textVarRecordInfo);

		textVar.addChild(DataAtomic.withDataIdAndValue("dataId", "textVarDataId"));
		textVar.addChild(DataAtomic.withDataIdAndValue("textId", "textVarText"));
		textVar.addChild(DataAtomic.withDataIdAndValue("defTextId", "textVarDefText"));
		textVar.addChild(DataAtomic.withDataIdAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		dataGroups.add(textVar);

		// group
		DataGroup group = DataGroup.withDataId("metadata");
		group.addAttributeByIdWithValue("type", "group");

		DataGroup groupRecordInfo = DataGroup.withDataId("recordInfo");
		groupRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "group"));
		group.addChild(groupRecordInfo);

		group.addChild(DataAtomic.withDataIdAndValue("dataId", "group"));
		group.addChild(DataAtomic.withDataIdAndValue("textId", "groupTextId"));
		group.addChild(DataAtomic.withDataIdAndValue("defTextId", "groupDefText"));

		DataGroup attributeReferences = DataGroup.withDataId("attributeReferences");
		attributeReferences.addChild(DataAtomic.withDataIdAndValue("ref", "groupTypeVar"));
		group.addChild(attributeReferences);

		DataGroup childReferences = DataGroup.withDataId("childReferences");
		group.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId("childReference");
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "textVarId"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMin", "1"));
		// childReference.addChild(DataAtomic.withDataIdAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMax", "15"));
		// childReference.addChild(DataAtomic.withDataIdAndValue("secret", "true"));
		// childReference.addChild(DataAtomic.withDataIdAndValue("secretKey", "SECRET_KEY"));
		// childReference.addChild(DataAtomic.withDataIdAndValue("readOnly", "true"));
		// childReference.addChild(DataAtomic.withDataIdAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		dataGroups.add(group);

		// collection
		DataGroup colVar2 = DataGroup.withDataId("metadata");
		colVar2.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "collectionVar2"));
		colVar2.addChild(recordInfo);

		colVar2.addChild(DataAtomic.withDataIdAndValue("dataId", "collectionVar2"));
		colVar2.addChild(DataAtomic.withDataIdAndValue("textId", "collectionVarText"));
		colVar2.addChild(DataAtomic.withDataIdAndValue("defTextId", "collectionVarDefText"));
		colVar2.addChild(DataAtomic
				.withDataIdAndValue("refCollectionId", "authorityTypeCollection"));
		dataGroups.add(colVar2);

		// itemCollection
		DataGroup authority = DataGroup.withDataId("metadata");
		authority.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup authorityRecordInfo = DataGroup.withDataId("recordInfo");
		authorityRecordInfo
				.addChild(DataAtomic.withDataIdAndValue("id", "authorityTypeCollection"));
		authority.addChild(authorityRecordInfo);

		authority.addChild(DataAtomic.withDataIdAndValue("dataId", "authorityTypeCollection"));
		authority
				.addChild(DataAtomic.withDataIdAndValue("textId", "authorityTypeCollectionTextId"));
		authority.addChild(DataAtomic.withDataIdAndValue("defTextId",
				"authorityTypeCollectionDefTextId"));

		DataGroup authorityItemReferences = DataGroup.withDataId("collectionItemReferences");
		authorityItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "person"));
		authorityItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "family"));
		authorityItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "organisation"));
		authorityItemReferences.addChild(DataAtomic.withDataIdAndValue("ref", "place"));
		authority.addChild(authorityItemReferences);
		dataGroups.add(authority);

		// personItem
		DataGroup personItem = DataGroup.withDataId("metadata");
		personItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup personRecordInfo = DataGroup.withDataId("recordInfo");
		personRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "person"));
		personItem.addChild(personRecordInfo);

		personItem.addChild(DataAtomic.withDataIdAndValue("dataId", "person"));
		personItem.addChild(DataAtomic.withDataIdAndValue("textId", "personTextId"));
		personItem.addChild(DataAtomic.withDataIdAndValue("defTextId", "personDefTextId"));
		dataGroups.add(personItem);

		// familyItem
		DataGroup familyItem = DataGroup.withDataId("metadata");
		familyItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup familyRecordInfo = DataGroup.withDataId("recordInfo");
		familyRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "family"));
		familyItem.addChild(familyRecordInfo);

		familyItem.addChild(DataAtomic.withDataIdAndValue("dataId", "family"));
		familyItem.addChild(DataAtomic.withDataIdAndValue("textId", "familyTextId"));
		familyItem.addChild(DataAtomic.withDataIdAndValue("defTextId", "familyDefTextId"));
		dataGroups.add(familyItem);

		// organisationItem
		DataGroup organisationItem = DataGroup.withDataId("metadata");
		organisationItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup organisationRecordInfo = DataGroup.withDataId("recordInfo");
		organisationRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "organisation"));
		organisationItem.addChild(organisationRecordInfo);

		organisationItem.addChild(DataAtomic.withDataIdAndValue("dataId", "organisation"));
		organisationItem.addChild(DataAtomic.withDataIdAndValue("textId", "organisationTextId"));
		organisationItem.addChild(DataAtomic.withDataIdAndValue("defTextId",
				"organisationDefTextId"));
		dataGroups.add(organisationItem);

		// placeItem
		DataGroup placeItem = DataGroup.withDataId("metadata");
		placeItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup placeRecordInfo = DataGroup.withDataId("recordInfo");
		placeRecordInfo.addChild(DataAtomic.withDataIdAndValue("id", "place"));
		placeItem.addChild(placeRecordInfo);

		placeItem.addChild(DataAtomic.withDataIdAndValue("dataId", "place"));
		placeItem.addChild(DataAtomic.withDataIdAndValue("textId", "placeTextId"));
		placeItem.addChild(DataAtomic.withDataIdAndValue("defTextId", "placeDefTextId"));
		dataGroups.add(placeItem);

		return dataGroups;
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getTexts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
