package se.uu.ub.cora.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataMissingException;

public class DataMissingExceptionTest {
	@Test
	public void testInitWithMessage() {
		DataMissingException dataMissingException = new DataMissingException("Message");
		assertEquals(dataMissingException.getMessage(), "Message");
	}
}
