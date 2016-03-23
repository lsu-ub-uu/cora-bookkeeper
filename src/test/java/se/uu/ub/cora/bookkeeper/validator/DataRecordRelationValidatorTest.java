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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataRecordRelationValidatorTest {
	private DataElementValidator dataElementValidator;

	@BeforeMethod
	public void setUp() {
		dataElementValidator = createOneRecordRelationWithOneTextChildReturnDataElementValidator();

	}

	@Test
	public void testRecordRelationValidData() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertTrue(dataElementValidator.validateData(dataGroup).dataIsValid(),
				dataElementValidator.validateData(dataGroup).getErrorMessages().toString());
	}

	private DataElementValidator createOneRecordRelationWithOneTextChildReturnDataElementValidator() {
		MetadataHolder metadataHolder = createOneRecordRelationOneTextChild();
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		return dataValidatorFactory.factor("ourRelation");
	}

	private MetadataHolder createOneRecordRelationOneTextChild() {
		MetadataHolder metadataHolder = new MetadataHolder();
		DataCreator.createRecordRelation("ourRelation", "ourRelation", "testLink",
				"relationInfoGroup", metadataHolder);

		// recordLInk
		RecordLink recordLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType("testLink", "testLink",
						"textId", "defTextId", "metadataGroup");
		metadataHolder.addMetadataElement(recordLink);

		// relationInfoGroup
		MetadataGroup group = DataCreator.createMetaDataGroupWithIdAndNameInData(
				"relationInfoGroup", "relationInfoGroup", metadataHolder);
		DataCreator.addTextVarWithIdAndNameInDataAndRegExChildReferenceToGroup("whatEverTextVar",
				"whatEverText", "^.*$", group, metadataHolder);

		return metadataHolder;
	}

	@Test(expectedExceptions = DataValidationException.class)
	public void testRecordRelationInValidDataMissingPiecesOfMetadata() {
		MetadataHolder metadataHolder = new MetadataHolder();
		DataCreator.createRecordRelation("ourRelation", "ourRelation", "testLink",
				"relationInfoGroup", metadataHolder);
		DataValidatorFactory dataValidatorFactory = new DataValidatorFactoryImp(metadataHolder);
		DataElementValidator dataElementValidator = dataValidatorFactory.factor("ourRelation");

		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		dataElementValidator.validateData(dataGroup);
	}

	@Test
	public void testRecordRelationInValidDataExtraRepeatId() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		testLink.setRepeatId("1");
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataWrongNameInData() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelationNOT");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataMissingLink() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataWrongNameInDataForLink() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLinkNOT");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataWrongRecordTypeForLink() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textVariable"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroup");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataWrongNameInDataForMetadataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroupNOT");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataMissingMetadataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}

	@Test
	public void testRecordRelationInValidDataMetadataGroupWithRepeatId() {
		DataGroup dataGroup = DataGroup.withNameInData("ourRelation");

		DataGroup testLink = DataGroup.withNameInData("testLink");
		testLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataGroup"));
		testLink.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", "metadataTextVariableGroup"));
		dataGroup.addChild(testLink);

		DataGroup relationInfo = DataGroup.withNameInData("relationInfoGroupNOT");
		relationInfo.addChild(DataAtomic.withNameInDataAndValue("whatEverText", "some text"));
		relationInfo.setRepeatId("2");
		dataGroup.addChild(relationInfo);

		assertFalse(dataElementValidator.validateData(dataGroup).dataIsValid());
	}
}
