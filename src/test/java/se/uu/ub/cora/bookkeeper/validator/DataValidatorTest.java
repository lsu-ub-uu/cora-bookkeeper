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

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

/**
 * ValidateDataTest tests that the ValidateData class correctly validates data based on the
 * metadataFormat
 * 
 */
public class DataValidatorTest {
	private static final String FILTER = "filter";
	private static final String INDEX_SETTINGS = "indexSettings";
	private static final String SOME_RECORD_TYPE_WITHOUT_LINKS = "someRecordTypeWithoutLinks";
	private static final String SOME_RECORD_TYPE_WITH_LINKS = "someRecordTypeWithLinks";
	private DataValidatorImp dataValidator;
	private MetadataStorageForDataValidatorSpy metadataStorage;
	private DataGroup dataGroupToValidate;
	private DataElementValidatorFactorySpy validatorFactory;
	private Map<String, DataGroup> recordTypeHolder = new HashMap<>();

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageForDataValidatorSpy();
		validatorFactory = new DataElementValidatorFactorySpy();
		addRecordTypesToHolder();
		dataValidator = new DataValidatorImp(metadataStorage, validatorFactory, recordTypeHolder);
		dataGroupToValidate = new DataGroupSpy("someGroup");
	}

	private void addRecordTypesToHolder() {
		DataGroupCheckCallsSpy someRecordTypeWithFilter = new DataGroupCheckCallsSpy();
		recordTypeHolder.put(SOME_RECORD_TYPE_WITH_LINKS, someRecordTypeWithFilter);

		DataGroupCheckCallsSpy someRecordTypeWithoutFilter = new DataGroupCheckCallsSpy();
		recordTypeHolder.put(SOME_RECORD_TYPE_WITHOUT_LINKS, someRecordTypeWithoutFilter);
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
		assertValidationAnswerIsInvalid(validationAnswer);

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
		assertSame(dataValidator.getDataElementValidatorFactory(), validatorFactory);
	}

	@Test
	public void testValidateListFilter() {
		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();

		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter(SOME_RECORD_TYPE_WITH_LINKS, filterDataGroup);

		assertValidationOfRecordTypeWithLinks(FILTER, validationAnswer, filterDataGroup);

	}

	private void assertValidationOfRecordTypeWithLinks(String nameInData,
			ValidationAnswer validationAnswer, DataGroupCheckCallsSpy filterDataGroup) {

		String extractedLinkID = assertExtractionOfLinkFromRecordTypeInRecordTypeHolder(nameInData);

		asssertValidationAnswerIsFromValidator(validationAnswer, filterDataGroup, extractedLinkID);
	}

	private String assertExtractionOfLinkFromRecordTypeInRecordTypeHolder(String nameInData) {
		DataGroupCheckCallsSpy recordTypeGroupSpy = (DataGroupCheckCallsSpy) recordTypeHolder
				.get(SOME_RECORD_TYPE_WITH_LINKS);
		recordTypeGroupSpy.MCR.assertParameters("getFirstGroupWithNameInData", 0, nameInData);
		DataGroupCheckCallsSpy firstGroupWithNameInDataSpy = (DataGroupCheckCallsSpy) recordTypeGroupSpy.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		firstGroupWithNameInDataSpy.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"linkedRecordId");

		String extractedLinkID = (String) firstGroupWithNameInDataSpy.MCR
				.getReturnValue("getFirstAtomicValueWithNameInData", 0);
		return extractedLinkID;
	}

	private void asssertValidationAnswerIsFromValidator(ValidationAnswer validationAnswer,
			DataGroupCheckCallsSpy filterDataGroup, String extractedLinkID) {
		validatorFactory.MCR.assertParameters("factor", 0, extractedLinkID);

		DataElementValidatorSpy validatorSpy = (DataElementValidatorSpy) validatorFactory.MCR
				.getReturnValue("factor", 0);

		validatorSpy.MCR.assertParameters("validateData", 0, filterDataGroup);

		ValidationAnswer validationAnswerFromValidator = (ValidationAnswer) validatorSpy.MCR
				.getReturnValue("validateData", 0);
		assertEquals(validationAnswer, validationAnswerFromValidator);
	}

	@Test
	public void testValidateListFilterInvalidOneMessage() {
		validatorFactory.numOfInvalidMessages = 1;
		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter(SOME_RECORD_TYPE_WITH_LINKS, new DataGroupCheckCallsSpy());

		assertValidationAnswerIsInvalid(validationAnswer);

	}

	@Test
	public void testValidateListFilterNoValidatorFactored() {
		validatorFactory.throwError = true;
		ValidationAnswer validationAnswer = dataValidator
				.validateListFilter(SOME_RECORD_TYPE_WITH_LINKS, new DataGroupCheckCallsSpy());
		assertTrue(validatorFactory.factorWasCalled);

		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.size(), 1);
		assertEquals(errorMessagesAsList.get(0),
				"DataElementValidator not created for the requested metadataId: "
						+ "someValueForlinkedRecordId with error: Error from validatorFactorySpy");
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "No " + FILTER + " exists for recordType: " + SOME_RECORD_TYPE_WITHOUT_LINKS)
	public void testValidateNoListFilterThrowsException() {
		DataGroupCheckCallsSpy dataGroupWithoutFilterSpy = (DataGroupCheckCallsSpy) recordTypeHolder
				.get(SOME_RECORD_TYPE_WITHOUT_LINKS);
		dataGroupWithoutFilterSpy.nameInDatasToNotContain.add(FILTER);

		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();
		dataValidator.validateListFilter(SOME_RECORD_TYPE_WITHOUT_LINKS, filterDataGroup);

	}

	@Test
	public void testgetRecordTypeHolder() throws Exception {
		assertNotNull(dataValidator.getRecordTypeHolder());
	}

	@Test(expectedExceptions = DataValidationException.class, expectedExceptionsMessageRegExp = ""
			+ "No " + INDEX_SETTINGS + " exists for recordType: " + SOME_RECORD_TYPE_WITHOUT_LINKS)
	public void testValidateNoIndexSettingThrowsException() {
		DataGroupCheckCallsSpy dataGroupWithoutLinksSpy = (DataGroupCheckCallsSpy) recordTypeHolder
				.get(SOME_RECORD_TYPE_WITHOUT_LINKS);
		dataGroupWithoutLinksSpy.nameInDatasToNotContain.add(INDEX_SETTINGS);
		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();

		dataValidator.validateIndexSettings(SOME_RECORD_TYPE_WITHOUT_LINKS, filterDataGroup);
	}

	@Test
	public void testValidateIndexFilterExtractIndexSettingsFactorValidator() throws Exception {

		DataGroupCheckCallsSpy filterDataGroup = new DataGroupCheckCallsSpy();

		ValidationAnswer validationAnswer = dataValidator
				.validateIndexSettings(SOME_RECORD_TYPE_WITH_LINKS, filterDataGroup);

		assertValidationOfRecordTypeWithLinks(INDEX_SETTINGS, validationAnswer, filterDataGroup);

	}

	@Test
	public void testValidateIndexSettingsInvalidOneMessage() {
		validatorFactory.numOfInvalidMessages = 1;
		ValidationAnswer validationAnswer = dataValidator
				.validateIndexSettings(SOME_RECORD_TYPE_WITH_LINKS, new DataGroupCheckCallsSpy());

		assertValidationAnswerIsInvalid(validationAnswer);

	}

	private void assertValidationAnswerIsInvalid(ValidationAnswer validationAnswer) {
		assertFalse(validationAnswer.dataIsValid());
		assertEquals(validationAnswer.getErrorMessages().size(), 1);
		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.get(0), "an errorMessageFromSpy 0");
	}

	@Test
	public void testValidateIndeSettingsNoValidatorFactored() {
		validatorFactory.throwError = true;
		ValidationAnswer validationAnswer = dataValidator
				.validateIndexSettings(SOME_RECORD_TYPE_WITH_LINKS, new DataGroupCheckCallsSpy());
		assertTrue(validatorFactory.factorWasCalled);

		List<String> errorMessagesAsList = getErrorMessagesAsList(validationAnswer);
		assertEquals(errorMessagesAsList.size(), 1);
		assertEquals(errorMessagesAsList.get(0),
				"DataElementValidator not created for the requested metadataId: "
						+ "someValueForlinkedRecordId with error: Error from validatorFactorySpy");
	}

}