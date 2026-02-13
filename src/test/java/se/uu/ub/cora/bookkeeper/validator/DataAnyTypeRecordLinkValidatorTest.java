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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.AnyTypeRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderImp;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;

public class DataAnyTypeRecordLinkValidatorTest {
	private AnyTypeRecordLink anyTypeRecordLink;
	private DataAnyTypeRecordLinkValidator anyTypeRecordLinkValidator;
	private MetadataHolder metadataHolder = new MetadataHolderImp();
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();

	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		anyTypeRecordLink = AnyTypeRecordLink.withIdAndNameInDataAndTextIdAndDefTextId("id",
				"nameInData", "textId", "defTextId");
		metadataHolder.addMetadataElement(anyTypeRecordLink);

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

		anyTypeRecordLinkValidator = new DataAnyTypeRecordLinkValidator(recordTypeHolder,
				metadataHolder, anyTypeRecordLink);
	}

	@Test
	public void testValidateNoAttributes() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateOneAttribute() {
		DataAtomicSpy dataAtomicSpy = new DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> "choice1NameInData");
		dataFactorySpy.MRV.setDefaultReturnValuesSupplier("factorAtomicUsingNameInDataAndValue",
				() -> dataAtomicSpy);

		addAttributeCollectionToMetadataHolder();

		anyTypeRecordLink.addAttributeReference("linkAttributeId");

		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordId");
		dataRecordLink.addAttributeByIdWithValue("linkAttributeNameInData", "choice1NameInData");

		anyTypeRecordLinkValidator = new DataAnyTypeRecordLinkValidator(recordTypeHolder,
				metadataHolder, anyTypeRecordLink);
		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

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

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertFalse(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateInvalidRecordId_NotAccordingRegeExp() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "myLinkedRecordIdÅÄÖ");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateRecordType_AnyType() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"AnyType", "myLinkedRecordId");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateEmptyNameInData() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("", "linkedRecordType",
						"myLinkedRecordId");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertErrorMessageExists(validationAnswer,
				"DataGroup should have name(nameInData): nameInData it does not.");
		assertTrue(validationAnswer.dataIsInvalid());
	}

	private void assertErrorMessageExists(ValidationAnswer validationAnswer, String errorMessage) {
		Collection<String> errorMessages = validationAnswer.getErrorMessages();
		assertTrue(errorMessages.contains(errorMessage));
	}

	@Test
	public void testValidateEmptyRecordType() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData", "",
						"myLinkedRecordId");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertErrorMessageExists(validationAnswer,
				"DataRecordLink with nameInData:nameInData must have an nonempty recordType as child.");
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordType() {
		DataGroup dataRecordLink = new DataGroupOldSpy("nameInData");

		DataAtomic linkedRecordId = new DataAtomicOldSpy("linkedRecordId", "myLinkedRecordId");
		dataRecordLink.addChild(linkedRecordId);

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertErrorMessageExists(validationAnswer,
				"DataRecordLink with nameInData:nameInData must have an nonempty recordType as child.");
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateEmptyRecordId() {
		DataGroup dataRecordLink = DataCreator
				.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId("nameInData",
						"linkedRecordType", "");

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertErrorMessageExists(validationAnswer,
				"TextVariable with nameInData:linkedRecordId is NOT valid, regular expression((^[0-9A-Za-z:-_]{2,50}$)) does not match:");
		assertTrue(validationAnswer.dataIsInvalid());
	}

	@Test
	public void testValidateNoRecordId() {
		DataGroup dataRecordLink = new DataGroupOldSpy("nameInData");

		DataAtomic linkedRecordType = new DataAtomicOldSpy("linkedRecordType",
				"myLinkedRecordType");
		dataRecordLink.addChild(linkedRecordType);

		ValidationAnswer validationAnswer = anyTypeRecordLinkValidator.validateData(dataRecordLink);

		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertErrorMessageExists(validationAnswer,
				"DataRecordLink with nameInData:nameInData must have an nonempty recordId as child.");
		assertTrue(validationAnswer.dataIsInvalid());
	}
}
