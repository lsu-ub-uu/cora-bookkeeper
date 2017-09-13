package se.uu.ub.cora.bookkeeper.searchtermcollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

public class MetadataStorageForSearchTermStub implements MetadataStorage {
	private List<DataGroup> dataGroups;

	@Override
	public Collection<DataGroup> getMetadataElements() {
		dataGroups = new ArrayList<>();

		DataGroup book = createBookMetadataGroup();
		dataGroups.add(book);

		DataGroup searchTitleTextVar = createSearchTitleTextVar();
		dataGroups.add(searchTitleTextVar);
		DataGroup bookTitleTextVar = createBookTitleTextVar();
		dataGroups.add(bookTitleTextVar);
		DataGroup bookSubTitleTextVar = createSubBookTitleTextVar();
		dataGroups.add(bookSubTitleTextVar);
		DataGroup nameTextVar = createNameTextVar();
		dataGroups.add(nameTextVar);

		DataGroup personRoleGroup = createPersonRoleGroup();
		dataGroups.add(personRoleGroup);

		return dataGroups;
	}

	private DataGroup createBookMetadataGroup() {

		DataGroup book = DataGroup.withNameInData("metadata");
		book.addAttributeByIdWithValue("type", "group");
		DataGroup recordInfo = createRecordInfoWithIdAndType("bookGroup", "metadataGroup");
		book.addChild(recordInfo);
		book.addChild(DataAtomic.withNameInDataAndValue("nameInData", "book"));
		addTextByNameInDataAndId(book, "textId", "bookTextId");
		addTextByNameInDataAndId(book, "defTextId", "bookDefTextId");

		DataGroup childReferences = createChildReferencesForBook();
		book.addChild(childReferences);
		return book;
	}

	private DataGroup createChildReferencesForBook() {
		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		DataGroup childReference = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookTitleTextVar", "1", "1");

		DataGroup childRefSearchTerm = createSearchTerm("titleSearchTerm");
		childReference.addChild(childRefSearchTerm);
		childReferences.addChild(childReference);

		DataGroup childReference2 = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"personRoleGroup", "1", "2");
		DataGroup childRefSearchTerm2 = createSearchTerm("someGroupSearchTerm");
		childReference2.addChild(childRefSearchTerm2);
		childReferences.addChild(childReference2);

		DataGroup childReference3 = createChildReferenceWithIdRepeatMinAndRepeatMax(
				"bookSubTitleTextVar", "0", "1");
		childReferences.addChild(childReference3);

		return childReferences;
	}

	private DataGroup createChildReferenceWithIdRepeatMinAndRepeatMax(String id, String repeatMin,
			String repeatMax) {
		DataGroup childReference = DataGroup.withNameInData("childReference");
		DataGroup ref = DataGroup.withNameInData("ref");
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", id));
		childReference.addChild(ref);
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", repeatMin));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", repeatMax));
		return childReference;
	}

	private DataGroup createSearchTerm(String searchTermId) {
		DataGroup childRefSearchTerm = DataGroup.withNameInData("childRefSearchTerm");
		childRefSearchTerm
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "searchTerm"));
		childRefSearchTerm
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", searchTermId));
		childRefSearchTerm.setRepeatId("0");
		return childRefSearchTerm;
	}

	private DataGroup createSearchTitleTextVar() {
		DataGroup searchTitleTextVar = createTextVariableWithIdAndNameInData("searchTitleTextVar",
				"searchTitle");
		return searchTitleTextVar;
	}

	private DataGroup createTextVariableWithIdAndNameInData(String id, String nameInData) {
		DataGroup textVar = DataGroup.withNameInData("metadata");
		textVar.addAttributeByIdWithValue("type", "textVariable");

		DataGroup recordInfo = createRecordInfoWithIdAndType(id, "textVariable");
		textVar.addChild(recordInfo);
		textVar.addChild(DataAtomic.withNameInDataAndValue("nameInData", nameInData));
		textVar.addChild(DataAtomic.withNameInDataAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));
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
		DataGroup text = DataGroup.withNameInData(nameInData);
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", textId));
		dataGroup.addChild(text);
	}

	private DataGroup createNameTextVar() {
		DataGroup nameTextVar = createTextVariableWithIdAndNameInData("nameTextVar", "name");
		return nameTextVar;
	}

	private DataGroup createPersonRoleGroup() {
		DataGroup personRoleGroup = DataGroup.withNameInData("metadata");
		personRoleGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = createRecordInfoWithIdAndType("personRoleGroup", "metadataGroup");
		personRoleGroup.addChild(recordInfo);

		personRoleGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "personRole"));
		addTextByNameInDataAndId(personRoleGroup, "textId", "personRoleTextId");
		addTextByNameInDataAndId(personRoleGroup, "defTextId", "personRoleDefTextId");

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		DataGroup childReference = createChildReferenceWithIdRepeatMinAndRepeatMax("nameTextVar",
				"1", "1");

		DataGroup childRefSearchTerm = createSearchTerm("nameSearchTerm");
		childReference.addChild(childRefSearchTerm);

		childReferences.addChild(childReference);
		personRoleGroup.addChild(childReferences);
		return personRoleGroup;
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

		addIndexTypes(searchTerm);

		searchTerms.add(searchTerm);

		DataGroup searchTerm2 = DataGroup.withNameInData("searchTerm");
		DataGroup recordInfo2 = createRecordInfoWithIdAndType("nameSearchTerm", "searchTerm");
		searchTerm2.addChild(recordInfo2);
		searchTerm2.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
		DataGroup searchFieldRef2 = DataGroup.withNameInData("searchFieldRef");
		searchFieldRef2.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		searchFieldRef2.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));

		searchTerm2.addChild(searchFieldRef2);
		searchTerm2.addChild(createIndexTypeWithTypeStringAndRepeatId("indexTypeString", "0"));

		searchTerms.add(searchTerm2);
		return searchTerms;
	}

	private void addIndexTypes(DataGroup searchTerm) {
		DataAtomic indexType = createIndexTypeWithTypeStringAndRepeatId("indexTypeString", "0");
		searchTerm.addChild(indexType);

		DataAtomic indexType2 = createIndexTypeWithTypeStringAndRepeatId("indexTypeBoolean", "1");
		searchTerm.addChild(indexType2);
	}

	private DataAtomic createIndexTypeWithTypeStringAndRepeatId(String value, String repeatId) {
		return DataAtomic.withNameInDataAndValueAndRepeatId("indexType", value, repeatId);
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
