package se.uu.ub.cora.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAttribute;

public class DataAttributeTest {
	@Test
	public void testInit() {
		DataAttribute dataAttribute = DataAttribute.withNameInDataAndValue("nameInData", "value");
		assertEquals(dataAttribute.getNameInData(), "nameInData",
				"NameInData should be the one set in the constructor");
		assertEquals(dataAttribute.getValue(), "value", "Value should be "
				+ "the same as the value set in the constructor");
	}
}
