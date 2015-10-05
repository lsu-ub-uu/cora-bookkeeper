package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.metadata.CollectionItem;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariable;
import se.uu.ub.cora.metadataformat.metadata.ItemCollection;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.validator.DataCollectionVariableValidator;

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
