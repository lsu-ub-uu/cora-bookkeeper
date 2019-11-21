/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2016 Olov McKie
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.metadata.ResourceLink;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToResourceLinkConverterTest {
	@BeforeMethod
	public void setUp() {

	}

	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createDataGroup();

		DataGroupToResourceLinkConverter converter = DataGroupToResourceLinkConverter
				.fromDataGroup(dataGroup);
		ResourceLink resourceLink = converter.toMetadata();

		assertBasicTextVariableValuesAreCorrect(resourceLink);
	}

	private void assertBasicTextVariableValuesAreCorrect(ResourceLink resourceLink) {
		assertEquals(resourceLink.getId(), "otherId");
		assertEquals(resourceLink.getNameInData(), "other");
		assertEquals(resourceLink.getTextId(), "otherTextId");
		assertEquals(resourceLink.getDefTextId(), "otherDefTextId");
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "resourceLink");
		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));

		addTextWithNameInDataAndId(dataGroup, "textId", "otherTextId");
		addTextWithNameInDataAndId(dataGroup, "defTextId", "otherDefTextId");
		return dataGroup;
	}

	private DataGroup addTextWithNameInDataAndId(DataGroup dataGroup, String textIdNameInData,
			String textId) {
		DataGroup textIdGroup = new DataGroupSpy(textIdNameInData);
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "someRecordType"));
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordId", textId));
		dataGroup.addChild(textIdGroup);
		return textIdGroup;
	}
}
