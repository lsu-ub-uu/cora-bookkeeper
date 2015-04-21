package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.presentation.PresentationHolder;
import epc.metadataformat.testdata.TestDataMetadataElement;
import epc.metadataformat.testdata.TestDataPresentationElement;
import epc.metadataformat.testdata.TestDataTextElement;

public class CoherentMetadataTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() throws Exception {
		CoherentMetadata.usingTextMetadataPresentationHolders(null, null, null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithTextElementsNull() throws Exception {
		MetadataHolder metadataHolder = TestDataMetadataElement.createTestMetadataElements();
		PresentationHolder presentationHolder = TestDataPresentationElement
				.createTestPresentationElements();
		CoherentMetadata.usingTextMetadataPresentationHolders(null, metadataHolder, presentationHolder);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithMetadataElementsNull() throws Exception {
		TextHolder textHolder = TestDataTextElement.createTestTextElements();
		PresentationHolder presentationHolder = TestDataPresentationElement
				.createTestPresentationElements();
		CoherentMetadata.usingTextMetadataPresentationHolders(textHolder, null, presentationHolder);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithPresentationElementsNull() throws Exception {
		TextHolder textHolder = TestDataTextElement.createTestTextElements();
		MetadataHolder metadataHolder = TestDataMetadataElement.createTestMetadataElements();
		CoherentMetadata.usingTextMetadataPresentationHolders(textHolder, metadataHolder, null);
	}

	@Test
	public void testInit() {
		TextHolder textHolder = TestDataTextElement.createTestTextElements();
		MetadataHolder metadataHolder = TestDataMetadataElement.createTestMetadataElements();
		PresentationHolder presentationHolder = TestDataPresentationElement
				.createTestPresentationElements();
		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextMetadataPresentationHolders(
				textHolder, metadataHolder, presentationHolder);

		assertEquals(coherentMetadata.getTextElements(), textHolder,
				"TextElements should be the same as the one set in the constructor");

		assertEquals(coherentMetadata.getMetadataElements(), metadataHolder,
				"MetadataElements should be the same as the one set in the constructor");
		assertEquals(coherentMetadata.getPresentationElements(), presentationHolder);
	}
}
