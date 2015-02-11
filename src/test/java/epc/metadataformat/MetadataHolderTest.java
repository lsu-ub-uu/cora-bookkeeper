package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import epc.metadataformat.testdata.TestMetadataElementData;
import epc.metadataformat.testdata.TestTextData;

public class MetadataHolderTest {
	@Test
	public void testInit() {
		Map<String, TextElement> textElements = TestTextData
				.createTestTextElements();
		Map<String, MetadataElement> metadataElements = TestMetadataElementData
				.createTestMetadataElements();
		MetadataHolder metadataHolder = new MetadataHolder(textElements,
				metadataElements);

		assertEquals(metadataHolder.getTextElements(), textElements,
				"TextElements should be the same as the one set in the constructor");

		assertEquals(metadataHolder.getMetadataElements(), metadataElements,
				"MetadataElements should be the same as the one set in the constructor");

	}
}
