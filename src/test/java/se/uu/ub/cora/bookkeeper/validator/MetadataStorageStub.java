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
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

public class MetadataStorageStub implements MetadataStorage {

	private List<DataGroup> dataGroups;

	@Override
	public Collection<DataGroup> getMetadataElements() {
		dataGroups = new ArrayList<>();

		// textVar2
		DataGroup textVar2 = DataGroup.withNameInData("metadata");
		textVar2.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVar2RecordInfo = DataGroup.withNameInData("recordInfo");
		textVar2RecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "textVar2"));
		textVar2.addChild(textVar2RecordInfo);

		textVar2.addChild(DataAtomic.withNameInDataAndValue("nameInData", "textVar2"));
		addTextByNameInDataAndId(textVar2, "textId", "textVarText");
		addTextByNameInDataAndId(textVar2, "defTextId", "textVarDefText");
		textVar2.addChild(DataAtomic.withNameInDataAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));
		dataGroups.add(textVar2);

		// groupTypeVar
		DataGroup groupTypeVar = DataGroup.withNameInData("metadata");
		groupTypeVar.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup groupTypeVarRecordInfo = DataGroup.withNameInData("recordInfo");
		groupTypeVarRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "groupTypeVar"));
		groupTypeVar.addChild(groupTypeVarRecordInfo);

		groupTypeVar.addChild(DataAtomic.withNameInDataAndValue("nameInData", "groupTypeVar"));
		addTextByNameInDataAndId(groupTypeVar, "textId", "groupTypeVarText");
		addTextByNameInDataAndId(groupTypeVar, "defTextId", "groupTypeVarDefText");

		DataGroup refCollection = DataGroup.withNameInData("refCollection");
		refCollection.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "groupTypeCollection"));
		groupTypeVar.addChild(refCollection);

		dataGroups.add(groupTypeVar);

		// groupType1
		DataGroup groupType1 = DataGroup.withNameInData("metadata");
		groupType1.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType1RecordInfo = DataGroup.withNameInData("recordInfo");
		groupType1RecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "groupType1"));
		groupType1.addChild(groupType1RecordInfo);

		groupType1.addChild(DataAtomic.withNameInDataAndValue("nameInData", "groupType1"));

		dataGroups.add(groupType1);

		addTextByNameInDataAndId(groupType1, "textId", "groupType1Text");
		addTextByNameInDataAndId(groupType1, "defTextId", "groupType1DefText");

		// groupType2
		DataGroup groupType2 = DataGroup.withNameInData("metadata");
		groupType2.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup groupType2RecordInfo = DataGroup.withNameInData("recordInfo");
		groupType2RecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "groupType2"));
		groupType2.addChild(groupType2RecordInfo);

		groupType2.addChild(DataAtomic.withNameInDataAndValue("nameInData", "groupType2"));
		addTextByNameInDataAndId(groupType2, "textId", "groupType2Text");
		addTextByNameInDataAndId(groupType2, "defTextId", "groupType2DefText");
		dataGroups.add(groupType2);

		// groupTypeCollection
		DataGroup groupTypeCollection = DataGroup.withNameInData("metadata");
		groupTypeCollection.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup groupTypeCollectionRecordInfo = DataGroup.withNameInData("recordInfo");
		groupTypeCollectionRecordInfo
				.addChild(DataAtomic.withNameInDataAndValue("id", "groupTypeCollection"));
		groupTypeCollection.addChild(groupTypeCollectionRecordInfo);

		groupTypeCollection
				.addChild(DataAtomic.withNameInDataAndValue("nameInData", "groupTypeCollection"));
		addTextByNameInDataAndId(groupTypeCollection, "textId", "groupTypeCollectionText");
		addTextByNameInDataAndId(groupTypeCollection, "defTextId", "groupTypeCollectionDefText");

		DataGroup collectionItemReferences = DataGroup.withNameInData("collectionItemReferences");

		createAndAddItemReference(collectionItemReferences, "groupType1", "one");
		createAndAddItemReference(collectionItemReferences, "groupType2", "two");

		groupTypeCollection.addChild(collectionItemReferences);

		dataGroups.add(groupTypeCollection);

		// child (textVar)
		DataGroup textVar = DataGroup.withNameInData("metadata");
		textVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup textVarRecordInfo = DataGroup.withNameInData("recordInfo");
		textVarRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "textVarId"));
		textVar.addChild(textVarRecordInfo);

		textVar.addChild(DataAtomic.withNameInDataAndValue("nameInData", "textVarNameInData"));
		addTextByNameInDataAndId(textVar, "textId", "textVarText");
		addTextByNameInDataAndId(textVar, "defTextId", "textVarDefText");
		textVar.addChild(DataAtomic.withNameInDataAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		dataGroups.add(textVar);

		// group
		DataGroup group = DataGroup.withNameInData("metadata");
		group.addAttributeByIdWithValue("type", "group");

		DataGroup groupRecordInfo = DataGroup.withNameInData("recordInfo");
		groupRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "group"));
		group.addChild(groupRecordInfo);

		group.addChild(DataAtomic.withNameInDataAndValue("nameInData", "group"));
		addTextByNameInDataAndId(group, "textId", "groupTextId");
		addTextByNameInDataAndId(group, "defTextId", "groupDefText");

		DataGroup attributeReferences = DataGroup.withNameInData("attributeReferences");
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref", "groupTypeVar"));
		group.addChild(attributeReferences);

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		group.addChild(childReferences);

		DataGroup childReference = DataGroup.withNameInData("childReference");
		DataGroup ref = DataGroup.withNameInData("ref");
		ref.addAttributeByIdWithValue("type", "textVariable");
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataTextVariable"));
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "textVarId"));
		childReference.addChild(ref);
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "1"));
		// childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey",
		// "SOME_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "15"));
		// childReference.addChild(DataAtomic.withNameInDataAndValue("secret",
		// "true"));
		// childReference.addChild(DataAtomic.withNameInDataAndValue("secretKey",
		// "SECRET_KEY"));
		// childReference.addChild(DataAtomic.withNameInDataAndValue("readOnly",
		// "true"));
		// childReference.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey",
		// "READONLY_KEY"));
		childReferences.addChild(childReference);

		dataGroups.add(group);

		// collection
		DataGroup colVar2 = DataGroup.withNameInData("metadata");
		colVar2.addAttributeByIdWithValue("type", "collectionVariable");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "collectionVar2"));
		colVar2.addChild(recordInfo);

		colVar2.addChild(DataAtomic.withNameInDataAndValue("nameInData", "collectionVar2"));
		addTextByNameInDataAndId(colVar2, "textId", "collectionVarText");
		addTextByNameInDataAndId(colVar2, "defTextId", "collectionVarDefText");

		DataGroup refCollection2 = DataGroup.withNameInData("refCollection");
		refCollection2.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataItemCollection"));
		refCollection2.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "authorityTypeCollection"));
		colVar2.addChild(refCollection2);

		dataGroups.add(colVar2);

		// itemCollection
		DataGroup authority = DataGroup.withNameInData("metadata");
		authority.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup authorityRecordInfo = DataGroup.withNameInData("recordInfo");
		authorityRecordInfo
				.addChild(DataAtomic.withNameInDataAndValue("id", "authorityTypeCollection"));
		authority.addChild(authorityRecordInfo);

		authority.addChild(
				DataAtomic.withNameInDataAndValue("nameInData", "authorityTypeCollection"));
		authority.addChild(createTextGroup("textId", "authorityTypeCollectionTextId"));
		authority.addChild(createTextGroup("defTextId", "authorityTypeCollectionDefTextId"));

		DataGroup authorityItemReferences = DataGroup.withNameInData("collectionItemReferences");

		createAndAddItemReference(authorityItemReferences, "person", "one");
		createAndAddItemReference(authorityItemReferences, "family", "two");
		createAndAddItemReference(authorityItemReferences, "organisation", "three");
		createAndAddItemReference(authorityItemReferences, "place", "four");
		authority.addChild(authorityItemReferences);
		dataGroups.add(authority);

		// personItem
		DataGroup personItem = DataGroup.withNameInData("metadata");
		personItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup personRecordInfo = DataGroup.withNameInData("recordInfo");
		personRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "person"));
		personItem.addChild(personRecordInfo);

		personItem.addChild(DataAtomic.withNameInDataAndValue("nameInData", "person"));
		addTextByNameInDataAndId(personItem, "textId", "personTextId");
		addTextByNameInDataAndId(personItem, "defTextId", "personDefTextId");
		//
		dataGroups.add(personItem);

		// familyItem
		DataGroup familyItem = DataGroup.withNameInData("metadata");
		familyItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup familyRecordInfo = DataGroup.withNameInData("recordInfo");
		familyRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "family"));
		familyItem.addChild(familyRecordInfo);

		familyItem.addChild(DataAtomic.withNameInDataAndValue("nameInData", "family"));
		addTextByNameInDataAndId(familyItem, "textId", "familyTextId");
		addTextByNameInDataAndId(familyItem, "defTextId", "familyDefTextId");
		dataGroups.add(familyItem);

		// organisationItem
		DataGroup organisationItem = DataGroup.withNameInData("metadata");
		organisationItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup organisationRecordInfo = DataGroup.withNameInData("recordInfo");
		organisationRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "organisation"));
		organisationItem.addChild(organisationRecordInfo);

		organisationItem.addChild(DataAtomic.withNameInDataAndValue("nameInData", "organisation"));
		addTextByNameInDataAndId(organisationItem, "textId", "organisationTextId");
		addTextByNameInDataAndId(organisationItem, "defTextId", "organisationDefTextId");
		dataGroups.add(organisationItem);

		// placeItem
		DataGroup placeItem = DataGroup.withNameInData("metadata");
		placeItem.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup placeRecordInfo = DataGroup.withNameInData("recordInfo");
		placeRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "place"));
		placeItem.addChild(placeRecordInfo);

		placeItem.addChild(DataAtomic.withNameInDataAndValue("nameInData", "place"));
		addTextByNameInDataAndId(placeItem, "textId", "placeTextId");
		addTextByNameInDataAndId(placeItem, "defTextId", "placeDefTextId");
		//
		dataGroups.add(placeItem);

		addDataToDataMetadata();
		return dataGroups;
	}

	private void addTextByNameInDataAndId(DataGroup dataGroup, String nameInData, String textId) {
		DataGroup text = DataGroup.withNameInData(nameInData);
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", textId));
		dataGroup.addChild(text);
	}

	private void createAndAddItemReference(DataGroup collectionItemReferences,
			String linkedRecordId, String repeatId) {
		DataGroup ref1 = DataGroup.withNameInData("ref");
		ref1.setRepeatId(repeatId);
		ref1.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataCollectionItem"));
		ref1.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
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
		DataGroup testLinkGroup = DataGroup.withNameInData("metadata");
		testLinkGroup.addAttributeByIdWithValue("type", "recordLink");

		DataGroup testLinkGroupRecordInfo = DataGroup.withNameInData("recordInfo");
		testLinkGroupRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "testLink"));
		testLinkGroup.addChild(testLinkGroupRecordInfo);

		testLinkGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "testLink"));

		addTextByNameInDataAndId(testLinkGroup, "textId", "testLinkTextId");
		addTextByNameInDataAndId(testLinkGroup, "defTextId", "testLinkDefText");

		DataGroup linkedRecordType = DataGroup.withNameInData("linkedRecordType");
		linkedRecordType
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		linkedRecordType
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "linkedRecordType1"));
		testLinkGroup.addChild(linkedRecordType);

		// testLinkGroup.addChild(
		// DataAtomic.withNameInDataAndValue("linkedRecordType",
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
		DataGroup bushGroup = DataGroup.withNameInData("metadata");
		bushGroup.addAttributeByIdWithValue("type", "group");

		DataGroup groupRecordInfo = DataGroup.withNameInData("recordInfo");
		groupRecordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "bush"));
		bushGroup.addChild(groupRecordInfo);

		bushGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "bush"));
		addTextByNameInDataAndId(bushGroup, "textId", "bushTextId");
		addTextByNameInDataAndId(bushGroup, "defTextId", "bushDefText");

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		bushGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withNameInData("childReference");
		DataGroup ref = DataGroup.withNameInData("ref");
		ref.addAttributeByIdWithValue("type", "recordLink");
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataRecordLink"));
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "testLink"));
		childReference.addChild(ref);
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "1"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "15"));
		childReferences.addChild(childReference);

		dataGroups.add(bushGroup);
	}

	private DataGroup createTextGroup(String nameInData, String linkedRecordId) {
		DataGroup text = DataGroup.withNameInData(nameInData);
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getSearchTerms() {
		List<DataGroup> searchTerms = new ArrayList<>();

		DataGroup searchTerm = DataGroup.withNameInData("searchTerm");
		DataGroup recordInfo = createRecordInfoWithIdAndType("titleSearchTerm", "searchTerm");
		searchTerm.addChild(recordInfo);
		searchTerm.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
		DataGroup searchFieldRef = DataGroup.withNameInData("searchFieldRef");
		searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		searchFieldRef.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));

		searchTerm.addChild(searchFieldRef);

		searchTerms.add(searchTerm);

		DataGroup searchTerm2 = DataGroup.withNameInData("searchTerm");
		DataGroup recordInfo2 = createRecordInfoWithIdAndType("someNameSearchTerm", "searchTerm");
		searchTerm2.addChild(recordInfo2);
		searchTerm2.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
		DataGroup searchFieldRef2 = DataGroup.withNameInData("searchFieldRef");
		searchFieldRef2.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		searchFieldRef2.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));

		searchTerm2.addChild(searchFieldRef2);

		searchTerms.add(searchTerm2);
		return searchTerms;
	}

	private DataGroup createRecordInfoWithIdAndType(String id, String typeString) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", typeString));
		recordInfo.addChild(type);
		return recordInfo;
	}

}
