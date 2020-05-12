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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

/**
 * ValidateDataTest tests that the ValidateData class correctly validates data based on the
 * metadataFormat
 * 
 */
public class DataValidatorTest {
	private DataValidatorImp dataValidator;
	private MetadataStorageForDataValidatorSpy metadataStorage;
	private DataGroup dataGroupToValidate;
	private DataValidatorFactorySpy validatorFactory;
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForDataValidatorSpy();
		validatorFactory = new DataValidatorFactorySpy();
		addRecordTypesToHolder();
		dataValidator = new DataValidatorImp(metadataStorage, validatorFactory, recordTypeHolder);
		dataGroupToValidate = new DataGroupSpy("someGroup");
	}

	private void addRecordTypesToHolder() {
		DataGroupCheckCallsSpy someRecordTypeWithFilter = new DataGroupCheckCallsSpy();
		DataGroupSpy filter = new DataGroupSpy("filter");
		filter.addChild(new DataAtomicSpy("linkedRecordId", "someFilter"));
		recordTypeHolder.put("someRecordTypeWithFilter", someRecordTypeWithFilter);

		DataGroupCheckCallsSpy someRecordTypeWithoutFilter = new DataGroupCheckCallsSpy();
		recordTypeHolder.put("someRecordTypeWithoutFilter", someRecordTypeWithoutFilter);
	}

	@Test
	public void testGetMetadataStorage() {
		DataValidatorImp dataValidatorImp = dataValidator;
		assertSame(dataValidatorImp.getMetadataStorage(), metadataStorage);
	}

	@Test
	public void testDataValidatorDataIsInvalidOneMessage() {
		validatorFactory.numOfInvalidMessages = 1;
		ValidationAnswer validationAnswer = dataValidator.validateData("someMetadataId",
				dataGroupToValidate);
		assertFalse(validationAnswer.dataIsValid());
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.get(0), "an errorMessageFromSpy 0");

	}

	private List<String> getErrorMessagesAsList(ValidationAnswer validationAnswer) {
		List<String> errorMessagesAsList = new ArrayList<>();
		errorMessagesAsList.addAll(validationAnswer.getErrorMessages());
		return errorMessagesAsList;
	}

	@Test
	public void testDataValidatorDataIsInvalidThreeMessage() {
		validatorFactory.numOfInvalidMessages = 3;
		ValidationAnswer validationAnswer = dataValidator.validateData("someMetadataId",
				dataGroupToValidate);
		assertFalse(validationAnswer.dataIsValid());
		assertEquals(validationAnswer.getErrorMessages().size(), 3);
		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.get(0), "an errorMessageFromSpy 0");
		assertEquals(errorMessagesAsList.get(1), "an errorMessageFromSpy 1");
		assertEquals(errorMessagesAsList.get(2), "an errorMessageFromSpy 2");
	}

	@Test
	public void testDataValidatorDataIsValid() {
		ValidationAnswer validationAnswer = dataValidator.validateData("someMetadataId",
				dataGroupToValidate);
		assertTrue(validationAnswer.dataIsValid());
	}

	@Test
	public void testDataValidatorFactoryWasCalled() {
		dataValidator.validateData("someMetadataId", dataGroupToValidate);
		assertTrue(validatorFactory.factorWasCalled);
		assertEquals(validatorFactory.metadataIdSentToFactory, "someMetadataId");

		DataElementValidatorSpy elementValidator = validatorFactory.elementValidator;
		assertEquals(elementValidator.dataElement, dataGroupToValidate);
	}

	@Test
	public void testNoValidatorFactored() {
		validatorFactory.throwError = true;
		ValidationAnswer validationAnswer = dataValidator.validateData("someMetadataId",
				dataGroupToValidate);
		assertTrue(validatorFactory.factorWasCalled);

		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.size(), 1);
		assertEquals(errorMessagesAsList.get(0),
				"DataElementValidator not created for the requested metadataId: "
						+ "someMetadataId with error: Error from validatorFactorySpy");
	}

	@Test
	public void testGetDataValidatorFactory() {
		assertSame(dataValidator.getDataValidatorFactory(), validatorFactory);
	}

	@Test
	public void testValidateListFilter() {
		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();
		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter("someRecordTypeWithFilter", filterDataGroup);
		DataGroupCheckCallsSpy recordTypeDataGroup = (DataGroupCheckCallsSpy) recordTypeHolder
				.get("someRecordTypeWithFilter");

		assertFirstRequestedChildGroupIsFilter(recordTypeDataGroup);
		assertValueFromFilterLInkedIdIsSentToValidator(recordTypeDataGroup);

		DataElementValidatorSpy elementValidator = validatorFactory.elementValidator;
		assertEquals(elementValidator.dataElement, filterDataGroup);
		assertEquals(validationAnswer, elementValidator.validationAnswer);
	}

	private void assertFirstRequestedChildGroupIsFilter(
			DataGroupCheckCallsSpy recordTypeDataGroup) {
		assertEquals(recordTypeDataGroup.requestedFirstGroupWithNameInData.get(0), "filter");
	}

	private void assertValueFromFilterLInkedIdIsSentToValidator(
			DataGroupCheckCallsSpy recordTypeDataGroup) {
		DataGroupCheckCallsSpy returnedChild = recordTypeDataGroup.returnedDataGroup;
		assertEquals(returnedChild.requestedFirstAtomicValue.get(0), "linkedRecordId");
		assertEquals(validatorFactory.metadataIdSentToFactory,
				returnedChild.returnedAtomicValues.get(0));
	}

	@Test
	public void testValidateListFilterInvalidOneMessage() {
		validatorFactory.numOfInvalidMessages = 1;
		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter("someRecordTypeWithFilter", new DataGroupCheckCallsSpy());

		assertFalse(validationAnswer.dataIsValid());
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.get(0), "an errorMessageFromSpy 0");

	}

	@Test
	public void testValidateListFilterNoValidatorFactored() {
		validatorFactory.throwError = true;
		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter("someRecordTypeWithFilter", new DataGroupCheckCallsSpy());
		assertTrue(validatorFactory.factorWasCalled);

		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.size(), 1);
		assertEquals(errorMessagesAsList.get(0),
				"DataElementValidator not created for the requested metadataId: "
						+ "someValueForlinkedRecordId with error: Error from validatorFactorySpy");
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "No filter exists for recordType: someRecordTypeWithoutFilter")
	public void testValidateNoListFilterThrowsException() {
		DataGroupCheckCallsSpy dataGroupWithoutFilterSpy = (DataGroupCheckCallsSpy) recordTypeHolder
				.get("someRecordTypeWithoutFilter");
		dataGroupWithoutFilterSpy.nameInDatasToNotContain.add("filter");

		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();
		dataValidator.validateListFilter("someRecordTypeWithoutFilter", filterDataGroup);

	}

	@Test
	public void testgetRecordTypeHolder() throws Exception {
		assertNotNull(dataValidator.getRecordTypeHolder());
	}

}