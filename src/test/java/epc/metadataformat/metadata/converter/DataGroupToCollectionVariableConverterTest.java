package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariable;

public class DataGroupToCollectionVariableConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refCollectionId", "refCollection"));

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertEquals(collectionVariable.getId(), "otherId");
		assertEquals(collectionVariable.getNameInData(), "other");
		assertEquals(collectionVariable.getTextId(), "otherTextId");
		assertEquals(collectionVariable.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariable.getRefCollectionId(), "refCollection");

	}
}
