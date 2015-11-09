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

import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DataGroupValidatorTest {
	@Test
	public void testOneGroupNoAttributesOneTextChildWrongNameInData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("groupDataERRORId");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as the nameInData is invalid");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildValidData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraAttribute() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an "
						+ "attribute it should not have");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildNameInData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textNameInDataERROR", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an "
						+ "child with wrong nameInData");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10Error10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has an " + "child with wrong data");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildMissingChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		// dataGroup.addChild(new DataAtomic("textVarNameInData", "10Error10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it does not " + "have a child");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraChildData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false, as it has " + "too many children");
	}

	private MetadataHolder createOneGroupNoAttributesOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = DataCreator.createMetaDataGroup("test", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group, metadataHolder);
		return metadataHolder;
	}

	@Test
	public void testOneGroupNoAttributesOneRecordLinkChildValidData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneRecordLinkChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = DataGroup.withNameInData("groupNameInData");
		dataGroup.addChild(DataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				"recordLinkNameInData", "recordLinkLinkedRecordType", "someRecordLinkId"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	private MetadataHolder createOneGroupNoAttributesOneRecordLinkChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("groupId",
				"groupNameInData", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);

		RecordLink recordLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("recordLinkId",
						"recordLinkNameInData", "recordLinkTextId", "recordLinkDefTextId",
						"recordLinkLinkedRecordType");
		metadataHolder.addMetadataElement(recordLink);

		MetadataChildReference linkChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("recordLinkId", 1, 1);

		group.addChildReference(linkChild);
		return metadataHolder;
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildValidData() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("collectionVarNameInData", "choice1NameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true, as it has " + "valid data");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildInvalidAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("collectionVarNameInData", "choice1ERRORNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an invalid attribute");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildMissingAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		// dataGroup.addAttributeByIdWithValue("collectionVarNameInData",
		// "choice1ERRORNameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an missing attribute");

	}

	@Test
	public void testOneGroupOneAttributeOneTextChildExtraAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("collectionVarNameInData", "choice1NameInData");
		dataGroup.addAttributeByIdWithValue("collectionVar2NameInData", "choice1NameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to true, as it has " + "an extra attribute");

	}

	private MetadataHolder createOneGroupOneAttributeOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		MetadataGroup group = DataCreator.createMetaDataGroup("test", metadataHolder);

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("collectionVarId",
				"collectionVarNameInData", "collectionVarTextId", "collectionVarDefTextId",
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

		group.addAttributeReference("collectionVarId");

		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group, metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testTwoGroupsTwoAttributesOneTextChildOneGroupChildValidData() {
		MetadataHolder metadataHolder = createTwoGroupsTwoAttributesOneTextChildOneGroupChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");

		// dataGroup
		DataGroup dataGroup = DataGroup.withNameInData("childGroupNameInData");
		dataGroup.addAttributeByIdWithValue("test1CollectionVarNameInData", "choice1NameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		// dataGroup2
		DataGroup dataGroup2 = DataGroup.withNameInData("parentGroupNameInData");
		dataGroup2.addAttributeByIdWithValue("test2CollectionVarNameInData", "choice1NameInData");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("text2NameInData", "10:10"));
		dataGroup2.addChild(dataGroup);

		assertEquals(dataElementValidator.validateData(dataGroup2).dataIsValid(), true,
				"The group should be validate to true, as it has " + "valid data");

	}

	private MetadataHolder createTwoGroupsTwoAttributesOneTextChildOneGroupChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		MetadataGroup childGroup = DataCreator.createMetaDataGroup("child", metadataHolder);
		MetadataGroup parentGroup = DataCreator.createMetaDataGroup("parent", metadataHolder);

		// collection groupType
		DataCreator.addDefaultCollectionTwoChoices("test1", childGroup, metadataHolder);
		DataCreator.addDefaultCollectionTwoChoices("test2", parentGroup, metadataHolder);

		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", childGroup, metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text2", parentGroup, metadataHolder);

		MetadataChildReference groupChild = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("childGroupId", 1, 1);
		parentGroup.addChildReference(groupChild);

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
	 * correct number of attributes/children, one missing, one extra, correct value, etc?
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
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");

		DataAtomic child1 = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		child1.setRepeatId("0");
		dataGroup.addChild(child1);

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	private MetadataHolder createMetadataForOneSimpleGroup() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);

		// collection groupType
		DataCreator.addDefaultCollectionTwoChoices("test", metadataGroup, metadataHolder);

/*
		CollectionVariable colVar = new CollectionVariable("groupTypeVar", "groupTypeVar",
				"groupTypeVarText", "groupTypeVarDefText", "groupTypeCollection");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem groupType1 = new CollectionItem("groupType1", "groupType1", "groupType1Text",
				"groupType1DefText");
		metadataHolder.addMetadataElement(groupType1);

		CollectionItem groupType2 = new CollectionItem("groupType2", "groupType2", "groupType2Text",
				"groupType2DefText");
		metadataHolder.addMetadataElement(groupType2);

		ItemCollection groupTypeCollection = new ItemCollection("groupTypeCollection",
				"groupTypeCollection", "groupTypeCollectionText", "groupTypeCollectionDefText");
		metadataHolder.addMetadataElement(groupTypeCollection);
		groupTypeCollection.addItemReference("groupType1");
		groupTypeCollection.addItemReference("groupType2");*/

		// child
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup, metadataHolder);

		// attribute references
//		metadataGroup.addAttributeReference("groupTypeVar");

		return metadataHolder;
	}

	@Test
	public void testInvalidAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1_NOT_VALID");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as attributes value is invalid");
	}

	@Test
	public void testMissingAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		// dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as it does not have a needed attribute");
	}

	@Test
	public void testExtraAttribute() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("testCollectionVar", "choice1Id");
		dataGroup.addAttributeByIdWithValue("test2CollectionVar", "choice1Id");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false as it has an extra attribute");
	}

	@Test
	public void testValidateOneWrongDataChildElement() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "66:66"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	@Test
	public void testValidateTwoRightDataChildElements() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		DataAtomic child1 = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		child1.setRepeatId("0");
		dataGroup.addChild(child1);
		DataAtomic child2 = DataAtomic.withNameInDataAndValue("text1NameInData", "01:11");
		child2.setRepeatId("1");
		dataGroup.addChild(child2);

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	@Test
	public void testValidateWithOneRightAndOneWrongAttributeName() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addAttributeByIdWithValue("groupTypeVar2", "groupType1");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	@Test
	public void testValidateWithOneWrongAttributeValue() throws Exception {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType11");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	// @Test(expectedExceptions = RuntimeException.class)
	@Test
	public void dataWithNameInDataNotInMetadataShouldNotBeValid() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("testCollectionVar", "choice1Id");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("unknownNameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), false,
				"The group should be validate to false");
	}

	@Test
	public void testAdvancedGroupOneRightDataChildElement() {
		MetadataHolder metadataHolder = createMetadataForOneGroupDoubleAttributesAndChildren();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		// 1-x,1-1,0-x
		DataAtomic child1 = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		child1.setRepeatId("one");
		dataGroup.addChild(child1);
		DataAtomic child2 = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		child2.setRepeatId("two");
		dataGroup.addChild(child2);
		DataAtomic child3 = DataAtomic.withNameInDataAndValue("text2NameInData", "10:10");
		dataGroup.addChild(child3);
		DataAtomic child4 = DataAtomic.withNameInDataAndValue("text3NameInData", "10:10");
		child4.setRepeatId("four");
		dataGroup.addChild(child4);
		DataAtomic child5 = DataAtomic.withNameInDataAndValue("text3NameInData", "10:10");
		child5.setRepeatId("five");
		dataGroup.addChild(child5);

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be validate to true");
	}

	private MetadataHolder createMetadataForOneGroupDoubleAttributesAndChildren() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);

		// collection groupType
		CollectionVariable colVar = new CollectionVariable("groupTypeVar", "groupTypeVar",
				"groupTypeVarText", "groupTypeVarDefText", "groupTypeCollection");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem groupType1 = new CollectionItem("groupType1", "groupType1", "groupType1Text",
				"groupType1DefText");
		metadataHolder.addMetadataElement(groupType1);

		CollectionItem groupType2 = new CollectionItem("groupType2", "groupType2", "groupType2Text",
				"groupType2DefText");
		metadataHolder.addMetadataElement(groupType2);

		ItemCollection groupTypeCollection = new ItemCollection("groupTypeCollection",
				"groupTypeCollection", "groupTypeCollectionText", "groupTypeCollectionDefText");
		metadataHolder.addMetadataElement(groupTypeCollection);
		groupTypeCollection.addItemReference("groupType1");
		groupTypeCollection.addItemReference("groupType2");

		// children
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup, metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text2", metadataGroup, metadataHolder);
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text3", metadataGroup, metadataHolder);

		// attribute references
		metadataGroup.addAttributeReference("groupTypeVar");

		return metadataHolder;
	}

	private MetadataHolder createMetadataGroupWithUnlimitedChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);
		// child
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup, metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testValidRepeatChild() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		dataAtomic.setRepeatId("3");
		dataGroup.addChild(dataAtomic);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testInvalidRepeatChildMissing() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		dataGroup.addChild(dataAtomic);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsInvalid());
	}

	@Test
	public void testInvalidRepeatChildEmpty() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		dataAtomic.setRepeatId("");
		dataGroup.addChild(dataAtomic);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsInvalid());
	}

	@Test
	public void testSameRepeatId() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupNameInData");

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		dataAtomic.setRepeatId("1");
		dataGroup.addChild(dataAtomic);

		DataAtomic dataAtomic2 = DataAtomic.withNameInDataAndValue("text1NameInData", "10:20");
		dataAtomic2.setRepeatId("1");
		dataGroup.addChild(dataAtomic2);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsInvalid());
	}

	@Test
	public void testRepeatIdWhereNotExpected() {
		MetadataHolder metadataHolder = createMetadataGroupWithOneChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = DataGroup.withNameInData("testGroupId");

		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("text1NameInData", "10:10");
		dataAtomic.setRepeatId("1");
		dataGroup.addChild(dataAtomic);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsInvalid());
	}

	private MetadataHolder createMetadataGroupWithOneChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", metadataGroup, metadataHolder);

		return metadataHolder;
	}


}
