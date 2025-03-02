/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2016, 2025 Olov McKie
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

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.ResourceLink;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToResourceLinkConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToResourceLinkConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addResourceLinkSpecificsToDataRecordGroup();

		converter = DataToResourceLinkConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addResourceLinkSpecificsToDataRecordGroup() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refCollection", "refCollection");
	}

	@Test
	public void testToMetadata() {
		ResourceLink resourceLink = converter.toMetadata();

		assertBasicTextVariableValuesAreCorrect(resourceLink);
	}

	private void assertBasicTextVariableValuesAreCorrect(ResourceLink resourceLink) {
		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						resourceLink, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());
	}
}
