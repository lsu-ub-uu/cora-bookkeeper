package se.uu.ub.cora.metadataformat.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataGroup;

public class DataToDataLinkTest {
	private DataToDataLink dataToDataLink;

	@BeforeMethod
	public void setUp() {
		dataToDataLink = DataToDataLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
				"id", "nameInData", "textId", "defTextId", "linkedRecordType");
	}

	@Test
	public void testInit() {
		assertEquals(dataToDataLink.getId(), "id");
		assertEquals(dataToDataLink.getNameInData(), "nameInData");
		assertEquals(dataToDataLink.getTextId(), "textId");
		assertEquals(dataToDataLink.getDefTextId(), "defTextId");
		assertEquals(dataToDataLink.getLinkedRecordType(), "linkedRecordType");
	}

	@Test
	public void testInitWithPath() {
		dataToDataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));
		assertEquals(dataToDataLink.getId(), "id");
		assertEquals(dataToDataLink.getNameInData(), "nameInData");
		assertEquals(dataToDataLink.getTextId(), "textId");
		assertEquals(dataToDataLink.getDefTextId(), "defTextId");
		assertEquals(dataToDataLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(dataToDataLink.getLinkedPath().getNameInData(), "linkedPath");
	}

}
