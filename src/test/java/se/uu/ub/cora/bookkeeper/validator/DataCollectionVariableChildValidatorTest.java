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

import org.testng.Assert;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariableChild;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;

import static org.testng.Assert.assertTrue;

public class DataCollectionVariableChildValidatorTest {
	// * valid
	// * invalid
	// * missing
	// * extra
	@Test
	public void testValidateValidData() {
		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariableChild collectionVariableChild = (CollectionVariableChild) metadataHolder
				.getMetadataElement("collectionChildVarId");

		DataCollectionVariableChildValidator validator = new DataCollectionVariableChildValidator(
				metadataHolder, collectionVariableChild);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1NameInData");
		assertTrue(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to true");
	}

	@Test
	public void testValidateInvalidData() {

		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariableChild collectionVariableChild = (CollectionVariableChild) metadataHolder
				.getMetadataElement("collectionChildVarId");

		DataCollectionVariableChildValidator validator = new DataCollectionVariableChildValidator(
				metadataHolder, collectionVariableChild);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1ERRORNameInData");
		Assert.assertFalse(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to false");
	}

	@Test
	public void testValidateFinalValueValidData() {
		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariableChild collectionVariableChild = (CollectionVariableChild) metadataHolder
				.getMetadataElement("collectionChildVarId");

		collectionVariableChild.setFinalValue("choice2NameInData");

		DataCollectionVariableChildValidator validator = new DataCollectionVariableChildValidator(
				metadataHolder, collectionVariableChild);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice2NameInData");
		assertTrue(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to true");
	}

	@Test
	public void testValidateFinalValueInvalidData() {

		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariableChild collectionVariableChild = (CollectionVariableChild) metadataHolder
				.getMetadataElement("collectionChildVarId");

		collectionVariableChild.setFinalValue("choice2NameInData");

		DataCollectionVariableChildValidator validator = new DataCollectionVariableChildValidator(
				metadataHolder, collectionVariableChild);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1ERRORNameInData");
		Assert.assertFalse(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to false");
	}

	@Test
	public void testValidateFinalValueWrongChoiceData() {

		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariableChild collectionVariableChild = (CollectionVariableChild) metadataHolder
				.getMetadataElement("collectionChildVarId");

		collectionVariableChild.setFinalValue("choice2NameInData");

		DataCollectionVariableChildValidator validator = new DataCollectionVariableChildValidator(
				metadataHolder, collectionVariableChild);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1NameInData");
		Assert.assertFalse(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to false");
	}

	private MetadataHolder createCollectionVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupTypen
		CollectionVariable colVar = new CollectionVariable("collectionVarId",
				"collectionVarNameInData", "collectionVarTextId", "collectionVarDefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem choice1 = new CollectionItem("choice1Id", "choice1NameInData", "choice1TextId",
				"choice1DefTextId");
		metadataHolder.addMetadataElement(choice1);

		CollectionItem choice2 = new CollectionItem("choice2Id", "choice2NameInData", "choice2TextId",
				"choice2DefTextId");
		metadataHolder.addMetadataElement(choice2);

		ItemCollection collection = new ItemCollection("collectionId", "collectionNameInData",
				"CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(collection);
		collection.addItemReference("choice1Id");
		collection.addItemReference("choice2Id");

		// child
		CollectionVariableChild colChildVar = new CollectionVariableChild("collectionChildVarId",
				"collectionChildVarNameInData", "collectionChildVarTextId",
				"collectionChildVarDefTextId", "collectionId", "collectionVarId");

		metadataHolder.addMetadataElement(colChildVar);

		return metadataHolder;
	}
}
