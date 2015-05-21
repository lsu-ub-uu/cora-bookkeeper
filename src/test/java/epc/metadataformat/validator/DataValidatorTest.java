package epc.metadataformat.validator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.storage.MetadataStorage;

/**
 * ValidateDataTest tests that the ValidateData class correctly validates data based on the
 * metadataFormat
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
		DataElement data = DataAtomic.withDataIdAndValue("anId", "12:12");
		assertFalse(dataValidator.validateData("doesNotExist", data).dataIsValid(),
				"The regular expression should be validated to false");
	}

	@Test
	public void testValidateTextVariable() {
		DataElement data = DataAtomic.withDataIdAndValue("textVar2", "12:12");
		assertTrue(dataValidator.validateData("textVar2", data).dataIsValid(),
				"The regular expression should be validated to true");
	}

	@Test
	public void testValidateCollectionVariable() {
		DataElement data = DataAtomic.withDataIdAndValue("collectionVar2", "person");
		Assert.assertEquals(dataValidator.validateData("collectionVar2", data).dataIsValid(), true,
				"The collection variable person should be validated to true");
		DataElement data2 = DataAtomic.withDataIdAndValue("collectionVar2", "place");
		assertTrue(dataValidator.validateData("collectionVar2", data2).dataIsValid(),
				"The collection variable place should be validated to true");
	}

	@Test
	public void testValidateMetadataGroup() {
		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "11:11"));
		assertTrue(dataValidator.validateData("group", dataGroup).dataIsValid(),
				"The group should be validate to true");
	}
}
