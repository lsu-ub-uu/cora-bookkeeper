package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariable;

public class DataGroupToCollectionVariableConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("refCollectionId", "refCollection"));

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertEquals(collectionVariable.getId(), "otherId");
		assertEquals(collectionVariable.getDataId(), "other");
		assertEquals(collectionVariable.getTextId(), "otherTextId");
		assertEquals(collectionVariable.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariable.getRefCollectionId(), "refCollection");

	}
}
