/*
 * Copyright 2018, 2019, 2021 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.LimitsContainer;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.bookkeeper.metadata.StandardMetadataParameters;
import se.uu.ub.cora.bookkeeper.metadata.TextContainer;
import se.uu.ub.cora.data.DataAtomic;

public class DataNumberVariableValidatorTest {
	private DataElementValidator numberDataValidator;
	private int min = 1;
	private int max = 10;

	@BeforeMethod
	public void setUp() {
		TextContainer textContainer = TextContainer.usingTextIdAndDefTextId("someText",
				"someDefText");
		StandardMetadataParameters standardParams = StandardMetadataParameters
				.usingIdNameInDataAndTextContainer("someId", "someNameInData", textContainer);

		LimitsContainer limits = LimitsContainer.usingMinAndMax(min, max);
		LimitsContainer warnLimits = LimitsContainer.usingMinAndMax(2, 8);

		NumberVariable numberVariable = NumberVariable
				.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(standardParams, limits,
						warnLimits, 4);

		numberDataValidator = new DataNumberVariableValidator(numberVariable);

	}

	@Test
	public void testInvalidNumberBelowMinimum() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", "0");
		ValidationAnswer validateAnswer = numberDataValidator.validateData(number);
		assertTrue(validateAnswer.dataIsInvalid());

		assertNumberOfErrorMessages(validateAnswer, 1);

		assertFirstErrorMessageIs(validateAnswer,
				"NumberVariable with nameInData: someNameInData is NOT valid,"
						+ " value 0 is outside range of 1 - 10");
	}

	private void assertFirstErrorMessageIs(ValidationAnswer validateAnswer,
			String expectedErrorMessage) {
		Object errorMessages = getFirstErrorMessage(validateAnswer.getErrorMessages());
		assertEquals(errorMessages, expectedErrorMessage);
	}

	private void assertNumberOfErrorMessages(ValidationAnswer validateAnswer,
			int numberOfErrorMessages) {
		Collection<String> errorMessages = validateAnswer.getErrorMessages();
		assertEquals(errorMessages.size(), numberOfErrorMessages);
	}

	private Object getFirstErrorMessage(Collection<String> errorMessages) {
		Object[] errorMessagesArray = errorMessages.toArray();
		Object firstErrorMessage = errorMessagesArray[0];
		return firstErrorMessage;
	}

	@Test
	public void testInvalidNumberOverMaximum() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", "12");
		ValidationAnswer validateAnswer = numberDataValidator.validateData(number);
		assertTrue(validateAnswer.dataIsInvalid());
		assertNumberOfErrorMessages(validateAnswer, 1);

		assertFirstErrorMessageIs(validateAnswer,
				"NumberVariable with nameInData: someNameInData is NOT valid,"
						+ " value 12 is outside range of 1 - 10");

	}

	@Test
	public void testValidNumberSameAsMin() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", String.valueOf(min));
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberSameAsMax() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", String.valueOf(max));
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberBetweenMinAndMax() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", "3");
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberBetweenMinAndMaxButTooManyDecimals() {
		DataAtomic number = new DataAtomicOldSpy("nameInData", "3.1245674456543");
		ValidationAnswer validateAnswer = numberDataValidator.validateData(number);
		assertTrue(validateAnswer.dataIsInvalid());

		assertNumberOfErrorMessages(validateAnswer, 1);

		assertFirstErrorMessageIs(validateAnswer,
				"NumberVariable with nameInData: someNameInData is NOT valid,"
						+ " value 3.1245674456543 has more decimals than the allowed 4");

	}
}
