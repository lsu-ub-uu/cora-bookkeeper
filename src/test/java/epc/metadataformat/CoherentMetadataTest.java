package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import epc.metadataformat.testdata.TestDataMetadataElement;
import epc.metadataformat.testdata.TestDataTextElement;

public class CoherentMetadataTest {
	@Test
	public void testInit() {
		Map<String, TextElement> textElements = TestDataTextElement
				.createTestTextElements();
		Map<String, MetadataElement> metadataElements = TestDataMetadataElement
				.createTestMetadataElements();
		CoherentMetadata coherentMetadata = new CoherentMetadata(textElements,
				metadataElements);

		assertEquals(coherentMetadata.getTextElements(), textElements,
				"TextElements should be the same as the one set in the constructor");

		assertEquals(coherentMetadata.getMetadataElements(), metadataElements,
				"MetadataElements should be the same as the one set in the constructor");
		
	}
}
