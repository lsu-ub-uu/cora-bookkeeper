package se.uu.ub.cora.bookkeeper.searchtermcollector;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataGroupSearchTermCollectorTest {
	private DataGroupSearchTermCollector collector;

	@BeforeMethod
	public void setUp() {
		MetadataHolderCreatorForSearchTerm metadataHolderCreator = new MetadataHolderCreatorForSearchTerm();

		MetadataHolder metadataHolder = metadataHolderCreator.addMetadata();
		collector = new DataGroupSearchTermCollector(metadataHolder);
	}

	@Test
	public void testCollectSearchTermsNoTitle() {
		DataGroup book = createBookWithNoTitle();

		List<DataGroup> collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertEquals(collectedSearchTerms.size(), 0);
	}

	@Test
	public void testCollectSearchTermsWithTitle() {
		DataGroup book = createBookWithNoTitle();
		book.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));
		List<DataGroup> collectedSearchTerms = collector.collectSearchTerms("bookGroup", book);
		assertEquals(collectedSearchTerms.size(), 1);
		DataGroup searchTerm = collectedSearchTerms.get(0);
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermValue"), "Some title");
		// TODO: failar nu eftersom det som står i textfältet är
		// titleSearchTerm, dvs id på searchTerm
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermName"), "searchTitle");
	}

	private DataGroup createBookWithNoTitle() {
		DataGroup book = DataGroup.withNameInData("book");
		DataGroup recordInfo = createRecordInfo();
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "book1"));
		DataGroup type = DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"type", "recordType", "book");
		recordInfo.addChild(type);
		DataGroup dataDivider = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("dataDivider",
						"system", "testSystem");
		recordInfo.addChild(dataDivider);
		return recordInfo;
	}

//	{"name": "searchData",
//			"children": [
//		{
//			"name": "type",
//				"value": "book"
//		},{
//		"name": "id",
//				"value": "book123"
//	},
//
//		{
//			"name": "searchTerm",
//				"children": [
//
//			{
//				"name": "searchTermName",
//					"value": "bookTitle"
//			},
//			{
//				"name": "searchTermValue",
//					"value": "Min titel på den här boken"
//			}
//			]
//		},
//		{
//			"name": "searchTerm",
//				"children": [
//
//			{
//				"name": "searchTermName",
//					"value": "freeText"
//			},
//			{
//				"name": "searchTermValue",
//					"value": "Min titel på den här boken"
//			}
//			]
//		}
//		]
//	}

}
