package se.uu.ub.cora.metadataformat.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataToDataLinkTest {
	@Test
	public void testInit() {
		DataToDataLink dataToDataLink = DataToDataLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType("id", "nameInData",
						"textId", "defTextId", "targetRecordType");
		assertEquals(dataToDataLink.getId(), "id");
		assertEquals(dataToDataLink.getNameInData(), "nameInData");
		assertEquals(dataToDataLink.getTextId(), "textId");
		assertEquals(dataToDataLink.getDefTextId(), "defTextId");
		assertEquals(dataToDataLink.getTargetRecordType(), "targetRecordType");
	}
}
