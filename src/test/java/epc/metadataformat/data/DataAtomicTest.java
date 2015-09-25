package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataAtomicTest {
	@Test
	public void testInit() {
		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("nameInData", "value");
		assertEquals(dataAtomic.getNameInData(), "nameInData",
				"NameInData should be the one set in the constructor");
		assertEquals(dataAtomic.getValue(), "value", "Value should be "
				+ "the same as the value set in the constructor");
	}
}
