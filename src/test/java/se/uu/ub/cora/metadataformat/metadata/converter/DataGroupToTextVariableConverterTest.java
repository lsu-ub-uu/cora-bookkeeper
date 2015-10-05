package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;
import se.uu.ub.cora.metadataformat.metadata.converter.DataGroupToTextVariableConverter;

public class DataGroupToTextVariableConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "textVar");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}"));

		DataGroupToTextVariableConverter converter = DataGroupToTextVariableConverter
				.fromDataGroup(dataGroup);
		TextVariable textVariable = converter.toMetadata();

		assertEquals(textVariable.getId(), "otherId");
		assertEquals(textVariable.getNameInData(), "other");
		assertEquals(textVariable.getTextId(), "otherTextId");
		assertEquals(textVariable.getDefTextId(), "otherDefTextId");
		assertEquals(textVariable.getRegularExpression(),
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}");
	}
}
