package se.uu.ub.cora.bookkeeper.termcollector;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;

public class CollectedDataCreatorTest {

	@Test
	public void testCreateCollectedDataNoCollectTerms() {
		CollectedDataCreatorImp dataCreator = new CollectedDataCreatorImp();
		DataGroup dataGroup = createBasicDataGroup();
		Map<String, List<DataGroup>> collectedTerms = new HashMap<>();
		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(collectedTerms, dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 2);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");
	}

	@Test
	public void testCreateCollectedDataOneCollectTermAtomicValue() {
		CollectedDataCreatorImp dataCreator = new CollectedDataCreatorImp();
		DataGroup dataGroup = createBasicDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("bookTitle", "Some title"));

		Map<String, List<DataGroup>> collectedTerms = new HashMap<>();
		DataGroup dataTerm = DataGroup.withNameInData("collectedDataTerm");
		dataTerm.addChild(DataAtomic.withNameInDataAndValue("collectTermId", "titleIndexTerm"));
		dataTerm.addChild(DataAtomic.withNameInDataAndValue("collectTermValue", "Some title"));
		List<DataGroup> dataGroups = new ArrayList<>();
		dataGroups.add(dataTerm);
		collectedTerms.put("index", dataGroups);

		DataGroup result = dataCreator
				.createCollectedDataFromCollectedTermsAndRecord(collectedTerms, dataGroup);

		assertEquals(result.getNameInData(), "collectedData");
		assertEquals(result.getChildren().size(), 3);
		assertEquals(result.getFirstAtomicValueWithNameInData("type"), "book");
		assertEquals(result.getFirstAtomicValueWithNameInData("id"), "book:111");
		DataGroup indexDataGroup = result.getFirstGroupWithNameInData("index");
		DataGroup collectedDataTerm = indexDataGroup
				.getFirstGroupWithNameInData("collectedDataTerm");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermId"),
				"titleIndexTerm");
		assertEquals(collectedDataTerm.getFirstAtomicValueWithNameInData("collectTermValue"),
				"Some title");
	}

	private DataGroup createBasicDataGroup() {
		DataGroup book = DataGroup.withNameInData("book");
		DataGroup recordInfo = createRecordInfo("book", "book:111", "testSystem");
		book.addChild(recordInfo);

		return book;
	}

	private DataGroup createRecordInfo(String type, String id, String dataDivider) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		DataGroup typeGroup = DataGroup.asLinkWithNameInDataAndTypeAndId("type", "recordType",
				type);
		recordInfo.addChild(typeGroup);
		DataGroup dataDividerGroup = DataGroup.asLinkWithNameInDataAndTypeAndId("dataDivider",
				"system", dataDivider);
		recordInfo.addChild(dataDividerGroup);
		return recordInfo;
	}

}
