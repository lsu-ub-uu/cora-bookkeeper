package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataAtomicTest {
	@Test
	public void testInit() {
		DataAtomic dataAtomic = new DataAtomic("dataId", "value");
		assertEquals(dataAtomic.getDataId(), "dataId",
				"DataId should be the one set in the constructor");
		assertEquals(dataAtomic.getValue(), "value", "Value should be "
				+ "the same as the value set in the constructor");
	}
}
