package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetadataStorageForSearchTermStub implements MetadataStorage {
	private List<DataGroup> dataGroups;


	@Override
	public Collection<DataGroup> getMetadataElements() {
		dataGroups = new ArrayList<>();

		DataGroup book = createBookDataGroup();
		dataGroups.add(book);

		// DataGroup searchTerm = createSearchTerm();
		// dataGroups.add(searchTerm);

		DataGroup searchTitleTextVar = createSearchTitleTextVar();
		dataGroups.add(searchTitleTextVar);
		DataGroup bookTitleTextVar = createBookTitleTextVar();
		dataGroups.add(bookTitleTextVar);
		return dataGroups;
	}

	// private DataGroup createSearchTerm() {
	// MetadataGroup searchTerm = TestMetadataCreator
	// .createMetadataGroupWithIdAndNameInData("titleSearchTerm",
	// "titleSearchTerm");
	// searchTerm.addChild(DataAtomic.withNameInDataAndValue("searchTermType",
	// "final"));
	// DataGroup searchFieldRef = DataGroup.withNameInData("searchFieldRef");
	// searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType",
	// "metadata"));
	// searchFieldRef.addChild(
	// DataAtomic.withNameInDataAndValue("linkedRecordId",
	// "searchTitleTextVar"));
	// searchTerm.addChild(searchFieldRef);
	// return searchTerm;
	// }

	private DataGroup createBookDataGroup() {

		DataGroup book = DataGroup.withNameInData("metadata");
		book.addAttributeByIdWithValue("type", "group");
		DataGroup recordInfo = createRecordInfoWithIdAndType("bookGroup", "book");
//				DataGroup.withNameInData("recordInfo");
//		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "bookGroup"));
		book.addChild(recordInfo);
		book.addChild(DataAtomic.withNameInDataAndValue("nameInData", "book"));
		addTextByNameInDataAndId(book, "textId", "bookTextId");
		addTextByNameInDataAndId(book, "defTextId", "bookDefTextId");

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		DataGroup childReference = createChildReferenceForBook();
		childReferences.addChild(childReference);
		book.addChild(childReferences);
		return book;
	}

	private DataGroup createChildReferenceForBook() {
		DataGroup childReference = DataGroup.withNameInData("childReference");
		DataGroup ref = DataGroup.withNameInData("ref");
		ref.addAttributeByIdWithValue("type", "textVariable");
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataTextVariable"));
		ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "bookTitleTextVar"));
		childReference.addChild(ref);
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "1"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "1"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("searchTerm","titleSearchTerm" ));

		return childReference;
	}

	// private DataGroup createLinkWithNameInDataLinkedTypeAndLinkeId(String
	// nameInData,
	// String recordType, String recordId) {
	// DataGroup ref = DataGroup.withNameInData(nameInData);
	// ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType",
	// recordType));
	// ref.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId",
	// recordId));
	// return ref;
	// }

	private DataGroup createSearchTitleTextVar() {
		DataGroup searchTitleTextVar = createTextVariableWithIdAndNameInData("searchTitleTextVar", "searchTitle");
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
		addTextByNameInDataAndId(textVar, "textId", id+"TextId");
		addTextByNameInDataAndId(textVar, "defTextId", id+"DefTextId");
		return textVar;
	}

	private DataGroup createBookTitleTextVar() {
		DataGroup bookTitleTextVar = createTextVariableWithIdAndNameInData("bookTitleTextVar", "bookTitle");
		return bookTitleTextVar;
	}

	private void addTextByNameInDataAndId(DataGroup dataGroup, String nameInData, String textId) {
		DataGroup text = DataGroup.withNameInData(nameInData);
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", textId));
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
