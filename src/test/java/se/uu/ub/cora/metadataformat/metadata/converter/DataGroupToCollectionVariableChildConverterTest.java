package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariableChild;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToCollectionVariableChildConverter;

public class DataGroupToCollectionVariableChildConverterTest {
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
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "refParentId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("finalValue", "finalValue"));

		DataGroupToCollectionVariableChildConverter converter = DataGroupToCollectionVariableChildConverter
				.fromDataGroup(dataGroup);
		CollectionVariableChild collectionVariableChild = converter.toMetadata();

		assertEquals(collectionVariableChild.getId(), "otherId");
		assertEquals(collectionVariableChild.getNameInData(), "other");
		assertEquals(collectionVariableChild.getTextId(), "otherTextId");
		assertEquals(collectionVariableChild.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariableChild.getRefCollectionId(), "refCollection");
		assertEquals(collectionVariableChild.getRefParentId(), "refParentId");
		assertEquals(collectionVariableChild.getFinalValue(), "finalValue");
	}
}
