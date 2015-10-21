package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;

/**
 * ValidateDataTest tests that the ValidateData class correctly validates data
 * based on the metadataFormat
 * 
 * @author olov
 * 
 */
public class DataValidatorTest {
	private DataValidator dataValidator;

	@BeforeMethod
	public void setUp() {
		MetadataStorage metadataStorage = new MetadataStorageStub();
		dataValidator = new DataValidatorImp(metadataStorage);
	}

	@Test
	public void testValidateWhereMetadataIdNotPresentInMetadata() {
		DataElement data = DataAtomic.withNameInDataAndValue("anId", "12:12");
		assertFalse(dataValidator.validateData("doesNotExist", data).dataIsValid(),
				"The regular expression should be validated to false");
	}

	@Test
	public void testValidateTextVariable() {
		DataElement data = DataAtomic.withNameInDataAndValue("textVar2", "12:12");
		assertTrue(dataValidator.validateData("textVar2", data).dataIsValid(),
				"The regular expression should be validated to true");
	}

	@Test
	public void testValidateCollectionVariable() {
		DataElement data = DataAtomic.withNameInDataAndValue("collectionVar2", "person");
		Assert.assertEquals(dataValidator.validateData("collectionVar2", data).dataIsValid(), true,
				"The collection variable person should be validated to true");
		DataElement data2 = DataAtomic.withNameInDataAndValue("collectionVar2", "place");
		assertTrue(dataValidator.validateData("collectionVar2", data2).dataIsValid(),
				"The collection variable place should be validated to true");
	}

	@Test
	public void testValidateMetadataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		DataAtomic child1 = DataAtomic.withNameInDataAndValue("textVarNameInData", "10:10");
		child1.setRepeatId("4");
		dataGroup.addChild(child1);
		DataAtomic child2 = DataAtomic.withNameInDataAndValue("textVarNameInData", "11:11");
		child2.setRepeatId("3");
		dataGroup.addChild(child2);
		assertTrue(dataValidator.validateData("group", dataGroup).dataIsValid(),
				"The group should be validate to true");
	}
}
