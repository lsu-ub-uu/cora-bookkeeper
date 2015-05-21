package epc.metadataformat.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MetadataHolderTest {
	@Test
	public void testInit() {
		MetadataHolder metadataHolder = new MetadataHolder();
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		MetadataElement textElement = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("id", "dataId", "textId",
						"defTextId", regularExpression);
		metadataHolder.addMetadataElement(textElement);
		Assert.assertEquals(metadataHolder.getMetadataElement("id"), textElement,
				"textElement should be the same one that was entered");
	}
}
