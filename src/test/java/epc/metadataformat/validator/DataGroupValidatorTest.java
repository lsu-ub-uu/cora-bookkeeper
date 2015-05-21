package epc.metadataformat.validator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionItem;
import epc.metadataformat.metadata.CollectionVariable;
import epc.metadataformat.metadata.ItemCollection;
import epc.metadataformat.metadata.MetadataChildReference;
import epc.metadataformat.metadata.MetadataGroup;
import epc.metadataformat.metadata.MetadataHolder;
import epc.metadataformat.metadata.TextVariable;

public class DataGroupValidatorTest {
	@Test
	public void testOneGroupNoAttributesOneTextChildWrongDataId() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataERRORId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as the dataId is invalid");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildValidData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraAttribute() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an "
						+ "attribute it should not have");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildDataId() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataIdERROR", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an " + "child with wrong dataId");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10Error10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an " + "child with wrong data");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildMissingChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		// dataGroup.addChild(new DataAtomic("textVarDataId", "10Error10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it does not " + "have a child");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has " + "too many children");
	}

	private MetadataHolder createOneGroupNoAttributesOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("groupId",
				"groupDataId", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);

		TextVariable textVar = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId",
						"textVarDataId", "textVarTextId", "textVarDefTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar);

		MetadataChildReference textChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId", 1, 1);

		group.addChildReference(textChild);
		return metadataHolder;
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildValidData() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("collectionVarDataId", "choice1DataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true, as it has " + "valid data");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildInvalidAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("collectionVarDataId", "choice1ERRORDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an invalid attribute");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildMissingAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		// dataGroup.addAttributeByIdWithValue("collectionVarDataId",
		// "choice1ERRORDataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an missing attribute");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildExtraAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("collectionVarDataId", "choice1DataId");
		dataGroup.addAttributeByIdWithValue("collectionVar2DataId", "choice1DataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an extra attribute");

	}

	private MetadataHolder createOneGroupOneAttributeOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("collectionVarId",
				"collectionVarDataId", "collectionVarTextId", "collectionVarDefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar);

		CollectionVariable colVar2 = new CollectionVariable("collectionVar2Id",
				"collectionVarData2Id", "collectionVar2TextId", "collectionVar2DefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar2);

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

		MetadataGroup group = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("groupId",
				"groupDataId", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);

		group.addAttributeReference("collectionVarId");

		TextVariable textVar = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId",
						"textVarDataId", "textVarTextId", "textVarDefTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar);

		MetadataChildReference textChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId", 1, 1);

		group.addChildReference(textChild);
		return metadataHolder;
	}

	@Test
	public void testTwoGroupsTwoAttributesOneTextChildOneGroupChildValidData() {
		MetadataHolder metadataHolder = createTwoGroupsTwoAttributesOneTextChildOneGroupChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group2Id");

		// dataGroup
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("collectionVarDataId", "choice1DataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		// dataGroup2
		DataGroup dataGroup2 = DataGroup.withDataId("group2DataId");
		dataGroup2.addAttributeByIdWithValue("collectionVarDataId", "choice1DataId");
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup2.addChild(dataGroup);

		assertEquals(dataElementValidator.validateData(dataGroup2).dataIsValid(), true,
				"The group should be validate to true, as it has " + "valid data");

	}

	private MetadataHolder createTwoGroupsTwoAttributesOneTextChildOneGroupChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("collectionVarId",
				"collectionVarDataId", "collectionVarTextId", "collectionVarDefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar);

		CollectionVariable colVar2 = new CollectionVariable("collectionVar2Id",
				"collectionVarData2Id", "collectionVar2TextId", "collectionVar2DefTextId",
				"collectionId");
		metadataHolder.addMetadataElement(colVar2);

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

		MetadataGroup group = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("groupId",
				"groupDataId", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);

		group.addAttributeReference("collectionVarId");

		TextVariable textVar = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId",
						"textVarDataId", "textVarTextId", "textVarDefTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar);

		MetadataChildReference textChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId", 1, 1);

		group.addChildReference(textChild);

		// group2
		MetadataGroup group2 = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("group2Id",
				"group2DataId", "group2TextId", "group2DefTextId");
		metadataHolder.addMetadataElement(group2);

		group2.addAttributeReference("collectionVarId");
		group2.addChildReference(textChild);

		MetadataChildReference groupChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("groupId", 1, 1);
		group2.addChildReference(groupChild);

		return metadataHolder;
	}

	/**
	 * <pre>
	 * TODO: add tests for:
	 * one group without attribute and one atomic child
	 * one group with one attribute and one atomic child
	 * one group with two attributes and one atomic child
	 * one group with three attributes and one atomic child
	 * 
	 * one group with one inherited attribute and one atomic child
	 * one group with one inherited (inherited two levels) attributes and one atomic child
	 * one group with one inherited (inherited three levels) attributes and one atomic child
	 * 
	 * 
	 * one group without attribute and one atomic inherited child 
	 * one group without attribute and one atomic inherited (inherited two levels) child 
	 * one group without attribute and one atomic inherited (inherited three levels) child
	 *  
	 * one group without attribute and one group inherited child 
	 * one group without attribute and one group inherited (inherited two levels) child 
	 * one group without attribute and one group inherited (inherited three levels) child 
	 * 
	 * one group without attribute and one group child
	 * one group with one attribute and one group child
	 * one group with two attributes and one group child
	 * 
	 * etc.
	 * 
	 * 
	 * IDEA, BREAK DOWN THE PROBLEM INTO PARTS SUCH AS ATTRIBUTES AND CHILDREN 
	 * THAT CAN BE CHECKED INDEPENDENTLY FROM EACH OTHER. Could it work with 
	 * correct number of attributes/chilren, one missing, one extra, correct value, etc?
	 * 
	 * test all for:
	 * 
	 * valid attribute
	 * invalid attribute
	 * missing attribute
	 * extra attribute
	 * 
	 * correct children
	 * wrong children
	 * missing children
	 * extra children
	 * </pre>
	 */
	@Test
	public void testValidAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");

		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	@Test
	public void testInvalidAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1_NOT_VALID");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as attributes value is invalid");
	}

	@Test
	public void testMissingAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		// dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as it does not have a needed attribute");
	}

	@Test
	public void testExtraAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addAttributeByIdWithValue("groupTypeVar2", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as it does not have a needed attribute");
	}

	@Test
	public void testValidateOneWrongDataChildElement() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "66:66"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	@Test
	public void testValidateTwoRightDataChildElements() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "01:11"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	@Test
	public void testValidateWithOneRightAndOneWrongAttributeName() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addAttributeByIdWithValue("groupTypeVar2", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	@Test
	public void testValidateWithOneWrongAttributeValue() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType11");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	// @Test(expectedExceptions = RuntimeException.class)
	@Test
	public void dataWithDataIdNotInMetadataShouldNotBeValid() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId2", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true");
	}

	private MetadataHolder createMetadataForOneSimpleGroup() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("groupTypeVar", "groupTypeVar",
				"groupTypeVarText", "groupTypeVarDefText", "groupTypeCollection");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem groupType1 = new CollectionItem("groupType1", "groupType1",
				"groupType1Text", "groupType1DefText");
		metadataHolder.addMetadataElement(groupType1);

		CollectionItem groupType2 = new CollectionItem("groupType2", "groupType2",
				"groupType2Text", "groupType2DefText");
		metadataHolder.addMetadataElement(groupType2);

		ItemCollection groupTypeCollection = new ItemCollection("groupTypeCollection",
				"groupTypeCollection", "groupTypeCollectionText", "groupTypeCollectionDefText");
		metadataHolder.addMetadataElement(groupTypeCollection);
		groupTypeCollection.addItemReference("groupType1");
		groupTypeCollection.addItemReference("groupType2");
		// child
		TextVariable textVar = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId",
						"textVarDataId", "textVarTextId", "textVarDefTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar);

		// group
		MetadataGroup metadataGroup = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("group",
				"group", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(metadataGroup);

		MetadataChildReference groupChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId", 1,
						MetadataChildReference.UNLIMITED);

		// attribute references
		metadataGroup.addAttributeReference("groupTypeVar");

		// child references
		metadataGroup.addChildReference(groupChild);

		return metadataHolder;
	}

	@Test
	public void testAdvancedGroupOneRightDataChildElement() {
		MetadataHolder metadataHolder = createMetadataForOneGroupDoubleAttributesAndChildren();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("group");

		DataGroup dataGroup = DataGroup.withDataId("group");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		// 1-x,1-1,0-x
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId2", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId3", "10:10"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textVarDataId3", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	private MetadataHolder createMetadataForOneGroupDoubleAttributesAndChildren() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("groupTypeVar", "groupTypeVar",
				"groupTypeVarText", "groupTypeVarDefText", "groupTypeCollection");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem groupType1 = new CollectionItem("groupType1", "groupType1",
				"groupType1Text", "groupType1DefText");
		metadataHolder.addMetadataElement(groupType1);

		CollectionItem groupType2 = new CollectionItem("groupType2", "groupType2",
				"groupType2Text", "groupType2DefText");
		metadataHolder.addMetadataElement(groupType2);

		ItemCollection groupTypeCollection = new ItemCollection("groupTypeCollection",
				"groupTypeCollection", "groupTypeCollectionText", "groupTypeCollectionDefText");
		metadataHolder.addMetadataElement(groupTypeCollection);
		groupTypeCollection.addItemReference("groupType1");
		groupTypeCollection.addItemReference("groupType2");
		// children
		TextVariable textVar = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId",
						"textVarDataId", "textVarTextId", "textVarDefTextId",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar);

		TextVariable textVar2 = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId2",
						"textVarDataId2", "textVarTextId2", "textVarDefTextId2",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar2);

		TextVariable textVar3 = TextVariable
				.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression("textVarId3",
						"textVarDataId3", "textVarTextId3", "textVarDefTextId3",
						"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");
		metadataHolder.addMetadataElement(textVar3);

		// group
		MetadataGroup metadataGroup = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId("group",
				"group", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(metadataGroup);

		// attribute references
		metadataGroup.addAttributeReference("groupTypeVar");

		// children references
		MetadataChildReference groupChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId", 1,
						MetadataChildReference.UNLIMITED);
		metadataGroup.addChildReference(groupChild);

		MetadataChildReference groupChild2 = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId2", 1, 1);
		metadataGroup.addChildReference(groupChild2);

		MetadataChildReference groupChild3 = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVarId3", 0,
						MetadataChildReference.UNLIMITED);
		metadataGroup.addChildReference(groupChild3);

		return metadataHolder;
	}
}
