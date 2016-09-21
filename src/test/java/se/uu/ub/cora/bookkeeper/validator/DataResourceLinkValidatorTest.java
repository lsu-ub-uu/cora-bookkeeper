/*
 * Copyright 2015 Uppsala University Library
 * Copyright 2016 Olov McKie
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.bookkeeper.validator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataResourceLinkValidatorTest {
	private RecordLink dataLink;
	private DataResourceLinkValidator dataLinkValidator;
	private MetadataHolder metadataHolder = new MetadataHolder();

	@BeforeMethod
	public void setUp() {

		dataLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id",
				"nameInData", "textId", "defTextId", "linkedRecordType");
		metadataHolder.addMetadataElement(dataLink);

		TextVariable linkedRecordIdTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"linkedRecordIdTextVar", "linkedRecordId", "linkedRecordIdTextVarText",
						"linkedRecordIdTextVarDefText", "(^[0-9A-Za-z:-_]{2,50}$)");

		metadataHolder.addMetadataElement(linkedRecordIdTextVar);

		TextVariable linkedRepeatIdTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"linkedRepeatIdTextVar", "linkedRepeatId", "linkedRepeatIdTextVarText",
						"linkedRepeatIdTextVarDefText", "(^[0-9A-Za-z:-_]{1,50}$)");

		metadataHolder.addMetadataElement(linkedRepeatIdTextVar);

		dataLinkValidator = new DataResourceLinkValidator(metadataHolder, dataLink);
	}

	@Test
	public void testValidate() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateInvalidRecordId() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordIdÅÄÖ");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordType() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"notMyRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordTypeIsChildOfAbstractMetadataRecordType() {
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image",
				"recordType", "imageText", "imageDefText");
		image.setRefParentId("binary");
		metadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataResourceLinkValidator(metadataHolder, myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image",
						"image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 0);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeOtherParentId() {
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image",
				"recordType", "imageText", "imageDefText");
		image.setRefParentId("NOTBinary");
		metadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataResourceLinkValidator(metadataHolder, myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image",
						"image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeNoParentId() {
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image",
				"recordType", "imageText", "imageDefText");
		metadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataResourceLinkValidator(metadataHolder, myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image",
						"image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	private RecordLink createAndAddBinaryToMetadataHolder(MetadataHolder abstractMetadataHolder) {
		RecordLink myBinaryLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id", "binary",
						"textId", "defTextId", "binary");
		abstractMetadataHolder.addMetadataElement(myBinaryLink);
		return myBinaryLink;
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("", "linkedRecordType",
						"myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "",
						"myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordType() {
		DataGroup dataRecordLink = DataGroup.withNameInData("nameInData");

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId",
				"myLinkedRecordId");
		dataRecordLink.addChild(linkedRecordId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordId() {
		DataGroup dataRecordLink = DataGroup.withNameInData("nameInData");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"myLinkedRecordType");
		dataRecordLink.addChild(linkedRecordType);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 2);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateLinkedRecordIdDoesNotExist() {

	}

	@Test
	public void testLinkedRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedRepeatIdMissing() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedEmptyRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateInvalidRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "ÅÄÖ");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatIdShouldNotExist() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedLinkedPathShouldNeverExist() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.addChild(DataGroup.withNameInData("linkedPath"));

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateFinalValue() {
		dataLink.setFinalValue("someInstance");

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "someInstance");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 0);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateWrongFinalValue() {

		dataLink.setFinalValue("someInstance");

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "wrongInstance");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}
}
