package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.metadata.CollectionVariable;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariableChild;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroupChild;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

public class DataValidatorFactoryTest {
	@Test
	public void testFactorDataValidatorMetadataGroup() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(
				"metadataGroupId", "nameInData", "textId", "defTextId"));
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("metadataGroupId");
		assertTrue(dataGroupValidator instanceof DataGroupValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataGroupChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new MetadataGroupChild("metadataGroupChildId",
				"nameInData", "textId", "defTextId", "metadataGroupId"));
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("metadataGroupChildId");
		assertTrue(dataGroupValidator instanceof DataGroupValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataTextVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(
				TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"textVariableId", "nameInData", "textId", "defTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("textVariableId");
		assertTrue(dataGroupValidator instanceof DataTextVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataDataToDataLink() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(
				DataToDataLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(
						"dataToDataLinkId", "nameInData", "textId", "defTextId", "someRecordType"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory.factor("dataToDataLinkId");
		assertTrue(dataGroupValidator instanceof DataRecordLinkValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariable() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new CollectionVariable("collectionVariableId",
				"nameInData", "textId", "defTextId", "collectionId"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("collectionVariableId");
		assertTrue(dataGroupValidator instanceof DataCollectionVariableValidator);
	}

	@Test
	public void testFactorDataValidatorMetadataCollectionVariableChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		metadataHolder.addMetadataElement(new CollectionVariableChild("collectionVariableId",
				"nameInData", "textId", "defTextId", "collectionId", "scollectionId"));

		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataGroupValidator = dataValidatorFactory
				.factor("collectionVariableId");
		assertTrue(dataGroupValidator instanceof DataCollectionVariableChildValidator);
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testNotIdFound() {
		MetadataHolder metadataHolder = new MetadataHolder();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		dataValidatorFactory.factor("elementNotFound");
	}

}
