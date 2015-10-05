package se.uu.ub.cora.metadataformat.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.metadata.TextVariable;

public class TextVariableTest {
	@Test
	public void testRegExVariableInit() {
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		TextVariable textVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData", "textId",
						"defTextId", regularExpression);

		assertEquals(textVar.getId(), "id", "Id should have the value set in the constructor");

		assertEquals(textVar.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(textVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(textVar.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertEquals(textVar.getRegularExpression(), regularExpression,
				"RegularExpression should have the value set in the constructor");
	}
}
