/*
 * Copyright 2015, 2017, 2019 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataAtomicFactorySpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataGroupFactorySpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class DataRecordLinkValidatorTest {
	private RecordLink dataLink;
	private DataRecordLinkValidator dataLinkValidator;
	private MetadataHolder metadataHolder = new MetadataHolder();
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
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

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder, dataLink);
	}

	@Test
	public void testValidateNoAttributes() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateOneAttribute() {
		addAttributeCollectionToMetadataHolder();

		dataLink.addAttributeReference("linkAttributeId");

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		dataRecordLink.addAttributeByIdWithValue("linkAttributeNameInData", "choice1NameInData");

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder, dataLink);
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	private void addAttributeCollectionToMetadataHolder() {
		CollectionVariable colVar = new CollectionVariable("linkAttributeId",
				"linkAttributeNameInData", "linkAttributeText", "linkAttributeDefText",
				"collectionId");
		metadataHolder.addMetadataElement(colVar);
		CollectionItem choice1 = new CollectionItem("choice1Id", "choice1NameInData",
				"choice1TextId", "choice1DefTextId");
		metadataHolder.addMetadataElement(choice1);

		CollectionItem choice2 = new CollectionItem("choice2Id", "choice2NameInData",
				"choice2TextId", "choice2DefTextId");
		metadataHolder.addMetadataElement(choice2);

		ItemCollection collection = new ItemCollection("collectionId", "collectionNameInData",
				"CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(collection);
		collection.addItemReference("choice1Id");
		collection.addItemReference("choice2Id");
	}

	@Test
	public void testInvalidAttribute() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		dataRecordLink.addAttributeByIdWithValue("col1NameInData", "choice1NameInData_NOT_VALID");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertFalse(validationAnswer.dataIsValid());
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
		DataGroup image = new DataGroupSpy("image");
		DataGroup parentId = new DataGroupSpy("parentId");
		image.addChild(parentId);
		parentId.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		parentId.addChild(new DataAtomicSpy("linkedRecordId", "binary"));
		recordTypeHolder.put("image", image);

		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder,
				myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image",
						"image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 0);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeOtherParentId() {
		String incorrectParentId = "NOTBinary";
		DataGroup image = new DataGroupSpy("image");
		DataGroup parentId = new DataGroupSpy("parentId");
		image.addChild(parentId);
		parentId.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		parentId.addChild(new DataAtomicSpy("linkedRecordId", incorrectParentId));
		recordTypeHolder.put("image", image);

		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder,
				myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "image",
						"image001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordTypeIsNOTChildOfAbstractMetadataRecordTypeNoParent() {
		DataGroup image = new DataGroupSpy("image");
		recordTypeHolder.put("image", image);

		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder,
				myBinaryLink);

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
	public void testValidateRecordTypeIsGrandChildOfAbstractMetadataRecordType() {
		DataGroup image = new DataGroupSpy("image");
		DataGroup parentId = new DataGroupSpy("parentId");
		parentId.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		parentId.addChild(new DataAtomicSpy("linkedRecordId", "binary"));
		image.addChild(parentId);
		recordTypeHolder.put("image", image);

		DataGroup stillImage = new DataGroupSpy("stillImage");
		DataGroup stillImageParent = new DataGroupSpy("parentId");
		stillImageParent.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		stillImageParent.addChild(new DataAtomicSpy("linkedRecordId", "image"));
		stillImage.addChild(stillImageParent);
		recordTypeHolder.put("stillImage", stillImage);

		RecordLink myBinaryLink = createAndAddBinaryToMetadataHolder(metadataHolder);

		dataLinkValidator = new DataRecordLinkValidator(recordTypeHolder, metadataHolder,
				myBinaryLink);

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("binary", "stillImage",
						"stillImage001");
		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 0);
		assertTrue(validationAnswer.dataIsValid());
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
		DataGroup dataRecordLink = new DataGroupSpy("nameInData");

		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "myLinkedRecordId");
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
		DataGroup dataRecordLink = new DataGroupSpy("nameInData");

		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "myLinkedRecordType");
		dataRecordLink.addChild(linkedRecordType);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 2);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedRepeatId() {
		dataLink.setLinkedPath(new DataGroupSpy("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testLinkedRepeatIdMissing() {
		dataLink.setLinkedPath(new DataGroupSpy("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedEmptyRepeatId() {
		dataLink.setLinkedPath(new DataGroupSpy("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateInvalidRepeatId() {
		dataLink.setLinkedPath(new DataGroupSpy("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "ÅÄÖ");
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
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);

		ValidationAnswer validationAnswer = dataLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testLinkedLinkedPathShouldNeverExist() {
		dataLink.setLinkedPath(new DataGroupSpy("linkedPath"));

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "x1");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.addChild(new DataGroupSpy("linkedPath"));

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
