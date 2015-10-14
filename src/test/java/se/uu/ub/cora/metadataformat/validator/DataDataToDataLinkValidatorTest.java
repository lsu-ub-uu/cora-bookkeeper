package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public class DataDataToDataLinkValidatorTest {
	private DataToDataLink dataLink;
	private DataRecordLinkValidator dataLinkValidator;

	@BeforeMethod
	public void setUp() {
		dataLink = DataToDataLink.withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType("id",
				"nameInData", "textId", "defTextId", "targetRecordType");
		dataLinkValidator = new DataRecordLinkValidator();
	}

	@Test
	public void testValidate() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndRecordTypeAndRecordId(
				"nameInData", "targetRecordType", "targetRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndRecordTypeAndRecordId("",
				"targetRecordType", "targetRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "", "targetRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "targetRecordType", "");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

}
