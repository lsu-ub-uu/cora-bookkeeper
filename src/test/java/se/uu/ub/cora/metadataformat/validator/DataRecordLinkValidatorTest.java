package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.RecordLink;

public class DataRecordLinkValidatorTest {
	private RecordLink dataLink;
	private DataRecordLinkValidator dataLinkValidator;

	@BeforeMethod
	public void setUp() {
		dataLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id",
				"nameInData", "textId", "defTextId", "linkedRecordType");
		dataLinkValidator = new DataRecordLinkValidator(dataLink);
	}

	@Test
	public void testValidate() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordType() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "notMyRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("",
				"linkedRecordType", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData", "", "linkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData", "linkedRecordType", "");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedMissingRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedEmptyRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatIdShouldNotExist() {
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedLinkedPath() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedLinkedPathSholdNeverExist() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"nameInData", "linkedRecordType", "linkedRecordId");
		dataRecordLink.setLinkedRepeatId("x1");
		dataRecordLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}
}
