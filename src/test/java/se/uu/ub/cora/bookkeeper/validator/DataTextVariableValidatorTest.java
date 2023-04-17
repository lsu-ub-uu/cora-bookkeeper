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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataAtomic;

public class DataTextVariableValidatorTest {
	private DataElementValidator textDataValidator;
	private TextVariable textVariable;

	@BeforeMethod
	public void setUp() {
		textVariable = TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(
				"id", "nameInData", "textId", "defTextId",
				"^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$");

		textDataValidator = new DataTextVariableValidator(textVariable);
	}

	@Test
	public void testValidate() {
		validateDataAndAssertValid("10:10");
	}

	private void validateDataAndAssertValid(String value) {
		DataAtomic data = new DataAtomicOldSpy("nameInData", value);
		assertTrue(textDataValidator.validateData(data).dataIsValid());
	}

	@Test
	public void testInvalidCase() {
		validateDataAndAssertInvalid("1010");
	}

	@Test
	public void testEmptyValue() {
		validateDataAndAssertInvalid("");
	}

	private void validateDataAndAssertInvalid(String value) {
		DataAtomic data = new DataAtomicOldSpy("nameInData", value);
		assertFalse(textDataValidator.validateData(data).dataIsValid());
	}

	@Test
	public void testValidateFinalValueValidData() {
		String value = "12:12";
		textVariable.setFinalValue(value);
		validateDataAndAssertValid(value);
	}

	@Test
	public void testValidateFinalValueInvalidData() {
		textVariable.setFinalValue("12:12");
		String value = "12:10";
		validateDataAndAssertInvalid(value);
	}

}
