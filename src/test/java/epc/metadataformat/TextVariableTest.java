package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TextVariableTest {
	@Test
	public void testRegExVariableInit() {
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		TextVariable textVar = TextVariable.withIdAndDataIdAndTextIdAndDeffTextIdAndRegularExpression("id", "dataId", "textId", "deffTextId",
				regularExpression);

		assertEquals(textVar.getId(), "id",
				"Id should have the value set in the constructor");
		
		assertEquals(textVar.getDataId(), "dataId",
				"DataId should have the value set in the constructor");
		
		assertEquals(textVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");
		
		assertEquals(textVar.getDefTextId(), "deffTextId",
				"DeffTextId should have the value set in the constructor");
		
		assertEquals(textVar.getRegularExpression(), regularExpression,
				"RegularExpression should have the value set in the constructor");
	}
}
