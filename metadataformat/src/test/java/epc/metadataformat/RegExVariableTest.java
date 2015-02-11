package epc.metadataformat;

import org.testng.annotations.Test;

public class RegExVariableTest {
	@Test
	public void testRegExVariableInit(){
		RegExVariable regExVar = new RegExVariable("id", "dataId", "textId", "deffTextId",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}");
		
	}
}
