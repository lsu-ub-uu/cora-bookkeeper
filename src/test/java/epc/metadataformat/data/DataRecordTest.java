package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataRecordTest {
	private DataRecord dataRecord;

	@BeforeMethod
	public void beforeMethod() {
		dataRecord = new DataRecord();
	}

	@Test
	public void testKeys() {
		dataRecord.addKey("KEY");
		assertTrue(dataRecord.containsKey("KEY"));
	}

	@Test
	public void testDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataRecord.setDataGroup(dataGroup);
		assertEquals(dataRecord.getDataGroup(), dataGroup);
	}

	@Test
	public void testGetKeys() {
		dataRecord.addKey("KEY1");
		dataRecord.addKey("KEY2");
		Set<String> keys = dataRecord.getKeys();
		assertTrue(keys.contains("KEY1"));
		assertTrue(keys.contains("KEY2"));
	}

}
