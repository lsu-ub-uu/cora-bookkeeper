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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

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
		DataElement data = DataAtomic.withNameInDataAndValue("anId", "12:12");
		ValidationAnswer validationAnswer = dataValidator.validateData("doesNotExist", data);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertFalse(validationAnswer.dataIsValid(),
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
		Assert.assertEquals(dataValidator.validateData("collectionVar2", data).dataIsValid(), true);
		DataElement data2 = DataAtomic.withNameInDataAndValue("collectionVar2", "place");
		assertTrue(dataValidator.validateData("collectionVar2", data2).dataIsValid());
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

	@Test
	public void testValidateRecordLink() {

		DataGroup dataGroup = DataGroup.withNameInData("bush");
		DataGroup dataTestLink = DataGroup.withNameInData("testLink");
		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType",
				"linkedRecordType1");
		dataTestLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId", "bush1");
		dataTestLink.addChild(linkedRecordId);
		dataGroup.addChild(dataTestLink);
		ValidationAnswer validationAnswer = dataValidator.validateData("bush", dataGroup);
		assertTrue(validationAnswer.dataIsValid(), "The group should be validate to true");
	}
}
