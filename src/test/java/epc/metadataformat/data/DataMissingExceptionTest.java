package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataMissingExceptionTest {
	@Test
	public void testInitWithMessage() {
		DataMissingException dataMissingException = new DataMissingException("Message");
		assertEquals(dataMissingException.getMessage(), "Message");
	}
}
