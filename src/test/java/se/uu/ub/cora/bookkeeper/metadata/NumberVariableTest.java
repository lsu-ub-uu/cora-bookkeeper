/*
 * Copyright 2018, 2022 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NumberVariableTest {
	private NumberVariable number;
	private LimitsContainer limits;
	private LimitsContainer warnLimits;
	private int numOfDecimals;
	private StandardMetadataParameters standardParameters;

	@BeforeMethod
	public void setup() {
		TextContainer texts = TextContainer.usingTextIdAndDefTextId("someText", "someDefText");
		standardParameters = StandardMetadataParameters.usingIdNameInDataAndTextContainer("someId",
				"metadata", texts);

		limits = new LimitsContainer(0, 100.4);
		warnLimits = new LimitsContainer(2, 100);
		numOfDecimals = 2;
		number = NumberVariable.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(
				standardParameters, limits, warnLimits, numOfDecimals);
	}

	@Test
	public void testIdAndNameInDatat() {
		assertEquals(number.getId(), "someId");
		assertEquals(number.getNameInData(), "metadata");
	}

	@Test
	public void testTexts() {
		assertEquals(number.getTextId(), "someText");
		assertEquals(number.getDefTextId(), "someDefText");
	}

	@Test
	public void testMinMax() {
		assertEquals(number.getMin(), limits.min);
		assertEquals(number.getMax(), limits.max);

	}

	@Test
	public void testWarningMinMax() {
		assertEquals(number.getWarningMin(), warnLimits.min);
		assertEquals(number.getWarningMax(), warnLimits.max);
	}

	@Test
	public void testNumOfDecimals() {
		assertEquals(number.getNumOfDecmials(), numOfDecimals);
	}

	@Test
	public void testGetAttributeReferencesNoAttributes() {
		assertTrue(number.getAttributeReferences().isEmpty());
	}

	@Test
	public void testAddAttributeReference() {
		number.addAttributeReference("type");
		assertEquals(number.getAttributeReferences().iterator().next(), "type");
	}
}
