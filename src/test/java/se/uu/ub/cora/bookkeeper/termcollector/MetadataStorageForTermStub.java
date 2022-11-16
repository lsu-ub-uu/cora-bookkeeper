/*
 * Copyright 2015, 2019 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class MetadataStorageForTermStub implements MetadataStorageView {
	private List<DataGroup> dataGroups;
	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public Collection<DataGroup> getMetadataElements() {
		MCR.addCall();

		dataGroups = new ArrayList<>();

		DataGroup book = createBookMetadataGroup();
		dataGroups.add(book);

		DataGroup bookWithMoreCollectTerms = createBookWithMoreCollectTermsMetadataGroup();
		dataGroups.add(bookWithMoreCollectTerms);

		DataGroup bookWithStorageCollectTerms = createBookWithStorageCollectTermsMetadataGroup();
		dataGroups.add(bookWithStorageCollectTerms);

		DataGroup personRoleGroup = createPersonRoleGroup();
		dataGroups.add(personRoleGroup);

		DataGroup bookTitleTextVar = createBookTitleTextVar();
		dataGroups.add(bookTitleTextVar);
		DataGroup bookSubTitleTextVar = createSubBookTitleTextVar();
		dataGroups.add(bookSubTitleTextVar);
		DataGroup nameTextVar = createTextVariableWithIdAndNameInData("nameTextVar", "name");
		dataGroups.add(nameTextVar);
		DataGroup addressTextVar = createTextVariableWithIdAndNameInData("addressTextVar",
				"address");
		dataGroups.add(addressTextVar);

		DataGroup otherBookLink = createOtherBookLink();
		dataGroups.add(otherBookLink);

		MCR.addReturned(dataGroups);
		return dataGroups;
	}

	private DataGroup createBookMetadataGroup() {

		DataGroup book = new DataGroupOldSpy("metadata");
		book.addAttributeByIdWithValue("type", "group");
		DataGroup recordInfo = createRecordInfoWithIdAndType("bookGroup", "metadataGroup");
		book.addChild(recordInfo);
		book.addChild(new DataAtomicSpy("nameInData", "book"));
		addTextByNameInDataAndId(book, "textId", "bookTextId");
		addTextByNameInDataAndId(book, "defTextId", "bookDefTextId");

		DataGroup childReferences = createChildReferencesForBook();
		book.addChild(childReferences);
		return book;
	}

	private DataGroup createChildReferencesForBook() {
		DataGroup childReferences = new DataGroupOldSpy("childReferences");

		DataGroup childReferenceTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookTitleTextVar", "1", "1");
		DataGroup childRefTitleIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"titleIndexTerm", "0");
		childReferenceTitle.addChild(childRefTitleIndexTerm);
		childReferences.addChild(childReferenceTitle);

		DataGroup childReferencePersonRole = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"personRoleGroup", "1", "2");
		DataGroup childRefGroupIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"someGroupIndexTerm", "0");
		childReferencePersonRole.addChild(childRefGroupIndexTerm);
		childReferences.addChild(childReferencePersonRole);

		DataGroup childReferenceSubTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookSubTitleTextVar", "0", "5");
		DataGroup childRefSubTitleIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"subTitleIndexTerm", "0");
		childReferenceSubTitle.addChild(childRefSubTitleIndexTerm);
		childReferences.addChild(childReferenceSubTitle);

		DataGroup childReferenceOtherBook = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"otherBookLink", "0", "5");
		DataGroup childRefOtherBookIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"otherBookIndexTerm", "0");
		childReferenceOtherBook.addChild(childRefOtherBookIndexTerm);
		childReferences.addChild(childReferenceOtherBook);

		return childReferences;
	}

	private DataGroup createBookWithMoreCollectTermsMetadataGroup() {

		DataGroup book = new DataGroupOldSpy("metadata");
		book.addAttributeByIdWithValue("type", "group");
		DataGroup recordInfo = createRecordInfoWithIdAndType("bookWithMoreCollectTermsGroup",
				"metadataGroup");
		book.addChild(recordInfo);
		book.addChild(new DataAtomicSpy("nameInData", "book"));
		addTextByNameInDataAndId(book, "textId", "bookTextId");
		addTextByNameInDataAndId(book, "defTextId", "bookDefTextId");

		DataGroup childReferences = createChildReferencesForBookWithMoreCollectTerms();
		book.addChild(childReferences);
		return book;
	}

	private DataGroup createChildReferencesForBookWithMoreCollectTerms() {
		DataGroup childReferences = new DataGroupOldSpy("childReferences");

		DataGroup childReferenceTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookTitleTextVar", "1", "1");
		DataGroup childRefTitleIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"titleIndexTerm", "0");
		childReferenceTitle.addChild(childRefTitleIndexTerm);
		DataGroup childRefTitleIndexTerm2 = createCollectIndexTermWithNameAndRepeatId(
				"titleSecondIndexTerm", "0");
		childReferenceTitle.addChild(childRefTitleIndexTerm2);
		childReferences.addChild(childReferenceTitle);

		DataGroup childReferencePersonRole = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"personRoleGroup", "1", "2");
		DataGroup childRefGroupIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"someGroupIndexTerm", "0");
		childReferencePersonRole.addChild(childRefGroupIndexTerm);
		childReferences.addChild(childReferencePersonRole);

		DataGroup childReferenceSubTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookSubTitleTextVar", "0", "5");
		DataGroup childRefSubTitleIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"subTitleIndexTerm", "0");
		childReferenceSubTitle.addChild(childRefSubTitleIndexTerm);
		childReferences.addChild(childReferenceSubTitle);

		DataGroup childReferenceOtherBook = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"otherBookLink", "0", "5");
		DataGroup childRefOtherBookIndexTerm = createCollectIndexTermWithNameAndRepeatId(
				"otherBookIndexTerm", "0");
		childReferenceOtherBook.addChild(childRefOtherBookIndexTerm);
		DataGroup childRefOtherBookIndexTerm2 = createCollectIndexTermWithNameAndRepeatId(
				"otherBookSecondIndexTerm", "0");
		childReferenceOtherBook.addChild(childRefOtherBookIndexTerm2);
		childReferences.addChild(childReferenceOtherBook);

		return childReferences;
	}

	private DataGroup createBookWithStorageCollectTermsMetadataGroup() {

		DataGroup book = new DataGroupOldSpy("metadata");
		book.addAttributeByIdWithValue("type", "group");
		DataGroup recordInfo = createRecordInfoWithIdAndType("bookWithStorageCollectTermsGroup",
				"metadataGroup");
		book.addChild(recordInfo);
		book.addChild(new DataAtomicSpy("nameInData", "book"));
		addTextByNameInDataAndId(book, "textId", "bookTextId");
		addTextByNameInDataAndId(book, "defTextId", "bookDefTextId");

		DataGroup childReferences = createChildReferencesForBookWithStorageCollectTerms();
		book.addChild(childReferences);
		return book;
	}

	private DataGroup createChildReferencesForBookWithStorageCollectTerms() {
		DataGroup childReferences = new DataGroupOldSpy("childReferences");

		DataGroup childReferenceTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookTitleTextVar", "1", "1");
		DataGroup childRefTitleIndexTerm = createCollectStorageTermWithNameAndRepeatId(
				"titleStorageTerm", "0");
		childReferenceTitle.addChild(childRefTitleIndexTerm);
		DataGroup childRefTitleIndexTerm2 = createCollectStorageTermWithNameAndRepeatId(
				"titleSecondStorageTerm", "0");
		childReferenceTitle.addChild(childRefTitleIndexTerm2);
		childReferences.addChild(childReferenceTitle);

		DataGroup childReferencePersonRole = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"personRoleGroup", "1", "2");
		DataGroup childRefGroupIndexTerm = createCollectStorageTermWithNameAndRepeatId(
				"someGroupStorageTerm", "0");
		childReferencePersonRole.addChild(childRefGroupIndexTerm);
		childReferences.addChild(childReferencePersonRole);

		DataGroup childReferenceSubTitle = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookSubTitleTextVar", "0", "5");
		DataGroup childRefSubTitleIndexTerm = createCollectStorageTermWithNameAndRepeatId(
				"subTitleStorageTerm", "0");
		childReferenceSubTitle.addChild(childRefSubTitleIndexTerm);
		childReferences.addChild(childReferenceSubTitle);

		DataGroup childReferenceOtherBook = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"otherBookLink", "0", "5");
		DataGroup childRefOtherBookIndexTerm = createCollectStorageTermWithNameAndRepeatId(
				"otherBookStorageTerm", "0");
		childReferenceOtherBook.addChild(childRefOtherBookIndexTerm);
		DataGroup childRefOtherBookIndexTerm2 = createCollectStorageTermWithNameAndRepeatId(
				"otherBookSecondStorageTerm", "0");
		childReferenceOtherBook.addChild(childRefOtherBookIndexTerm2);
		childReferences.addChild(childReferenceOtherBook);

		return childReferences;
	}

	private DataGroup createChildReferenceWithIdRepeatMinAndRepeatMax(String id, String repeatMin,
			String repeatMax) {
		DataGroup childReference = new DataGroupOldSpy("childReference");
		DataGroup ref = new DataGroupOldSpy("ref");
		ref.addChild(new DataAtomicSpy("linkedRecordType", "metadata"));
		ref.addChild(new DataAtomicSpy("linkedRecordId", id));
		childReference.addChild(ref);
		childReference.addChild(new DataAtomicSpy("repeatMin", repeatMin));
		childReference.addChild(new DataAtomicSpy("repeatMax", repeatMax));
		return childReference;
	}

	private DataGroup createCollectIndexTermWithNameAndRepeatId(String collectIndexTermId,
			String repeatId) {
		DataGroup childRefCollectTerm = new DataGroupOldSpy("childRefCollectTerm");
		childRefCollectTerm.addChild(new DataAtomicSpy("linkedRecordType", "collectIndexTerm"));
		childRefCollectTerm.addChild(new DataAtomicSpy("linkedRecordId", collectIndexTermId));
		childRefCollectTerm.setRepeatId(repeatId);
		childRefCollectTerm.addAttributeByIdWithValue("type", "index");
		return childRefCollectTerm;
	}

	private DataGroup createCollectPermissionTerm(String collectPermissionTermId) {
		DataGroup childRefCollectTerm = new DataGroupOldSpy("childRefCollectTerm");
		childRefCollectTerm
				.addChild(new DataAtomicSpy("linkedRecordType", "collectPermissionTerm"));
		childRefCollectTerm.addChild(new DataAtomicSpy("linkedRecordId", collectPermissionTermId));
		childRefCollectTerm.addAttributeByIdWithValue("type", "permission");
		return childRefCollectTerm;
	}

	private DataGroup createCollectStorageTermWithNameAndRepeatId(String collectIndexTermId,
			String repeatId) {
		DataGroup childRefCollectTerm = new DataGroupOldSpy("childRefCollectTerm");
		childRefCollectTerm.addChild(new DataAtomicSpy("linkedRecordType", "collectStorageTerm"));
		childRefCollectTerm.addChild(new DataAtomicSpy("linkedRecordId", collectIndexTermId));
		childRefCollectTerm.setRepeatId(repeatId);
		childRefCollectTerm.addAttributeByIdWithValue("type", "storage");
		return childRefCollectTerm;
	}

	private DataGroup createTextVariableWithIdAndNameInData(String id, String nameInData) {
		DataGroup textVar = new DataGroupOldSpy("metadata");
		textVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup recordInfo = createRecordInfoWithIdAndType(id, "textVariable");
		textVar.addChild(recordInfo);
		textVar.addChild(new DataAtomicSpy("nameInData", nameInData));
		textVar.addChild(
				new DataAtomicSpy("regEx", "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));
		addTextByNameInDataAndId(textVar, "textId", id + "TextId");
		addTextByNameInDataAndId(textVar, "defTextId", id + "DefTextId");
		return textVar;
	}

	private DataGroup createBookTitleTextVar() {
		DataGroup bookTitleTextVar = createTextVariableWithIdAndNameInData("bookTitleTextVar",
				"bookTitle");
		return bookTitleTextVar;
	}

	private DataGroup createSubBookTitleTextVar() {
		DataGroup bookSubTitleTextVar = createTextVariableWithIdAndNameInData("bookSubTitleTextVar",
				"bookSubTitle");
		return bookSubTitleTextVar;
	}

	private void addTextByNameInDataAndId(DataGroup dataGroup, String nameInData, String textId) {
		DataGroup text = new DataGroupOldSpy(nameInData);
		text.addChild(new DataAtomicSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicSpy("linkedRecordId", textId));
		dataGroup.addChild(text);
	}

	private DataGroup createPersonRoleGroup() {
		DataGroup personRoleGroup = new DataGroupOldSpy("metadata");
		personRoleGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = createRecordInfoWithIdAndType("personRoleGroup", "metadataGroup");
		personRoleGroup.addChild(recordInfo);

		personRoleGroup.addChild(new DataAtomicSpy("nameInData", "personRole"));
		addTextByNameInDataAndId(personRoleGroup, "textId", "personRoleTextId");
		addTextByNameInDataAndId(personRoleGroup, "defTextId", "personRoleDefTextId");

		DataGroup childReferences = new DataGroupOldSpy("childReferences");
		DataGroup childReferenceName = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"nameTextVar", "1", "1");

		DataGroup refIndexTerm = createCollectIndexTermWithNameAndRepeatId("nameIndexTerm", "0");
		childReferenceName.addChild(refIndexTerm);
		childReferenceName.addChild(createCollectPermissionTerm("namePermissionTerm"));
		childReferences.addChild(childReferenceName);

		DataGroup childReferenceAddress = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"addressTextVar", "1", "1");
		childReferences.addChild(childReferenceAddress);

		personRoleGroup.addChild(childReferences);
		return personRoleGroup;
	}

	private DataGroup createOtherBookLink() {
		DataGroup otherBookLink = new DataGroupOldSpy("metadata");
		otherBookLink.addAttributeByIdWithValue("type", "recordLink");

		DataGroup recordInfo = createRecordInfoWithIdAndType("otherBookLink", "metadataRecordLink");
		otherBookLink.addChild(recordInfo);

		otherBookLink.addChild(new DataAtomicSpy("nameInData", "otherBook"));
		addTextByNameInDataAndId(otherBookLink, "textId", "otherBookLinkText");
		addTextByNameInDataAndId(otherBookLink, "defTextId", "otherBookLinkDefText");

		addLinkToGroupByNameInDataAndId(otherBookLink, "linkedRecordType", "book");
		return otherBookLink;
	}

	private void addLinkToGroupByNameInDataAndId(DataGroup dataGroup, String nameInData,
			String textId) {
		DataGroup text = new DataGroupOldSpy(nameInData);
		text.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		text.addChild(new DataAtomicSpy("linkedRecordId", textId));
		dataGroup.addChild(text);
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		return null;
	}

	@Override
	public Collection<DataGroup> getTexts() {
		return null;
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		return null;
	}

	@Override
	public Collection<DataGroup> getCollectTerms() {
		List<DataGroup> collectTerms = new ArrayList<>();

		DataGroup titleIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"titleIndexTerm", "indexTypeString", "title");
		collectTerms.add(titleIndexTerm);

		DataGroup titleSecondIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"titleSecondIndexTerm", "indexTypeString", "title");
		collectTerms.add(titleSecondIndexTerm);

		DataGroup nameIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"nameIndexTerm", "indexTypeString", "name");
		collectTerms.add(nameIndexTerm);

		DataGroup subTitleIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"subTitleIndexTerm", "indexTypeString", "subTitle");
		collectTerms.add(subTitleIndexTerm);

		DataGroup textIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"textIndexTerm", "indexTypeString", "text");
		collectTerms.add(textIndexTerm);

		DataGroup otherBookIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"otherBookIndexTerm", "indexTypeId", "otherBook");
		collectTerms.add(otherBookIndexTerm);

		DataGroup otherBookSecondIndexTerm = createIndexTermMetadataWithIdAndIndexTypeAndNameInData(
				"otherBookSecondIndexTerm", "indexTypeId", "otherBook");
		collectTerms.add(otherBookSecondIndexTerm);

		DataGroup namePermissionTerm = createPermissionTermMetadataWithIdAndPermissionKeyAndNameInData(
				"namePermissionTerm", "PERMISSIONFORNAME", "name");
		collectTerms.add(namePermissionTerm);

		DataGroup titleStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"titleStorageTerm", "storageTypeString", "STORAGEKEY_titleStorageTerm");
		collectTerms.add(titleStorageTerm);

		DataGroup titleSecondStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"titleSecondStorageTerm", "storageTypeString", "STORAGEKEY_titleSecondStorageTerm");
		collectTerms.add(titleSecondStorageTerm);

		DataGroup nameStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"nameStorageTerm", "storageTypeString", "STORAGEKEY_nameStorageTerm");
		collectTerms.add(nameStorageTerm);

		DataGroup subTitleStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"subTitleStorageTerm", "storageTypeString", "STORAGEKEY_subTitleStorageTerm");
		collectTerms.add(subTitleStorageTerm);

		DataGroup textStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"textStorageTerm", "storageTypeString", "STORAGEKEY_textStorageTerm");
		collectTerms.add(textStorageTerm);

		DataGroup otherBookStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"otherBookStorageTerm", "storageTypeId", "STORAGEKEY_otherBookStorageTerm");
		collectTerms.add(otherBookStorageTerm);

		DataGroup otherBookSecondStorageTerm = createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
				"otherBookSecondStorageTerm", "storageTypeId",
				"STORAGEKEY_otherBookSecondStorageTerm");
		collectTerms.add(otherBookSecondStorageTerm);
		return collectTerms;
	}

	private DataGroup createIndexTermMetadataWithIdAndIndexTypeAndNameInData(String id,
			String indexType, String nameInData) {
		String recordType = "collectIndexTerm";
		DataGroup titleIndexTerm = createCollectTermMetadataWithIdAndCollectTypeAndNameInDataAndRecordType(
				id, "index", nameInData, recordType);
		DataGroup extraData = new DataGroupOldSpy("extraData");
		extraData.addChild(new DataAtomicSpy("indexFieldName", "indexFieldNameForId:" + id));
		extraData.addChild(new DataAtomicSpy("indexType", indexType));
		titleIndexTerm.addChild(extraData);
		return titleIndexTerm;
	}

	private DataGroup createCollectTermMetadataWithIdAndCollectTypeAndNameInDataAndRecordType(
			String id, String collectType, String nameInData, String recordType) {
		DataGroup indexTerm = new DataGroupOldSpy("collectTerm");
		indexTerm.addAttributeByIdWithValue("type", collectType);
		DataGroup recordInfo = createRecordInfoWithIdAndType(id, recordType);
		indexTerm.addChild(recordInfo);
		indexTerm.addChild(new DataAtomicSpy("nameInData", nameInData));
		return indexTerm;
	}

	private DataGroup createPermissionTermMetadataWithIdAndPermissionKeyAndNameInData(String id,
			String permissionName, String nameInData) {
		String recordType = "collectPermissionTerm";
		DataGroup permissionTerm = createCollectTermMetadataWithIdAndCollectTypeAndNameInDataAndRecordType(
				id, "permission", nameInData, recordType);
		DataGroup extraData = new DataGroupOldSpy("extraData");
		extraData.addChild(new DataAtomicSpy("permissionKey", permissionName));
		permissionTerm.addChild(extraData);
		return permissionTerm;
	}

	private DataGroup createStorageTermMetadataWithTermIdAndTermValueAndStorageKey(
			String collectTermId, String collectTermValue, String storageKey) {
		String recordType = "collectStorageTerm";
		DataGroup storageTerm = createCollectTermMetadataWithIdAndCollectTypeAndNameInDataAndRecordType(
				collectTermId, "storage", collectTermValue, recordType);
		DataGroup extraData = new DataGroupOldSpy("extraData");
		extraData.addChild(new DataAtomicSpy("storageKey", storageKey));
		storageTerm.addChild(extraData);
		return storageTerm;
	}

	private DataGroup createRecordInfoWithIdAndType(String id, String typeString) {
		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", id));
		DataGroup type = new DataGroupOldSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", typeString));
		recordInfo.addChild(type);
		return recordInfo;
	}

}
