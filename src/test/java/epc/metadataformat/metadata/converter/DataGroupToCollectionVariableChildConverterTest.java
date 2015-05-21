package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariableChild;

public class DataGroupToCollectionVariableChildConverterTest {
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
		dataGroup.addChild(DataAtomic.withDataIdAndValue("refParentId", "refParentId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("finalValue", "finalValue"));

		DataGroupToCollectionVariableChildConverter converter = DataGroupToCollectionVariableChildConverter
				.fromDataGroup(dataGroup);
		CollectionVariableChild collectionVariableChild = converter.toMetadata();

		assertEquals(collectionVariableChild.getId(), "otherId");
		assertEquals(collectionVariableChild.getDataId(), "other");
		assertEquals(collectionVariableChild.getTextId(), "otherTextId");
		assertEquals(collectionVariableChild.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariableChild.getRefCollectionId(), "refCollection");
		assertEquals(collectionVariableChild.getRefParentId(), "refParentId");
		assertEquals(collectionVariableChild.getFinalValue(), "finalValue");
	}
}
