package se.uu.ub.cora.metadataformat.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataGroup;

public class RecordLinkTest {
	private RecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
				"id", "nameInData", "textId", "defTextId", "linkedRecordType");
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getId(), "id");
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertEquals(recordLink.getTextId(), "textId");
		assertEquals(recordLink.getDefTextId(), "defTextId");
		assertEquals(recordLink.getLinkedRecordType(), "linkedRecordType");
	}

	@Test
	public void testInitWithPath() {
		recordLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));
		assertEquals(recordLink.getId(), "id");
		assertEquals(recordLink.getNameInData(), "nameInData");
		assertEquals(recordLink.getTextId(), "textId");
		assertEquals(recordLink.getDefTextId(), "defTextId");
		assertEquals(recordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(recordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

}
