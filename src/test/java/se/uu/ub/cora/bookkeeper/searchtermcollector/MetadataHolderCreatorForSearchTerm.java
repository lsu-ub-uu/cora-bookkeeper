package se.uu.ub.cora.bookkeeper.searchtermcollector;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

public class MetadataHolderCreatorForSearchTerm {
	private MetadataHolder metadataHolder = new MetadataHolder();

	public MetadataHolder addMetadata() {

		MetadataGroup book = createBookDataGroup();
		metadataHolder.addMetadataElement(book);

		// DataGroup searchTerm = createSearchTerm();
		// dataGroups.add(searchTerm);

		TextVariable searchTitleTextVar = createSearchTitleTextVar();
		metadataHolder.addMetadataElement(searchTitleTextVar);
		TextVariable bookTitleTextVar = createBookTitleTextVar();
		metadataHolder.addMetadataElement(bookTitleTextVar);
		// return dataGroups;
		return metadataHolder;
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

	private MetadataGroup createBookDataGroup() {

		MetadataGroup book = TestMetadataCreator.createMetadataGroupWithIdAndNameInData("bookGroup",
				"book");
		// book.addAttributeByIdWithValue("type", "group");
		// book.addChild(DataAtomic.withNameInDataAndValue("nameInData",
		// "book"));
		MetadataChildReference childReferences = createChildReferenceForBook();
		book.addChildReference(childReferences);
		return book;
	}

	private MetadataChildReference createChildReferenceForBook() {
		MetadataChildReference childReference = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadata",
						"bookTitleTextVar", 1, 1);
		childReference.addSearchTerm("titleSearchTerm");
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

	private TextVariable createSearchTitleTextVar() {
		TextVariable searchTitleTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("searchTitleTextVar",
						"searchTitle", "searchTitleText", "searchTitleDefText",
						"(^[0-9A-ZÅÄÖ	 a-zåäö:-_]{2,100}$)");
		return searchTitleTextVar;
	}

	private TextVariable createBookTitleTextVar() {
		TextVariable bookTitleTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("bookTitleTextVar",
						"bookTitle", "bookitleText", "bookTitleDefText",
						"(^[0-9A-ZÅÄÖ	 a-zåäö:-_]{2,100}$)");
		return bookTitleTextVar;
	}

}
