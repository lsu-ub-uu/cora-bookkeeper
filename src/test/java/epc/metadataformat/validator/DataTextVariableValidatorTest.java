package epc.metadataformat.validator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.metadata.TextVariable;

public class DataTextVariableValidatorTest {
	private DataElementValidator textDataValidator;

	@BeforeMethod
	public void setUp() {
		TextVariable textVariable = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("id", "dataId", "textId",
						"defTextId", "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");

		textDataValidator = new DataTextVariableValidator(textVariable);
	}

	@Test
	public void testValidate() {
		DataAtomic data = DataAtomic.withDataIdAndValue("dataId", "10:10");
		assertTrue(textDataValidator.validateData(data).dataIsValid(),
				"The regular expression should be validated to true");
	}

	@Test
	public void testInvalidCase() {
		DataAtomic data = DataAtomic.withDataIdAndValue("dataId", "1010");
		assertFalse(textDataValidator.validateData(data).dataIsValid(),
				"The regular expression should be validated to false");
	}
}
