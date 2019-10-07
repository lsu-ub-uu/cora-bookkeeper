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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
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
	private DataValidatorImp dataValidator;
	private MetadataStorage metadataStorage;
	private DataGroup dataGroupToValidate;
	private DataValidatorFactorySpy validatorFactory;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForDataValidatorSpy();
		validatorFactory = new DataValidatorFactorySpy();
		dataValidator = new DataValidatorImp(metadataStorage, validatorFactory);
		dataGroupToValidate = new DataGroupSpy("someGroup");
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

}