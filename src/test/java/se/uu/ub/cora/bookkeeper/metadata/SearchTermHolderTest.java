package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class SearchTermHolderTest {
	@Test
	public void testInit() {
		SearchTermHolder searchTermHolder = new SearchTermHolder();
		DataGroup searchTerm = DataGroup.withNameInData("searchTerm");
		DataGroup recordInfo = createRecordInfoWithIdAndType("titleSearchTerm", "searchTerm");
		searchTerm.addChild(recordInfo);
		searchTerm.addChild(DataAtomic.withNameInDataAndValue("searchTermType", "final"));
		DataGroup searchFieldRef = DataGroup.withNameInData("searchFieldRef");
		searchFieldRef.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadata"));
		searchFieldRef.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "searchTitleTextVar"));

		searchTerm.addChild(searchFieldRef);

		searchTermHolder.addSearchTerm(searchTerm);

		assertEquals(searchTermHolder.getSearchTerm("titleSearchTerm"), searchTerm);

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
