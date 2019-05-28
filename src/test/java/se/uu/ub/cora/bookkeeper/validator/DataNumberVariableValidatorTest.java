/*
 * Copyright 2018, 2019 Uppsala University Library
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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
						warnLimits, 0);

		numberDataValidator = new DataNumberVariableValidator(numberVariable);

	}

	@Test
	public void testInvalidNumberBelowMinimum() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", "0");
		assertTrue(numberDataValidator.validateData(number).dataIsInvalid());
	}

	@Test
	public void testInvalidNumberOverMaximum() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", "12");
		assertTrue(numberDataValidator.validateData(number).dataIsInvalid());
	}

	@Test
	public void testValidNumberSameAsMin() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", String.valueOf(min));
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberSameAsMax() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", String.valueOf(max));
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberBetweenMinAndMax() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", "3");
		assertTrue(numberDataValidator.validateData(number).dataIsValid());
	}

	@Test
	public void testValidNumberBetweenMinAndMaxButTooManyDecimals() {
		DataAtomic number = DataAtomic.withNameInDataAndValue("nameInData", "3.12");
		assertTrue(numberDataValidator.validateData(number).dataIsInvalid());
	}
}
