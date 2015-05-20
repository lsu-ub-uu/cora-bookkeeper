package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.TextVariable;

public class DataGroupToTextVariableConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "textVar");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("deffTextId", "otherDeffTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}"));

		DataGroupToTextVariableConverter converter = DataGroupToTextVariableConverter
				.fromDataGroup(dataGroup);
		TextVariable textVariable = converter.toMetadata();

		assertEquals(textVariable.getId(), "otherId");
		assertEquals(textVariable.getDataId(), "other");
		assertEquals(textVariable.getTextId(), "otherTextId");
		assertEquals(textVariable.getDefTextId(), "otherDeffTextId");
		assertEquals(textVariable.getRegularExpression(),
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}");
	}
}
