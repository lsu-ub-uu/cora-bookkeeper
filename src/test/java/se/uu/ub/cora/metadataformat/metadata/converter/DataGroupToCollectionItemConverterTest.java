package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.CollectionItem;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToCollectionItemConverter;

public class DataGroupToCollectionItemConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));

		DataGroupToCollectionItemConverter converter = DataGroupToCollectionItemConverter
				.fromDataGroup(dataGroup);
		CollectionItem collectionItem = converter.toMetadata();

		assertEquals(collectionItem.getId(), "otherId");
		assertEquals(collectionItem.getNameInData(), "other");
		assertEquals(collectionItem.getTextId(), "otherTextId");
		assertEquals(collectionItem.getDefTextId(), "otherDefTextId");
	}
}
