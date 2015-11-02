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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DataTextVariableValidatorTest {
	private DataElementValidator textDataValidator;

	@BeforeMethod
	public void setUp() {
		TextVariable textVariable = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData", "textId",
						"defTextId", "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$)|^$){1}");

		textDataValidator = new DataTextVariableValidator(textVariable);
	}

	@Test
	public void testValidate() {
		DataAtomic data = DataAtomic.withNameInDataAndValue("nameInData", "10:10");
		assertTrue(textDataValidator.validateData(data).dataIsValid(),
				"The regular expression should be validated to true");
	}

	@Test
	public void testInvalidCase() {
		DataAtomic data = DataAtomic.withNameInDataAndValue("nameInData", "1010");
		assertFalse(textDataValidator.validateData(data).dataIsValid(),
				"The regular expression should be validated to false");
	}
}
