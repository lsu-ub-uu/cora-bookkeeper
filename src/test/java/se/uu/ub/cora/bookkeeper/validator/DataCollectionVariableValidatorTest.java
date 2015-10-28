package se.uu.ub.cora.bookkeeper.validator;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;

public class DataCollectionVariableValidatorTest {
	// * valid
	// * invalid
	// * missing
	// * extra
	@Test
	public void testValidateValidData() {
		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariable collectionVariable = (CollectionVariable) metadataHolder
				.getMetadataElement("collectionVarId");

		DataCollectionVariableValidator validator = new DataCollectionVariableValidator(
				metadataHolder, collectionVariable);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1NameInData");
		assertTrue(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to true");
	}

	@Test
	public void testValidateInvalidData() {

		MetadataHolder metadataHolder = createCollectionVariable();
		CollectionVariable collectionVariable = (CollectionVariable) metadataHolder
				.getMetadataElement("collectionVarId");

		DataCollectionVariableValidator validator = new DataCollectionVariableValidator(
				metadataHolder, collectionVariable);

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("collectionVarNameInData",
				"choice1ERRORNameInData");
		Assert.assertFalse(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to false");
	}

	private MetadataHolder createCollectionVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
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

		return metadataHolder;
	}
}
