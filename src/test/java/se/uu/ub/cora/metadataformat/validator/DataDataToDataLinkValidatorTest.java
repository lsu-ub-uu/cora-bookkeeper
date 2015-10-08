package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataDataToDataLinkValidatorTest {
	private DataToDataLink dataLink;
	private DataDataToDataLinkValidator dataLinkValidator;
	private DataGroup linkDataTest;

	@BeforeMethod
	public void setUp() {
		dataLink = DataToDataLink.withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType("id",
				"nameInData", "textId", "defTextId", "targetRecordType");
		dataLinkValidator = new DataDataToDataLinkValidator();
		linkDataTest = DataGroup.withNameInData("nameInData");
	}

	@Test
	public void testValidate() {
		linkDataTest.addChild(DataAtomic.withNameInDataAndValue("id", "someId"));
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(linkDataTest);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateMissingId() {
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(linkDataTest);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyId() {
		linkDataTest.addChild(DataAtomic.withNameInDataAndValue("id", ""));
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(linkDataTest);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateExtraChild() {
		linkDataTest.addChild(DataAtomic.withNameInDataAndValue("id", "someId"));
		linkDataTest.addChild(DataAtomic.withNameInDataAndValue("extraChild", "extraValue"));
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(linkDataTest);
		assertTrue(validationAnswer.dataIsInvalid());
	}

}
