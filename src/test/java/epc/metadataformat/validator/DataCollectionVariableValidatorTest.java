package epc.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.metadata.CollectionItem;
import epc.metadataformat.metadata.CollectionVariable;
import epc.metadataformat.metadata.ItemCollection;
import epc.metadataformat.metadata.MetadataHolder;

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

		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("collectionVarDataId",
				"choice1DataId");
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

		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("collectionVarDataId",
				"choice1ERRORDataId");
		Assert.assertFalse(validator.validateData(dataAtomic).dataIsValid(),
				"The collection variable should be validated to false");
	}

	private MetadataHolder createCollectionVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("collectionVarId",
				"collectionVarDataId", "collectionVarTextId", "collectionVarDefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem choice1 = new CollectionItem("choice1Id", "choice1DataId", "choice1TextId",
				"choice1DefTextId");
		metadataHolder.addMetadataElement(choice1);

		CollectionItem choice2 = new CollectionItem("choice2Id", "choice2DataId", "choice2TextId",
				"choice2DefTextId");
		metadataHolder.addMetadataElement(choice2);

		ItemCollection collection = new ItemCollection("collectionId", "collectionDataId",
				"CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(collection);
		collection.addItemReference("choice1Id");
		collection.addItemReference("choice2Id");

		return metadataHolder;
	}
}
