package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.testdata.TestDataMetadataElement;
import epc.metadataformat.testdata.TestDataTextElement;

public class CoherentMetadataTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() throws Exception {
		CoherentMetadata.usingTextHolderAndMetadataHolder(null, null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithTextElementsNull() throws Exception {
		MetadataHolder metadataHolder = TestDataMetadataElement.createTestMetadataElements();
		CoherentMetadata.usingTextHolderAndMetadataHolder(null, metadataHolder);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithMetadataElementsNull() throws Exception {
		TextHolder textHolder = TestDataTextElement.createTestTextElements();
		CoherentMetadata.usingTextHolderAndMetadataHolder(textHolder, null);
	}

	// @Test
	// public void testInitWithNull() {
	// CoherentMetadata coherentMetadata = new CoherentMetadata(null, null);
	//
	// }

	@Test
	public void testInit() {
		TextHolder textHolder = TestDataTextElement.createTestTextElements();
		MetadataHolder metadataHolder = TestDataMetadataElement.createTestMetadataElements();
		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextHolderAndMetadataHolder(
				textHolder, metadataHolder);

		assertEquals(coherentMetadata.getTextElements(), textHolder,
				"TextElements should be the same as the one set in the constructor");

		assertEquals(coherentMetadata.getMetadataElements(), metadataHolder,
				"MetadataElements should be the same as the one set in the constructor");

	}
}
