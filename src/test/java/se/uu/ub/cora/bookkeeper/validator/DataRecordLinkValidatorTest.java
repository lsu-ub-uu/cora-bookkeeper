/*
 * Copyright 2015 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DataRecordLinkValidatorTest {
	private RecordLink dataLink;
	private DataRecordLinkValidator dataLinkValidator;

	@BeforeMethod
	public void setUp() {
		MetadataHolder metadataHolder = new MetadataHolder();
		dataLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id",
				"nameInData", "textId", "defTextId", "linkedRecordType");
		metadataHolder.addMetadataElement(dataLink);
		dataLinkValidator = new DataRecordLinkValidator(metadataHolder, dataLink);
	}

	@Test
	public void testValidate() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	private DataGroup createGroupWithNameInDataAndRecordTypeAndRecordId(String nameInData, String linkedRecordTypeString, String linkedRecordIdString) {
		DataGroup dataRecordLink = DataGroup.withNameInData(nameInData);

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordTypeString);
		dataRecordLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordIdString);
		dataRecordLink.addChild(linkedRecordId);
		return dataRecordLink;
	}

	@Test
	public void testValidateRecordType() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "notMyRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordTypeIsChildOfAbstractMetadataRecordType(){
		MetadataHolder abstractMetadataHolder = new MetadataHolder();
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(abstractMetadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image", "recordType", "imageText","imageDefText");
		image.setRefParentId("binary");
		abstractMetadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataRecordLinkValidator(abstractMetadataHolder, myBinaryLink);

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image", "image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 0);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeOtherParentId(){
		MetadataHolder abstractMetadataHolder = new MetadataHolder();
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(abstractMetadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image", "recordType", "imageText","imageDefText");
		image.setRefParentId("NOTBinary");
		abstractMetadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataRecordLinkValidator(abstractMetadataHolder, myBinaryLink);

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image", "image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeNoParentId(){
		MetadataHolder abstractMetadataHolder = new MetadataHolder();
		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(abstractMetadataHolder);

		MetadataGroup image = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("image", "recordType", "imageText","imageDefText");
		abstractMetadataHolder.addMetadataElement(image);

		dataLinkValidator = new DataRecordLinkValidator(abstractMetadataHolder, myBinaryLink);

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image", "image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	private RecordLink createAndAddBinaryToMetadataHolder(MetadataHolder abstractMetadataHolder) {
		RecordLink myBinaryLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("id",
				"binary", "textId", "defTextId", "binary");
		abstractMetadataHolder.addMetadataElement(myBinaryLink);
		return myBinaryLink;
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("", "linkedRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordType(){
		DataGroup dataRecordLink = DataGroup.withNameInData("nameInData");

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId", "myLinkedRecordId");
		dataRecordLink.addChild(linkedRecordId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordId(){
		DataGroup dataRecordLink = DataGroup.withNameInData("nameInData");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType", "myLinkedRecordType");
		dataRecordLink.addChild(linkedRecordType);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 2);
		assertTrue(validationAnswer.dataIsInvalid());
	}


	@Test
	public void testLinkedRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedRepeatIdMissing() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedEmptyRepeatId() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatIdShouldNotExist() {
		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedLinkedPathShouldNeverExist() {
		dataLink.setLinkedPath(DataGroup.withNameInData("linkedPath"));

		DataGroup dataRecordLink = createGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.addChild(DataGroup.withNameInData("linkedPath"));

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}
}

