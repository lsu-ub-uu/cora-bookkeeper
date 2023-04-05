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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderImp;
import se.uu.ub.cora.data.DataAtomic;

public class DataCollectionVariableValidatorTest {
	private MetadataHolder metadataHolder;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderImp();
	}

	@Test
	public void testValidateValidData() {
		DataCollectionVariableValidator validator = createCollectionVariableInValidator();

		DataAtomic dataAtomic = new DataAtomicOldSpy("collectionVarNameInData", "choice1NameInData");
		assertTrue(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to true");
	}

	private DataCollectionVariableValidator createCollectionVariableInValidator() {
		CollectionVariable collectionVariable = createCollectionVariable();
		return new DataCollectionVariableValidator(metadataHolder, collectionVariable);
	}

	private CollectionVariable createCollectionVariable() {
		CollectionVariable collectionVariable = new CollectionVariable("collectionVarId",
				"collectionVarNameInData", "collectionVarTextId", "collectionVarDefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(collectionVariable);

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
		return collectionVariable;
	}

	@Test
	public void testValidateInvalidData() {
		DataCollectionVariableValidator validator = createCollectionVariableInValidator();

		DataAtomic dataAtomic = new DataAtomicOldSpy("collectionVarNameInData",
				"choice1ERRORNameInData");
		ValidationAnswer validationAnswer = validator.validateData(dataAtomic);

		assertDataInvalidWithOnlyOneErrorMessage(validationAnswer);
	}

	private void assertDataInvalidWithOnlyOneErrorMessage(ValidationAnswer validationAnswer) {
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The collection variable should be validated to false");
	}

	@Test
	public void testValidateFinalValueValidData() {
		DataCollectionVariableValidator validator = createCollectionVariableWithFinalValue();

		DataAtomic dataAtomic = new DataAtomicOldSpy("collectionVarNameInData", "choice2NameInData");

		assertTrue(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to true");
	}

	private DataCollectionVariableValidator createCollectionVariableWithFinalValue() {
		CollectionVariable collectionVariable = createCollectionVariable();
		collectionVariable.setFinalValue("choice2NameInData");
		return new DataCollectionVariableValidator(metadataHolder, collectionVariable);
	}

	@Test
	public void testValidateFinalValueInvalidData() {
		DataCollectionVariableValidator validator = createCollectionVariableWithFinalValue();

		DataAtomic dataAtomic = new DataAtomicOldSpy("collectionVarNameInData",
				"choice1ERRORNameInData");
		ValidationAnswer validationAnswer = validator.validateData(dataAtomic);

		assertDataInvalidWithOnlyOneErrorMessage(validationAnswer);
	}

	@Test
	public void testValidateFinalValueWrongChoiceData() {
		DataCollectionVariableValidator validator = createCollectionVariableWithFinalValue();

		DataAtomic dataAtomic = new DataAtomicOldSpy("collectionVarNameInData", "choice1NameInData");
		ValidationAnswer validationAnswer = validator.validateData(dataAtomic);

		assertDataInvalidWithOnlyOneErrorMessage(validationAnswer);
	}

}
