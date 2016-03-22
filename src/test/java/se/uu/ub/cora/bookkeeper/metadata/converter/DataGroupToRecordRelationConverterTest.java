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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.RecordRelation;

public class DataGroupToRecordRelationConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createDataGroup();

		DataGroupToRecordRelationConverter converter = DataGroupToRecordRelationConverter
				.fromDataGroup(dataGroup);

		RecordRelation recordRelation = converter.toMetadata();
		assertEquals(recordRelation.getId(), "otherId");
		assertEquals(recordRelation.getNameInData(), "other");
		assertEquals(recordRelation.getTextId(), "otherTextId");
		assertEquals(recordRelation.getDefTextId(), "otherDefTextId");
		assertEquals(recordRelation.getRefRecordLinkId(), "otherRefRecordLinkId");
		assertEquals(recordRelation.getRefMetadataGroupId(), "otherRefMetadataGroupId");
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordRelation");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refRecordLinkId",
				"otherRefRecordLinkId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refMetadataGroupId",
				"otherRefMetadataGroupId"));

		return dataGroup;
	}

	@Test
	public void testToMetadataAsChildGroup() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "refParentId"));

		DataGroupToRecordRelationConverter converter = DataGroupToRecordRelationConverter
				.fromDataGroup(dataGroup);

		RecordRelation recordRelation = converter.toMetadata();
		assertEquals(recordRelation.getRefParentId(), "refParentId");
	}
}
