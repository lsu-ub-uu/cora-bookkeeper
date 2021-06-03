/*
 * Copyright 2015, 2017, 2019 Uppsala University Library
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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataAtomicFactorySpy;
import se.uu.ub.cora.bookkeeper.linkcollector.DataGroupFactorySpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class DataGroupValidatorTest {
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		DataGroup image = new DataGroupSpy("image");
		DataGroup parentId = new DataGroupSpy("parentId");
		image.addChild(parentId);
		parentId.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		parentId.addChild(new DataAtomicSpy("linkedRecordId", "binary"));
		recordTypeHolder.put("image", image);

	}

	@Test
	public void testOneGroupNoAttributesOneTextChildWrongNameInData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("groupDataERRORId");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid as the nameInData is invalid");
	}

	private DataElementValidator createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneTextChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		return dataValidatorFactory.factor("testGroupId");
	}

	private MetadataHolder createOneGroupNoAttributesOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = DataCreator.createMetaDataGroup("test", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group, metadataHolder);
		return metadataHolder;
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildValidData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid(),
				"The group should be valid");
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testMetadataGroupThatRefersToMetadataChildThatDoesNotExist() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = DataCreator.createMetaDataGroup("test", metadataHolder);
		MetadataChildReference groupChild = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadataGroup",
						"IdToChildThatDoesNotExist", 1, 1);

		group.addChildReference(groupChild);
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = new DataGroupValidator(
				(DataElementValidatorFactoryImp) dataValidatorFactory, metadataHolder, group);

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("NOT_text1NameInData", "10:10"));
		dataElementValidator.validateData(dataGroup);
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraAttribute() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has an attribute it should not have");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildNameInData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("textNameInDataERROR", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 2,
				"Two messages, missing child and has unknown child");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has a child with wrong nameInData");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildInvalidChildData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10Error10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has a child with wrong data");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildMissingChildData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 2, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it does not have a child");
	}

	@Test
	public void testOneGroupNoAttributesOneTextChildExtraChildData() {
		DataElementValidator dataElementValidator = createOneGroupWithNoAttributesOneTextChildReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has too many children");
	}

	@Test
	public void testOneGroupNoAttributesOneRecordLinkChildValidData() {
		MetadataHolder metadataHolder = createOneGroupNoAttributesOneRecordLinkChild();
		addLinkedRecordIdTextVarToMetadataHolder(metadataHolder);

		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("groupId");

		DataGroup dataGroup = new DataGroupSpy("groupNameInData");
		dataGroup.addChild(DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"recordLinkNameInData", "recordLinkLinkedRecordType", "someRecordLinkId"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	private void addLinkedRecordIdTextVarToMetadataHolder(MetadataHolder metadataHolder) {
		TextVariable linkedRecordIdTextVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
						"linkedRecordIdTextVar", "linkedRecordId", "linkedRecordIdTextVarText",
						"linkedRecordIdTextVarDefText", "(^[0-9A-Za-z:-_]{2,50}$)");

		metadataHolder.addMetadataElement(linkedRecordIdTextVar);
	}

	private MetadataHolder createOneGroupNoAttributesOneRecordLinkChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("groupId",
				"groupNameInData", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);

		DataCreator.addRecordLinkChildReferenceToGroup("recordLink", group, metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildValidData() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		assertEquals(dataElementValidator.validateData(dataGroup).dataIsValid(), true,
				"The group should be valid, as it has valid data");
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildInvalidAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1ERRORNameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has an invalid attribute");
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildMissingAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid, as it has a missing attribute");
	}

	@Test
	public void testOneGroupOneAttributeOneTextChildInvalidExtraAttribute() {
		MetadataHolder metadataHolder = createOneGroupOneAttributeOneTextChild();
		createSecondCollectionVariable(metadataHolder);

		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addAttributeByIdWithValue("_INVALID_NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid as it has an attribute that doesn't exist in metadata");
	}

	private void createSecondCollectionVariable(final MetadataHolder metadataHolder) {
		// Extra metadata collection for this test only
		CollectionVariable colVar2 = new CollectionVariable("col2Id", "col2NameInData",
				"col2TextId", "col2DefTextId", "collectionId");
		metadataHolder.addMetadataElement(colVar2);
	}

	private MetadataHolder createOneGroupOneAttributeOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		MetadataGroup group = DataCreator.createMetaDataGroup("test", metadataHolder);
		DataCreator.addDefaultCollectionTwoChoices("col1", group, metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group, metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testTwoGroupsTwoAttributesOneTextChildOneGroupChildValidData() {
		MetadataHolder metadataHolder = createTwoGroupsTwoAttributesOneTextChildOneGroupChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");

		// dataGroup
		DataGroup dataGroup = new DataGroupSpy("childGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10"));

		// dataGroup2
		DataGroup dataGroup2 = new DataGroupSpy("parentGroupNameInData");
		dataGroup2.addAttributeByIdWithValue("col2NameInData", "choice1NameInData");
		dataGroup2.addChild(new DataAtomicSpy("text2NameInData", "10:10"));
		dataGroup2.addChild(dataGroup);

		assertTrue(dataElementValidator.validateData(dataGroup2).dataIsValid(),
				"The group should be valid as it has valid data");
	}

	private MetadataHolder createTwoGroupsTwoAttributesOneTextChildOneGroupChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		MetadataGroup childGroup = DataCreator.createMetaDataGroup("child", metadataHolder);
		MetadataGroup parentGroup = DataCreator.createMetaDataGroup("parent", metadataHolder);

		// collection groupType
		DataCreator.addDefaultCollectionTwoChoices("col1", childGroup, metadataHolder);
		DataCreator.addDefaultCollectionTwoChoices("col2", parentGroup, metadataHolder);

		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", childGroup, metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text2", parentGroup, metadataHolder);

		MetadataChildReference groupChild = MetadataChildReference
				.withLinkedRecordTypeAndLinkedRecordIdAndRepeatMinAndRepeatMax("metadataGroup",
						"childGroupId", 1, 1);
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
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");

		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid(),
				"The group should be validate to true");
	}

	private DataElementValidator createMetadataForOneSimpleGroupReturnDataElementValidator() {
		MetadataHolder metadataHolder = createMetadataForOneSimpleGroup();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		return dataValidatorFactory.factor("testGroupId");
	}

	private MetadataHolder createMetadataForOneSimpleGroup() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);

		// collection groupType
		DataCreator.addDefaultCollectionTwoChoices("col1", metadataGroup, metadataHolder);

		// child
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup,
				metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testInvalidAttribute() {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData_NOT_VALID");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not validate as attribute value is invalid");
	}

	@Test
	public void testMissingAttribute() {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		// dataGroup.addAttributeByIdWithValue("col1NameInData",
		// "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid as it does not have the needed attribute");
	}

	@Test
	public void testExtraAttribute() {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addAttributeByIdWithValue("col2NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid(),
				"The group should be validate to false as it has an extra attribute");
	}

	@Test
	public void testValidateOneWrongDataChildElement() {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "66:66", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);

		assertFalse(validationAnswer.dataIsValid(), "The group should be validate to false");
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertTrue(validationAnswer.getErrorMessages().contains(
				"TextVariable with nameInData:text1NameInData is NOT valid, regular expression(((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}) does not match:66:66"));

	}

	@Test
	public void testValidateTwoRightDataChildElements() throws Exception {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "01:11", "1"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid(),
				"The group should be valid");
	}

	@Test
	public void testValidateWithOneRightAndOneWrongAttributeName() throws Exception {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addAttributeByIdWithValue("col2NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should not be valid because one attribute is wrong");
	}

	@Test
	public void testValidateWithOneWrongAttributeValue() throws Exception {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1_WRONG_NameInData");
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(), "The group should not be valid");
	}

	@Test
	public void dataWithNameInDataNotInMetadataShouldNotBeValid() {
		DataElementValidator dataElementValidator = createMetadataForOneSimpleGroupReturnDataElementValidator();

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		dataGroup.addChild(new DataAtomicSpy("unknownNameInData", "10:10", "0"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 2,
				"Should have error message for not enough children 'text1Id' and no metadata found for 'unknownNameInData'");
		assertFalse(validationAnswer.dataIsValid(), "The group should not be valid");
	}

	@Test
	public void testAdvancedGroupOneRightDataChildElement() {
		MetadataHolder metadataHolder = createMetadataForOneGroupDoubleAttributesAndChildren();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");
		dataGroup.addAttributeByIdWithValue("col1NameInData", "choice1NameInData");
		// 1-x,1-1,0-x
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "one"));
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "two"));
		dataGroup.addChild(new DataAtomicSpy("text2NameInData", "10:10"));
		dataGroup.addChild(new DataAtomicSpy("text3NameInData", "10:10", "four"));
		dataGroup.addChild(new DataAtomicSpy("text3NameInData", "10:10", "five"));

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	private MetadataHolder createMetadataForOneGroupDoubleAttributesAndChildren() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);

		// collection groupType
		DataCreator.addDefaultCollectionTwoChoices("col1", metadataGroup, metadataHolder);

		// children
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup,
				metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text2", metadataGroup, metadataHolder);
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text3", metadataGroup,
				metadataHolder);

		return metadataHolder;
	}

	private MetadataHolder createMetadataGroupWithUnlimitedChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);
		// child
		DataCreator.addUnlimitedTextVarChildReferenceToGroup("text1", metadataGroup,
				metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testValidRepeatChild() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		DataAtomic dataAtomic = new DataAtomicSpy("text1NameInData", "10:10", "3");
		// dataAtomic.setRepeatId("3");
		dataGroup.addChild(dataAtomic);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testInvalidRepeatChildMissing() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		DataAtomic dataAtomic = new DataAtomicSpy("text1NameInData", "10:10");
		dataGroup.addChild(dataAtomic);

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should be validate to false - has no repeatId");
	}

	@Test
	public void testInvalidRepeatChildEmpty() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", ""));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(), "The group should be validate to false");
	}

	@Test
	public void testSameRepeatId() {
		MetadataHolder metadataHolder = createMetadataGroupWithUnlimitedChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "1"));
		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:20", "1"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should be validate to false - same repeatId");
	}

	@Test
	public void testRepeatIdWhereNotExpected() {
		MetadataHolder metadataHolder = createMetadataGroupWithOneChild();
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		dataGroup.addChild(new DataAtomicSpy("text1NameInData", "10:10", "1"));

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(), "The group should be validate to false");
	}

	private MetadataHolder createMetadataGroupWithOneChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", metadataGroup, metadataHolder);

		return metadataHolder;
	}

	@Test
	public void testTwoGroupsWithSameAttributeDifferentValues() {
		MetadataHolder metadataHolder = new MetadataHolder();
		createMetadataTwoGroupsOneTextWithSameParentDifferentAttributeValues(metadataHolder);

		DataGroup dataParent = new DataGroupSpy("parentGroupNameInData");

		dataParent.addChild(createDataGroupWithTextChildAndOneAttribute("dateType", "birthYear",
				"text1NameInData"));
		dataParent.addChild(createDataGroupWithTextChildAndOneAttribute("dateType", "deathYear",
				"text2NameInData"));
		dataParent.addChild(DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"linkNameInData", "recordLinkLinkedRecordType", "linkedRecordId"));

		addLinkedRecordIdTextVarToMetadataHolder(metadataHolder);
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");
		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataParent);

		assertTrue(validationAnswer.dataIsValid());
	}

	private DataGroup createDataGroupWithTextChildAndOneAttribute(final String attributeName,
			final String attributeValue, final String childGroupNameInData) {
		DataGroup childGroup1 = new DataGroupSpy("testGroup");
		childGroup1.addAttributeByIdWithValue(attributeName, attributeValue);
		childGroup1.addChild(new DataAtomicSpy(childGroupNameInData, "10:10"));
		return childGroup1;
	}

	private void createMetadataTwoGroupsOneTextWithSameParentDifferentAttributeValues(
			final MetadataHolder metadataHolder) {
		MetadataGroup parent = DataCreator.createMetaDataGroup("parent", metadataHolder);
		MetadataGroup group1 = DataCreator.createMetaDataGroupWithIdAndNameInData("test1",
				"testGroup", metadataHolder);
		MetadataGroup group2 = DataCreator.createMetaDataGroupWithIdAndNameInData("test2",
				"testGroup", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", group1, metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text2", group2, metadataHolder);

		// Parent collection, contains two types of dates
		CollectionVariable colVar = new CollectionVariable("dateTypeId", "dateType", "Text",
				"DefText", "dateTypeCollectionId");
		metadataHolder.addMetadataElement(colVar);

		CollectionItem choice1 = new CollectionItem("birthYearId", "birthYear", "textId",
				"defTextId");
		metadataHolder.addMetadataElement(choice1);

		CollectionItem choice2 = new CollectionItem("deathYearId", "deathYear", "textId",
				"defTextId");
		metadataHolder.addMetadataElement(choice2);

		ItemCollection collection = new ItemCollection("dateTypeCollectionId",
				"dateTypeCollectionNameInData", "CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(collection);
		collection.addItemReference("birthYearId");
		collection.addItemReference("deathYearId");

		// Two child collections, holds one item each
		CollectionVariable birthColVar = new CollectionVariable("birthDateTypeId", "dateType",
				"Text", "DefText", "birthDateTypeCollectionId");
		birthColVar.setRefParentId("dateTypeId");
		metadataHolder.addMetadataElement(birthColVar);
		CollectionVariable deathColVar = new CollectionVariable("deathDateTypeId", "dateType",
				"Text", "DefText", "deathDateTypeCollectionId");
		deathColVar.setRefParentId("dateTypeId");
		metadataHolder.addMetadataElement(deathColVar);

		ItemCollection birthCollection = new ItemCollection("birthDateTypeCollectionId",
				"birthDateTypeCollectionNameInData", "CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(birthCollection);
		birthCollection.addItemReference("birthYearId");
		ItemCollection deathCollection = new ItemCollection("deathDateTypeCollectionId",
				"deathDateTypeCollectionNameInData", "CollectionTextId", "collectionDefTextId");
		metadataHolder.addMetadataElement(deathCollection);
		deathCollection.addItemReference("deathYearId");

		group1.addAttributeReference("birthDateTypeId");
		group2.addAttributeReference("deathDateTypeId");

		DataCreator.addDataGroupAsMetadataChildReferenceToParent(group1, parent);
		DataCreator.addDataGroupAsMetadataChildReferenceToParent(group2, parent);
		DataCreator.addRecordLinkChildReferenceToGroup("link", parent, metadataHolder);
	}

	@Test
	public void testTwoGroupsWithSameAttributeSameValueInData() {
		MetadataHolder metadataHolder = new MetadataHolder();
		createMetadataTwoGroupsOneTextWithSameParentDifferentAttributeValues(metadataHolder);

		DataGroup dataParent = new DataGroupSpy("parentGroupNameInData");
		dataParent.addChild(createDataGroupWithTextChildAndOneAttribute("dateType", "birthYear",
				"text1NameInData"));
		dataParent.addChild(createDataGroupWithTextChildAndOneAttribute("dateType", "birthYear",
				"text2NameInData"));
		dataParent.addChild(DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"linkNameInData", "recordLinkLinkedRecordType", "linkedRecordId"));

		addLinkedRecordIdTextVarToMetadataHolder(metadataHolder);
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");
		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataParent);
		assertEquals(validationAnswer.getErrorMessages().size(), 4);
		assertFalse(validationAnswer.dataIsValid());

	}

	@Test
	public void testTwoGroupsWithoutRelevantAttribute() {
		MetadataHolder metadataHolder = new MetadataHolder();
		createMetadataTwoGroupsOneTextWithSameParentDifferentAttributeValues(metadataHolder);

		DataGroup dataParent = new DataGroupSpy("parentGroupNameInData");
		DataGroup childGroup1 = new DataGroupSpy("testGroup");
		childGroup1.addChild(new DataAtomicSpy("text1NameInData", "10:10"));
		DataGroup childGroup2 = new DataGroupSpy("testGroup");
		childGroup2.addChild(new DataAtomicSpy("text2NameInData", "10:10"));
		dataParent.addChild(childGroup1);
		dataParent.addChild(childGroup2);
		dataParent.addChild(DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"linkNameInData", "recordLinkLinkedRecordType", "linkedRecordId"));

		addLinkedRecordIdTextVarToMetadataHolder(metadataHolder);
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");
		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataParent);
		assertEquals(validationAnswer.getErrorMessages().size(), 4);
		assertFalse(validationAnswer.dataIsValid());
	}

	@Test
	public void testTwoGroupsWithAttributesNotFoundInMetadata() {
		MetadataHolder metadataHolder = new MetadataHolder();
		createMetadataTwoGroupsOneTextWithSameParentDifferentAttributeValues(metadataHolder);

		DataGroup dataParent = new DataGroupSpy("parentGroupNameInData");
		DataGroup childGroup2 = createDataGroupWithTextChildAndOneAttribute("dateType", "deathYear",
				"text2NameInData");
		childGroup2.addAttributeByIdWithValue("extraAttribute", "value");

		dataParent.addChild(createDataGroupWithTextChildAndOneAttribute("dateType", "birthYear",
				"text1NameInData"));
		dataParent.addChild(childGroup2);
		dataParent.addChild(DataCreator.createRecordLinkGroupWithNameInDataAndRecordTypeAndRecordId(
				"linkNameInData", "recordLinkLinkedRecordType", "linkedRecordId"));

		addLinkedRecordIdTextVarToMetadataHolder(metadataHolder);
		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("parentGroupId");

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataParent);
		assertEquals(validationAnswer.getErrorMessages().size(), 2);
		assertFalse(validationAnswer.dataIsValid(),
				"Should not be valid, since we have an extra attribute in group 2");
	}

	@Test
	public void testGroupValidationThreeLevels() {
		MetadataHolder metadataHolder = new MetadataHolder();

		DataGroup dataGrandParent = createGroupsInThreeLevelsWithMatchingData(metadataHolder,
				false);

		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory
				.factor("grandParentGroupId");

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGrandParent);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testGroupValidationThreeLevelsErrorInChild() {
		MetadataHolder metadataHolder = new MetadataHolder();

		DataGroup dataGrandParent = createGroupsInThreeLevelsWithMatchingData(metadataHolder, true);

		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory
				.factor("grandParentGroupId");

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGrandParent);
		assertFalse(validationAnswer.dataIsValid());
	}

	private DataGroup createGroupsInThreeLevelsWithMatchingData(final MetadataHolder metadataHolder,
			final boolean addExtraChildToData) {
		MetadataGroup grandParent = DataCreator.createMetaDataGroup("grandParent", metadataHolder);
		MetadataGroup parent = DataCreator.createMetaDataGroup("parent", metadataHolder);
		MetadataGroup child = DataCreator.createMetaDataGroupWithIdAndNameInData("test1",
				"testGroup", metadataHolder);
		DataCreator.addOnlyOneTextVarChildReferenceToGroup("text1", child, metadataHolder);
		DataCreator.addDataGroupAsMetadataChildReferenceToParent(parent, grandParent);
		DataCreator.addDataGroupAsMetadataChildReferenceToParent(child, parent);
		DataCreator.addDefaultCollectionTwoChoices("col1", child, metadataHolder);

		DataGroup dataGrandParent = new DataGroupSpy("grandParentGroupNameInData");
		DataGroup dataParent = new DataGroupSpy("parentGroupNameInData");
		dataGrandParent.addChild(dataParent);

		DataGroup dataChild = createDataGroupWithTextChildAndOneAttribute("col1NameInData",
				"choice2NameInData", "text1NameInData");
		dataParent.addChild(dataChild);

		if (addExtraChildToData) {
			dataChild.addAttributeByIdWithValue("extraAttribute", "value");
		}

		return dataGrandParent;
	}

	@Test
	public void testInvalidDataGroupWhenChildrenMissing() {
		MetadataHolder metadataHolder = new MetadataHolder();

		// group
		MetadataGroup metadataGroup = DataCreator.createMetaDataGroup("test", metadataHolder);
		// child
		DataCreator.addTextVarChildReferenceToGroupMinMax("text1", 0, 4, metadataGroup,
				metadataHolder);

		DataElementValidatorFactory dataValidatorFactory = new DataElementValidatorFactoryImp(recordTypeHolder,
				metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("testGroupId");

		DataGroup dataGroup = new DataGroupSpy("testGroupNameInData");

		ValidationAnswer validationAnswer = dataElementValidator.validateData(dataGroup);
		assertEquals(validationAnswer.getErrorMessages().size(), 1, "Only one error message");
		assertFalse(validationAnswer.dataIsValid(),
				"The group should be validate to false - has no repeatId");
	}

}
