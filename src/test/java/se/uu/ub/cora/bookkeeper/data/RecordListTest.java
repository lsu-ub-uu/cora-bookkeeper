package se.uu.ub.cora.bookkeeper.data;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

public class RecordListTest {
	@Test
	public void testInit() {
		String containsRecordTypes = "metadata";
		RecordList recordList = RecordList.withContainRecordsOfType(containsRecordTypes);
		assertEquals(recordList.getContainRecordsOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		DataRecord record = new DataRecord();
		recordList.addRecord(record);
		List<DataRecord> records = recordList.getRecords();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testTotalNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setTotalNo("2");
		assertEquals(recordList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setFromNo("0");
		assertEquals(recordList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setToNo("2");
		assertEquals(recordList.getToNo(), "2");
	}
}
