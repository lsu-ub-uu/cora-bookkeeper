package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataGroupToDataToDataLinkConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "dataToDataLink");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("targetRecordType", "someRecordType"));

		DataGroupToDataToDataLinkConverter converter = DataGroupToDataToDataLinkConverter
				.fromDataGroup(dataGroup);
		DataToDataLink dataToDataLink = converter.toMetadata();

		assertEquals(dataToDataLink.getId(), "otherId");
		assertEquals(dataToDataLink.getNameInData(), "other");
		assertEquals(dataToDataLink.getTextId(), "otherTextId");
		assertEquals(dataToDataLink.getDefTextId(), "otherDefTextId");
		assertEquals(dataToDataLink.getTargetRecordType(), "someRecordType");
	}
}
