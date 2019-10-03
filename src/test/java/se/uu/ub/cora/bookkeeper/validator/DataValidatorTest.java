/*
 * Copyright 2015, 2019 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.MetadataStorage;

/**
 * ValidateDataTest tests that the ValidateData class correctly validates data based on the
 * metadataFormat
 * 
 * @author olov
 * 
 */
public class DataValidatorTest {
	private DataValidator dataValidator;
	private MetadataStorage metadataStorage;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageStub();
		dataValidator = new DataValidatorImp(metadataStorage);
	}

	@Test
	public void testGetMetadataStorage() {
		DataValidatorImp dataValidatorImp = (DataValidatorImp) dataValidator;
		assertSame(dataValidatorImp.getMetadataStorage(), metadataStorage);
	}

	@Test
	public void testValidateWhereMetadataIdNotPresentInMetadata() {
		DataElement data = new DataAtomicSpy("anId", "12:12");
		ValidationAnswer validationAnswer = dataValidator.validateData("doesNotExist", data);
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		assertFalse(validationAnswer.dataIsValid(),
				"The regular expression should be validated to false");
	}

	@Test
	public void testValidateTextVariable() {
		DataElement data = new DataAtomicSpy("textVar2", "12:12");
		assertTrue(dataValidator.validateData("textVar2", data).dataIsValid(),
				"The regular expression should be validated to true");
	}

	@Test
	public void testValidateCollectionVariable() {
		DataElement data = new DataAtomicSpy("collectionVar2", "person");
		Assert.assertEquals(dataValidator.validateData("collectionVar2", data).dataIsValid(), true);
		DataElement data2 = new DataAtomicSpy("collectionVar2", "place");
		assertTrue(dataValidator.validateData("collectionVar2", data2).dataIsValid());
	}

	@Test
	public void testValidateMetadataGroup() {
		DataGroupSpy dataGroup = new DataGroupSpy("group");
		dataGroup.numOfGetAllGroupsWithNameInDataToReturn.put("ref", 1);
		dataGroup.addAttributeByIdWithValue("groupTypeVar", "groupType1");
		DataAtomic child1 = new DataAtomicSpy("textVarNameInData", "10:10", "4");
		// child1.setRepeatId("4");
		dataGroup.addChild(child1);
		DataAtomic child2 = new DataAtomicSpy("textVarNameInData", "11:11", "3");
		// child2.setRepeatId("3");
		dataGroup.addChild(child2);
		ValidationAnswer validationAnswer = dataValidator.validateData("group", dataGroup);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testValidateRecordLink() {

		DataGroup dataGroup = new DataGroupSpy("bush");
		DataGroup dataTestLink = new DataGroupSpy("testLink");
		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "linkedRecordType1");
		dataTestLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "bush1");
		dataTestLink.addChild(linkedRecordId);
		dataGroup.addChild(dataTestLink);
		ValidationAnswer validationAnswer = dataValidator.validateData("bush", dataGroup);
		assertTrue(validationAnswer.dataIsValid(), "The group should be validate to true");
	}
}
