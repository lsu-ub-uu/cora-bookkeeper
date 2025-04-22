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

package se.uu.ub.cora.bookkeeper.decorator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.spies.DataAtomicSpy;

public class DataTextVariableDecoratorTest {
	private DataChildDecorator textDataChildDecorator;
	private TextVariable metadataTextVariable;
	private DataAtomicSpy dataTextVariable;

	@BeforeMethod
	public void setUp() {
		metadataTextVariable = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData",
						"textId", "defTextId", "^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$");

		textDataChildDecorator = new DataTextVariableDecorator(metadataTextVariable);
		dataTextVariable = new DataAtomicSpy();
	}

	@Test
	public void testDecorate() {
		// validateDataAndAssertValid("10:10");
		textDataChildDecorator.decorateData(dataTextVariable);

		dataTextVariable.MCR.assertParameters("addAttributeByIdWithValue", 0, "atext");

	}

	// private void validateDataAndAssertValid(String value) {
	// // DataAtomic data = new DataAtomicOldSpy("nameInData", value);
	// assertTrue(textDataChildDecorator.validateData(data).dataIsValid());
	// }

	// @Test
	// public void testInvalidCase() {
	// validateDataAndAssertInvalid("1010");
	// }
	//
	// @Test
	// public void testEmptyValue() {
	// validateDataAndAssertInvalid("");
	// }
	//
	// private void validateDataAndAssertInvalid(String value) {
	// DataAtomic data = new DataAtomicOldSpy("nameInData", value);
	// assertFalse(textDataChildDecorator.validateData(data).dataIsValid());
	// }
	//
	// @Test
	// public void testValidateFinalValueValidData() {
	// String value = "12:12";
	// metadataTextVariable.setFinalValue(value);
	// validateDataAndAssertValid(value);
	// }
	//
	// @Test
	// public void testValidateFinalValueInvalidData() {
	// metadataTextVariable.setFinalValue("12:12");
	// String value = "12:10";
	// validateDataAndAssertInvalid(value);
	// }
	//
	// @Test(expectedExceptions = ConfigurationException.class, expectedExceptionsMessageRegExp =
	// "Error while validating data. The regular expression (.|\\n|\\r)+ caused a stack overflow.")
	// public void handleStackOverflowErrorWhileMatchingARegexp() throws Exception {
	// DataElementValidator textDataValidator = setUpRegexpWithCatastrophicBacktracking();
	// DataAtomicSpy data = new DataAtomicSpy();
	// data.MRV.setDefaultReturnValuesSupplier("getValue", () -> getLongDummyString());
	//
	// textDataValidator.validateData(data).dataIsValid();
	// }
	//
	// private DataElementValidator setUpRegexpWithCatastrophicBacktracking() {
	// metadataTextVariable = TextVariable
	// .withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData",
	// "textId", "defTextId", "(.|\\n|\\r)+");
	// DataElementValidator textDataValidator = new DataTextVariableValidator(
	// metadataTextVariable);
	// return textDataValidator;
	// }
	//
	// @Test
	// public void handlePatternSyntaxExceptionWhileMatchingARegexp() throws Exception {
	// DataElementValidator textDataValidator = setUpRegexpWithSyntaxError();
	// DataAtomicSpy data = new DataAtomicSpy();
	// data.MRV.setDefaultReturnValuesSupplier("getValue", () -> "someValue");
	//
	// try {
	// textDataValidator.validateData(data).dataIsValid();
	// } catch (Exception e) {
	// assertTrue(e instanceof ConfigurationException);
	// assertEquals(e.getMessage(),
	// "Error while validating data using regular expression: (badRegEx . "
	// + "Unclosed group near index 9\n(badRegEx");
	// assertEquals(e.getCause().toString(),
	// "java.util.regex.PatternSyntaxException: Unclosed group near index 9\n(badRegEx");
	// }
	// }
	//
	// private DataElementValidator setUpRegexpWithSyntaxError() {
	// metadataTextVariable = TextVariable
	// .withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData",
	// "textId", "defTextId", "(badRegEx");
	// DataElementValidator textDataValidator = new DataTextVariableValidator(
	// metadataTextVariable);
	// return textDataValidator;
	// }
	//
	// private String getLongDummyString() {
	// return """
	// The beauty of autumn lies in its subtle transition between warmth and cold. The vibrant
	// colors of red, orange, and yellow leaves blanket the ground, creating a vivid contrast with
	// the crisp air. As the days shorten, there's a sense of nostalgia in the atmosphere, reminding
	// us of the fleeting nature of time. It's a season that invites reflection, where the outdoors
	// seem to slow down, and we too feel compelled to pause and enjoy the moment.
	//
	// In cities, autumn often brings a shift in pace. Streets once bustling with the energy of
	// summer are now quieter, as people retreat indoors to find warmth. Parks, once filled with
	// picnics and games, now offer a more solitary experience, perfect for long walks and moments
	// of solitude. This is a time when many seek comfort in books, hot drinks, and the cozy
	// ambiance of home, allowing for a deeper connection to one's thoughts and surroundings.
	//
	// For nature, autumn marks a critical time of preparation. Animals gather food and build
	// shelters in anticipation of the coming winter. Trees shed their leaves, conserving energy for
	// the colder months. The cycle of life becomes more visible as plants and creatures adapt to
	// the changing environment, demonstrating resilience and the natural balance of the ecosystem.
	// It's a reminder of the interconnectedness of all living things.
	//
	// As autumn progresses, the anticipation of winter grows. The crisp mornings turn colder, and
	// the first frost may appear. Yet, there's something comforting in the predictability of the
	// seasons, a rhythm that has guided human life for centuries. We know that after the stillness
	// of winter, a new spring will emerge, full of life and possibility. Until then, autumn offers
	// a gentle transition, allowing us to prepare for the next chapter.
	// """;
	// }
}
