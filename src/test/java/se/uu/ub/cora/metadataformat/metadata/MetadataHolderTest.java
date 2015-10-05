package se.uu.ub.cora.metadataformat.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.metadata.MetadataElement;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

public class MetadataHolderTest {
	@Test
	public void testInit() {
		MetadataHolder metadataHolder = new MetadataHolder();
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		MetadataElement textElement = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData", "textId",
						"defTextId", regularExpression);
		metadataHolder.addMetadataElement(textElement);
		Assert.assertEquals(metadataHolder.getMetadataElement("id"), textElement,
				"textElement should be the same one that was entered");
	}
}
