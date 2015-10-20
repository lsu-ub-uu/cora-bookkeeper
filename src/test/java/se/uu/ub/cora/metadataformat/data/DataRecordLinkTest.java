package se.uu.ub.cora.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataRecordLinkTest {
	@Test
	public void testInit() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "recordType", "recordId");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getRecordType(), "recordType");
		assertEquals(dataRecordLink.getRecordId(), "recordId");
	}

	@Test
	public void testInitWithRepeatId() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "recordType", "recordId");
		dataRecordLink.setRepeatId("one");
		assertEquals(dataRecordLink.getNameInData(), "nameInData");
		assertEquals(dataRecordLink.getRecordType(), "recordType");
		assertEquals(dataRecordLink.getRecordId(), "recordId");
		assertEquals(dataRecordLink.getRepeatId(), "one");
	}
}
