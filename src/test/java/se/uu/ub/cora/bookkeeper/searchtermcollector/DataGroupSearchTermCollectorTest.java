package se.uu.ub.cora.bookkeeper.searchtermcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.bookkeeper.validator.MetadataStorageStub;

public class DataGroupSearchTermCollectorTest {
	private DataGroupSearchTermCollector collector;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageForSearchTermStub();
//		MetadataStorageForSearchTermStub metadataHolderCreator = new MetadataStorageForSearchTermStub();

//		MetadataHolder metadataHolder = metadataHolderCreator.addMetadata();
		collector = new DataGroupSearchTermCollector(metadataStorage);
	}

	@Test
	public void testCollectSearchTermsNoTitle() {
		DataGroup book = createBookWithNoTitle();

		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertNull(collectedSearchTerms);
	}

	@Test
	public void testCollectSearchTermsWithTitle() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		DataGroup collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertEquals(collectedSearchTerms.getNameInData(), "searchData");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(collectedSearchTerms.getFirstAtomicValueWithNameInData("id"), "book1");

		// assertEquals(collectedSearchTerms.getChildren().size(), 1);
		DataGroup searchTerm = collectedSearchTerms.getFirstGroupWithNameInData("searchTerm");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermValue"), "Some title");
		// TODO: failar nu eftersom det som står i textfältet är
		// titleSearchTerm, dvs id på searchTerm
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermName"), "searchTitle");
	}

	// {"name": "searchData",
	// "children": [
	// {
	// "name": "type",
	// "value": "book"
	// },{
	// "name": "id",
	// "value": "book123"
	// },
	//
	// {
	// "name": "searchTerm",
	// "children": [
	//
	// {
	// "name": "searchTermName",
	// "value": "bookTitle"
	// },
	// {
	// "name": "searchTermValue",
	// "value": "Min titel på den här boken"
	// }
	// ]
	// },
	// {
	// "name": "searchTerm",
	// "children": [
	//
	// {
	// "name": "searchTermName",
	// "value": "freeText"
	// },
	// {
	// "name": "searchTermValue",
	// "value": "Min titel på den här boken"
	// }
	// ]
	// }
	// ]
	// }
	private DataGroup createBookWithNoTitle() {
		DataGroup book = DataGroup.withNameInData("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "book1"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "book"));
		DataGroup type = DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"type", "recordType", "book");
		recordInfo.addChild(type);
		DataGroup dataDivider = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("dataDivider",
						"system", "testSystem");
		recordInfo.addChild(dataDivider);
		return recordInfo;
	}

}
