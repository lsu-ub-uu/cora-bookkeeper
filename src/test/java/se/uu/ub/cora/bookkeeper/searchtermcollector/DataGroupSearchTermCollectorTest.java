package se.uu.ub.cora.bookkeeper.searchtermcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataGroupSearchTermCollectorTest {
	private DataGroupSearchTermCollector collector;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageForSearchTermStub();
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

		DataGroup searchTerm = collectedSearchTerms.getFirstGroupWithNameInData("searchTerm");
		assertEquals(searchTerm.getFirstAtomicValueWithNameInData("searchTermValue"), "Some title");
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
