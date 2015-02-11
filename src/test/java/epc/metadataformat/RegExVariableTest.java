package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class RegExVariableTest {
	@Test
	public void testRegExVariableInit() {
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		RegExVariable regExVar = new RegExVariable("id", "dataId", "textId",
				"deffTextId", regularExpression);

		assertEquals(regExVar.getId(), "id",
				"Id should have the value set in the constructor");
		
		assertEquals(regExVar.getDataId(), "dataId",
				"DataId should have the value set in the constructor");
		
		assertEquals(regExVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");
		
		assertEquals(regExVar.getDeffTextId(), "deffTextId",
				"DeffTextId should have the value set in the constructor");
		
		assertEquals(regExVar.getRegularExpression(), regularExpression,
				"RegularExpression should have the value set in the constructor");
	}
}
