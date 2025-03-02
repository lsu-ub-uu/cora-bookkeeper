/*
 * Copyright 2018, 2019 Uppsala University Library
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToNumberVariableConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToNumberVariableConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addNumberSpecificToDataRecordGroup();

		converter = DataToNumberVariableConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addNumberSpecificToDataRecordGroup() {
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "min", "0");
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "max", "5");
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "warningMin", "1");
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "warningMax", "4");
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "numberOfDecimals", "1");
	}

	@Test
	public void testStandardParameters() {
		assertCorrectStandardParameters();
	}

	private void assertCorrectStandardParameters() {
		NumberVariable numberVariable = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						numberVariable, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());
	}

	@Test
	public void testnumberSpecificParameters() {
		assertCorrectNumberSpecificParameters();
	}

	private void assertCorrectNumberSpecificParameters() {
		NumberVariable numberVariable = converter.toMetadata();

		assertEquals(numberVariable.getMin(), 0.0);
		assertEquals(numberVariable.getMax(), 5.0);
		assertEquals(numberVariable.getWarningMin(), 1.0);
		assertEquals(numberVariable.getWarningMax(), 4.0);
		assertEquals(numberVariable.getNumOfDecmials(), 1);
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		MetadataElement numberVariable = converter.toMetadata();

		assertEquals(numberVariable.getAttributeReferences().size(), 2);
		assertEquals(numberVariable.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(numberVariable.getAttributeReferences().get(1), "someOtherCollectionVar");
	}
}
