package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataGroupToDataToDataLinkConverterTest {
	private DataGroupToDataToDataLinkConverter converter;
	private DataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroupContainingDataToDataLink();
		converter = DataGroupToDataToDataLinkConverter.fromDataGroup(dataGroup);

	}

	private DataGroup createDataGroupContainingDataToDataLink() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "dataToDataLink");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		return dataGroup;
	}

	@Test
	public void testToMetadata() {
		DataToDataLink dataToDataLink = converter.toMetadata();

		assertEquals(dataToDataLink.getId(), "otherId");
		assertEquals(dataToDataLink.getNameInData(), "other");
		assertEquals(dataToDataLink.getTextId(), "otherTextId");
		assertEquals(dataToDataLink.getDefTextId(), "otherDefTextId");
		assertEquals(dataToDataLink.getLinkedRecordType(), "someRecordType");
	}

	@Test
	public void testToMetadataWithLinkedPath() {
		dataGroup.addChild(DataGroup.withNameInData("linkedPath"));

		DataToDataLink dataToDataLink = converter.toMetadata();

		assertEquals(dataToDataLink.getLinkedPath().getNameInData(), "linkedPath");
	}

}
