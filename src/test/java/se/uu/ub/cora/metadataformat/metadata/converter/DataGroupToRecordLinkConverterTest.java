package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.RecordLink;

public class DataGroupToRecordLinkConverterTest {
	private DataGroupToRecordLinkConverter converter;
	private DataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroupContainingRecordLink();
		converter = DataGroupToRecordLinkConverter.fromDataGroup(dataGroup);

	}

	private DataGroup createDataGroupContainingRecordLink() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordLink");
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
		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getId(), "otherId");
		assertEquals(recordLink.getNameInData(), "other");
		assertEquals(recordLink.getTextId(), "otherTextId");
		assertEquals(recordLink.getDefTextId(), "otherDefTextId");
		assertEquals(recordLink.getLinkedRecordType(), "someRecordType");
	}

	@Test
	public void testToMetadataWithLinkedPath() {
		dataGroup.addChild(DataGroup.withNameInData("linkedPath"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

}
