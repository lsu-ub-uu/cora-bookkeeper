package se.uu.ub.cora.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataRecordLinkTest {
	private DataRecordLink dataRecordLink;

	@BeforeMethod
	public void setUp() {
		dataRecordLink = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("nameInData",
				"recordType", "recordId");

	}

	@Test
	public void testInit() {
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getRecordType(), "recordType");
		assertEquals(dataRecordLink.getRecordId(), "recordId");
	}

	@Test
	public void testInitWithRepeatId() {
		dataRecordLink.setRepeatId("one");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getRecordType(), "recordType");
		assertEquals(dataRecordLink.getRecordId(), "recordId");
		assertEquals(dataRecordLink.getRepeatId(), "one");
	}

	@Test
	public void testInitWithLinkedRepeatId() {
		dataRecordLink.setLinkedRepeatId("x1");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getRecordType(), "recordType");
		assertEquals(dataRecordLink.getRecordId(), "recordId");
		assertEquals(dataRecordLink.getLinkedRepeatId(), "x1");
	}

}
