/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

public class MetadataStorageStub implements MetadataStorageView {

	private Set<DataGroup> dataGroups;

	@Override
	public Collection<DataGroup> getMetadataElements() {
		// dataGroups = new ArrayList<>();
		dataGroups = new LinkedHashSet<>();

		// textVar2
		DataGroup textVar2 = new DataGroupOldSpy("metadata");
		textVar2.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVar2RecordInfo = new DataGroupOldSpy("recordInfo");
		textVar2RecordInfo.addChild(new DataAtomicOldSpy("id", "textVar2"));
		textVar2.addChild(textVar2RecordInfo);

		textVar2.addChild(new DataAtomicOldSpy("nameInData", "textVar2"));
		addTextByNameInDataAndId(textVar2, "textId", "textVarText");
		addTextByNameInDataAndId(textVar2, "defTextId", "textVarDefText");
		textVar2.addChild(
				new DataAtomicOldSpy("regEx", "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));
		dataGroups.add(textVar2);

		// groupTypeVar
		DataGroup groupTypeVar = new DataGroupOldSpy("metadata");
		groupTypeVar.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup groupTypeVarRecordInfo = new DataGroupOldSpy("recordInfo");
		groupTypeVarRecordInfo.addChild(new DataAtomicOldSpy("id", "groupTypeVar"));
		groupTypeVar.addChild(groupTypeVarRecordInfo);

		groupTypeVar.addChild(new DataAtomicOldSpy("nameInData", "groupTypeVar"));
		addTextByNameInDataAndId(groupTypeVar, "textId", "groupTypeVarText");
		addTextByNameInDataAndId(groupTypeVar, "defTextId", "groupTypeVarDefText");

		DataGroup refCollection = new DataGroupOldSpy("refCollection");
		refCollection.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(new DataAtomicOldSpy("linkedRecordId", "groupTypeCollection"));
		groupTypeVar.addChild(refCollection);

		dataGroups.add(groupTypeVar);

		// groupType1
		DataGroup groupType1 = new DataGroupOldSpy("metadata");
		groupType1.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType1RecordInfo = new DataGroupOldSpy("recordInfo");
		groupType1RecordInfo.addChild(new DataAtomicOldSpy("id", "groupType1"));
		groupType1.addChild(groupType1RecordInfo);

		groupType1.addChild(new DataAtomicOldSpy("nameInData", "groupType1"));

		dataGroups.add(groupType1);

		addTextByNameInDataAndId(groupType1, "textId", "groupType1Text");
		addTextByNameInDataAndId(groupType1, "defTextId", "groupType1DefText");

		// groupType2
		DataGroup groupType2 = new DataGroupOldSpy("metadata");
		groupType2.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType2RecordInfo = new DataGroupOldSpy("recordInfo");
		groupType2RecordInfo.addChild(new DataAtomicOldSpy("id", "groupType2"));
		groupType2.addChild(groupType2RecordInfo);

		groupType2.addChild(new DataAtomicOldSpy("nameInData", "groupType2"));
		addTextByNameInDataAndId(groupType2, "textId", "groupType2Text");
		addTextByNameInDataAndId(groupType2, "defTextId", "groupType2DefText");
		dataGroups.add(groupType2);

		// groupTypeCollection
		DataGroup groupTypeCollection = new DataGroupOldSpy("metadata");
		groupTypeCollection.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup groupTypeCollectionRecordInfo = new DataGroupOldSpy("recordInfo");
		groupTypeCollectionRecordInfo.addChild(new DataAtomicOldSpy("id", "groupTypeCollection"));
		groupTypeCollection.addChild(groupTypeCollectionRecordInfo);

		groupTypeCollection.addChild(new DataAtomicOldSpy("nameInData", "groupTypeCollection"));
		addTextByNameInDataAndId(groupTypeCollection, "textId", "groupTypeCollectionText");
		addTextByNameInDataAndId(groupTypeCollection, "defTextId", "groupTypeCollectionDefText");

		DataGroup collectionItemReferences = new DataGroupOldSpy("collectionItemReferences");

		createAndAddItemReference(collectionItemReferences, "groupType1", "one");
		createAndAddItemReference(collectionItemReferences, "groupType2", "two");

		groupTypeCollection.addChild(collectionItemReferences);

		dataGroups.add(groupTypeCollection);

		// child (textVar)
		DataGroup textVar = new DataGroupOldSpy("metadata");
		textVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVarRecordInfo = new DataGroupOldSpy("recordInfo");
		textVarRecordInfo.addChild(new DataAtomicOldSpy("id", "textVarId"));
		textVar.addChild(textVarRecordInfo);

		textVar.addChild(new DataAtomicOldSpy("nameInData", "textVarNameInData"));
		addTextByNameInDataAndId(textVar, "textId", "textVarText");
		addTextByNameInDataAndId(textVar, "defTextId", "textVarDefText");
		textVar.addChild(
				new DataAtomicOldSpy("regEx", "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		dataGroups.add(textVar);

		// group
		DataGroup group = new DataGroupOldSpy("metadata");
		group.addAttributeByIdWithValue("type", "group");

		DataGroup groupRecordInfo = new DataGroupOldSpy("recordInfo");
		groupRecordInfo.addChild(new DataAtomicOldSpy("id", "group"));
		group.addChild(groupRecordInfo);

		group.addChild(new DataAtomicOldSpy("nameInData", "group"));
		addTextByNameInDataAndId(group, "textId", "groupTextId");
		addTextByNameInDataAndId(group, "defTextId", "groupDefText");

		DataGroup attributeReferences = new DataGroupOldSpy("attributeReferences");
		DataGroup attributeRef = new DataGroupOldSpy("ref");
		attributeRef
				.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataCollectionVariable"));
		attributeRef.addChild(new DataAtomicOldSpy("linkedRecordId", "groupTypeVar"));
		attributeReferences.addChild(attributeRef);
		group.addChild(attributeReferences);

		DataGroup childReferences = new DataGroupOldSpy("childReferences");
		group.addChild(childReferences);

		DataGroup childReference = new DataGroupOldSpy("childReference");
		DataGroup ref = new DataGroupOldSpy("ref");
		ref.addAttributeByIdWithValue("type", "textVariable");
		ref.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataTextVariable"));
		ref.addChild(new DataAtomicOldSpy("linkedRecordId", "textVarId"));
		childReference.addChild(ref);
		childReference.addChild(new DataAtomicOldSpy("repeatMin", "1"));
		// childReference.addChild(new DataAtomicSpy("repeatMinKey",
		// "SOME_KEY"));
		childReference.addChild(new DataAtomicOldSpy("repeatMax", "15"));
		// childReference.addChild(new DataAtomicSpy("secret",
		// "true"));
		// childReference.addChild(new DataAtomicSpy("secretKey",
		// "SECRET_KEY"));
		// childReference.addChild(new DataAtomicSpy("readOnly",
		// "true"));
		// childReference.addChild(new DataAtomicSpy("readOnlyKey",
		// "READONLY_KEY"));
		childReferences.addChild(childReference);

		dataGroups.add(group);

		// collection
		DataGroup colVar2 = new DataGroupOldSpy("metadata");
		colVar2.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicOldSpy("id", "collectionVar2"));
		colVar2.addChild(recordInfo);

		colVar2.addChild(new DataAtomicOldSpy("nameInData", "collectionVar2"));
		addTextByNameInDataAndId(colVar2, "textId", "collectionVarText");
		addTextByNameInDataAndId(colVar2, "defTextId", "collectionVarDefText");

		DataGroup refCollection2 = new DataGroupOldSpy("refCollection");
		refCollection2.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataItemCollection"));
		refCollection2.addChild(new DataAtomicOldSpy("linkedRecordId", "authorityTypeCollection"));
		colVar2.addChild(refCollection2);

		dataGroups.add(colVar2);

		// itemCollection
		DataGroup authority = new DataGroupOldSpy("metadata");
		authority.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup authorityRecordInfo = new DataGroupOldSpy("recordInfo");
		authorityRecordInfo.addChild(new DataAtomicOldSpy("id", "authorityTypeCollection"));
		authority.addChild(authorityRecordInfo);

		authority.addChild(new DataAtomicOldSpy("nameInData", "authorityTypeCollection"));
		authority.addChild(createTextGroup("textId", "authorityTypeCollectionTextId"));
		authority.addChild(createTextGroup("defTextId", "authorityTypeCollectionDefTextId"));

		DataGroup authorityItemReferences = new DataGroupOldSpy("collectionItemReferences");

		createAndAddItemReference(authorityItemReferences, "person", "one");
		createAndAddItemReference(authorityItemReferences, "family", "two");
		createAndAddItemReference(authorityItemReferences, "organisation", "three");
		createAndAddItemReference(authorityItemReferences, "place", "four");
		authority.addChild(authorityItemReferences);
		dataGroups.add(authority);

		// personItem
		DataGroup personItem = new DataGroupOldSpy("metadata");
		personItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup personRecordInfo = new DataGroupOldSpy("recordInfo");
		personRecordInfo.addChild(new DataAtomicOldSpy("id", "person"));
		personItem.addChild(personRecordInfo);

		personItem.addChild(new DataAtomicOldSpy("nameInData", "person"));
		addTextByNameInDataAndId(personItem, "textId", "personTextId");
		addTextByNameInDataAndId(personItem, "defTextId", "personDefTextId");
		//
		dataGroups.add(personItem);

		// familyItem
		DataGroup familyItem = new DataGroupOldSpy("metadata");
		familyItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup familyRecordInfo = new DataGroupOldSpy("recordInfo");
		familyRecordInfo.addChild(new DataAtomicOldSpy("id", "family"));
		familyItem.addChild(familyRecordInfo);

		familyItem.addChild(new DataAtomicOldSpy("nameInData", "family"));
		addTextByNameInDataAndId(familyItem, "textId", "familyTextId");
		addTextByNameInDataAndId(familyItem, "defTextId", "familyDefTextId");
		dataGroups.add(familyItem);

		// organisationItem
		DataGroup organisationItem = new DataGroupOldSpy("metadata");
		organisationItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup organisationRecordInfo = new DataGroupOldSpy("recordInfo");
		organisationRecordInfo.addChild(new DataAtomicOldSpy("id", "organisation"));
		organisationItem.addChild(organisationRecordInfo);

		organisationItem.addChild(new DataAtomicOldSpy("nameInData", "organisation"));
		addTextByNameInDataAndId(organisationItem, "textId", "organisationTextId");
		addTextByNameInDataAndId(organisationItem, "defTextId", "organisationDefTextId");
		dataGroups.add(organisationItem);

		// placeItem
		DataGroup placeItem = new DataGroupOldSpy("metadata");
		placeItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup placeRecordInfo = new DataGroupOldSpy("recordInfo");
		placeRecordInfo.addChild(new DataAtomicOldSpy("id", "place"));
		placeItem.addChild(placeRecordInfo);

		placeItem.addChild(new DataAtomicOldSpy("nameInData", "place"));
		addTextByNameInDataAndId(placeItem, "textId", "placeTextId");
		addTextByNameInDataAndId(placeItem, "defTextId", "placeDefTextId");
		//
		dataGroups.add(placeItem);

		// linkedRecordId (textVar)
		DataGroup linkedRecordIdTextVar = new DataGroupOldSpy("metadata");
		linkedRecordIdTextVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup linkedRecordIdTextVarRecordInfo = new DataGroupOldSpy("recordInfo");
		linkedRecordIdTextVarRecordInfo
				.addChild(new DataAtomicOldSpy("id", "linkedRecordIdTextVar"));
		linkedRecordIdTextVar.addChild(linkedRecordIdTextVarRecordInfo);

		linkedRecordIdTextVar.addChild(new DataAtomicOldSpy("nameInData", "linkedRecordId"));
		addTextByNameInDataAndId(linkedRecordIdTextVar, "textId", "textVarText");
		addTextByNameInDataAndId(linkedRecordIdTextVar, "defTextId", "textVarDefText");
		linkedRecordIdTextVar.addChild(new DataAtomicOldSpy("regEx", "((.*)|^$){1}"));

		dataGroups.add(linkedRecordIdTextVar);

		addDataToDataMetadata();
		return dataGroups;
	}

	private void addTextByNameInDataAndId(DataGroup dataGroup, String nameInData, String textId) {
		DataGroup text = new DataGroupOldSpy(nameInData);
		text.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicOldSpy("linkedRecordId", textId));
		dataGroup.addChild(text);
	}

	private void createAndAddItemReference(DataGroup collectionItemReferences,
			String linkedRecordId, String repeatId) {
		DataGroup ref1 = new DataGroupOldSpy("ref");
		ref1.setRepeatId(repeatId);
		ref1.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataCollectionItem"));
		ref1.addChild(new DataAtomicOldSpy("linkedRecordId", linkedRecordId));
		collectionItemReferences.addChild(ref1);
	}

	private void addDataToDataMetadata() {
		/**
		 * {"name":"metadata","attributes":{"type":"recordLink"},"children":
		 * [{"name":"recordInfo","children":[{"name":"id","value":"testLink"}]},
		 * {"name":"nameInData","value":"testLink"},{"name":"textId","value":
		 * "testLinkText"},{"name":"defTextId","value":"testLinkDefText"},{
		 * "name":"linkedRecordType","value":"someRecordType"}]}
		 */
		// recordLink
		DataGroup testLinkGroup = new DataGroupOldSpy("metadata");
		testLinkGroup.addAttributeByIdWithValue("type", "recordLink");

		DataGroup testLinkGroupRecordInfo = new DataGroupOldSpy("recordInfo");
		testLinkGroupRecordInfo.addChild(new DataAtomicOldSpy("id", "testLink"));
		testLinkGroup.addChild(testLinkGroupRecordInfo);

		testLinkGroup.addChild(new DataAtomicOldSpy("nameInData", "testLink"));

		addTextByNameInDataAndId(testLinkGroup, "textId", "testLinkTextId");
		addTextByNameInDataAndId(testLinkGroup, "defTextId", "testLinkDefText");

		DataGroup linkedRecordType = new DataGroupOldSpy("linkedRecordType");
		linkedRecordType.addChild(new DataAtomicOldSpy("linkedRecordType", "recordType"));
		linkedRecordType.addChild(new DataAtomicOldSpy("linkedRecordId", "linkedRecordType1"));
		testLinkGroup.addChild(linkedRecordType);

		// testLinkGroup.addChild(
		// new DataAtomicSpy("linkedRecordType",
		// "linkedRecordType1"));
		dataGroups.add(testLinkGroup);

		/**
		 * {"name":"metadata","attributes":{"type":"group"},"children":[{"name":
		 * "recordInfo","children":[{"name":"id","value":"bush"}]},{"name":
		 * "textId","value":"bushText"},{"name":"defTextId","value":
		 * "bushDefText"},{"name":"nameInData","value":"bush"},{"name":
		 * "childReferences","children":[{"name":"childReference","children":[{
		 * "name":"ref","value":"recordInfo"},{"name":"repeatMin","value":"1"},{
		 * "name":"repeatMax","value":"1"}]},{"name":"childReference","children"
		 * :[{"name":"ref","value":"testLink"},{"name":"repeatMin","value":"1"},
		 * {"name":"repeatMax","value":"5"}]}]}]}
		 */
		// group
		DataGroup bushGroup = new DataGroupOldSpy("metadata");
		bushGroup.addAttributeByIdWithValue("type", "group");

		DataGroup groupRecordInfo = new DataGroupOldSpy("recordInfo");
		groupRecordInfo.addChild(new DataAtomicOldSpy("id", "bush"));
		bushGroup.addChild(groupRecordInfo);

		bushGroup.addChild(new DataAtomicOldSpy("nameInData", "bush"));
		addTextByNameInDataAndId(bushGroup, "textId", "bushTextId");
		addTextByNameInDataAndId(bushGroup, "defTextId", "bushDefText");

		DataGroup childReferences = new DataGroupOldSpy("childReferences");
		bushGroup.addChild(childReferences);

		DataGroup childReference = new DataGroupOldSpy("childReference");
		DataGroup ref = new DataGroupOldSpy("ref");
		ref.addAttributeByIdWithValue("type", "recordLink");
		ref.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataRecordLink"));
		ref.addChild(new DataAtomicOldSpy("linkedRecordId", "testLink"));
		childReference.addChild(ref);
		childReference.addChild(new DataAtomicOldSpy("repeatMin", "1"));
		// childReference.addChild(new DataAtomicSpy("repeatMax", "15"));
		childReference.addChild(new DataAtomicOldSpy("repeatMax", "1"));
		childReferences.addChild(childReference);

		dataGroups.add(bushGroup);
	}

	private DataGroup createTextGroup(String nameInData, String linkedRecordId) {
		DataGroup text = new DataGroupOldSpy(nameInData);
		text.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicOldSpy("linkedRecordId", linkedRecordId));
		return text;
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
		List<DataGroup> recordTypes = new ArrayList<>();
		DataGroup image = new DataGroupOldSpy("image");

		DataGroup recordInfo = createRecordInfoWithIdAndType("image", "recordType");
		image.addChild(recordInfo);

		DataGroup parentId = new DataGroupOldSpy("parentId");
		image.addChild(parentId);

		parentId.addChild(new DataAtomicOldSpy("linkedRecordType", "recordType"));
		parentId.addChild(new DataAtomicOldSpy("linkedRecordId", "binary"));
		recordTypes.add(image);
		return recordTypes;
	}

	private DataGroup createRecordInfoWithIdAndType(String id, String typeString) {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicOldSpy("id", id));
		DataGroup type = new DataGroupOldSpy("type");
		type.addChild(new DataAtomicOldSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicOldSpy("linkedRecordId", typeString));
		recordInfo.addChild(type);
		return recordInfo;
	}

	@Override
	public Collection<ValidationType> getValidationTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ValidationType> getValidationType(String validationId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Collection<DataGroup> getCollectTermsAsDataGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CollectTerm> getCollectTerms() {
		// TODO Auto-generated method stub
		return null;
	}

}
