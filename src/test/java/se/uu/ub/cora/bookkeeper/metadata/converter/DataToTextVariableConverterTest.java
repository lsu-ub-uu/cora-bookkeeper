/*
 * Copyright 2015, 2019, 2022 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToTextVariableConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToTextVariableConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addCollectionVariableSpecificsToDataRecordGroup();

		converter = DataToTextVariableConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addCollectionVariableSpecificsToDataRecordGroup() {
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "regEx",
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}");
	}

	@Test
	public void testToMetadata() {
		TextVariable textVariable = converter.toMetadata();

		assertBasicTextVariableValuesAreCorrect(textVariable);
		assertEquals(textVariable.getRefParentId(), null);
		assertEquals(textVariable.getFinalValue(), null);
	}

	private void assertBasicTextVariableValuesAreCorrect(TextVariable textVariable) {
		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						textVariable, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());

		assertEquals(textVariable.getRegularExpression(),
				"((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}");
	}

	@Test
	public void testToMetadataWithRefParentId() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refParentId", "refParentId");

		TextVariable textVariable = converter.toMetadata();

		assertBasicTextVariableValuesAreCorrect(textVariable);
		assertEquals(textVariable.getRefParentId(), "refParentId");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "finalValue", "finalValue");

		TextVariable textVariable = converter.toMetadata();

		assertBasicTextVariableValuesAreCorrect(textVariable);
		assertEquals(textVariable.getFinalValue(), "finalValue");
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		TextVariable textVariable = converter.toMetadata();

		assertEquals(textVariable.getAttributeReferences().size(), 2);
		assertEquals(textVariable.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(textVariable.getAttributeReferences().get(1), "someOtherCollectionVar");
	}
}
