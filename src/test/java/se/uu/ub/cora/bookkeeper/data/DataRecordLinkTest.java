package se.uu.ub.cora.bookkeeper.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataRecordLinkTest {
	private DataRecordLink dataRecordLink;

	@BeforeMethod
	public void setUp() {
		dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData",
				"linkedRecordType", "linkedRecordId");

	}

	@Test
	public void testInit() {
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(dataRecordLink.getLinkedRecordId(), "linkedRecordId");
	}

	@Test
	public void testInitWithRepeatId() {
		dataRecordLink.setRepeatId("one");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(dataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertEquals(dataRecordLink.getRepeatId(), "one");
	}

	@Test
	public void testInitWithLinkedRepeatId() {
		dataRecordLink.setLinkedRepeatId("x1");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(dataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertEquals(dataRecordLink.getLinkedRepeatId(), "x1");
	}

	@Test
	public void testInitWithLinkedPath() {
		dataRecordLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(dataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertEquals(dataRecordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

}
