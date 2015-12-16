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

package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.MetadataStorageStub;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DataRecordLinkCollectorTest {
	private MetadataStorage metadataStorage;
	private DataRecordLinkCollector linkCollector;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageStub();
		linkCollector = new DataRecordLinkCollectorImp(metadataStorage);
	}

	@Test
	public void testCollectLinksGroupWithoutLink() {
		DataGroup dataGroup = DataGroup.withNameInData("bush");
		DataGroup collectedLinks = linkCollector.collectLinks("bush", dataGroup, "recordType",
				"recordId");
		assertEquals(collectedLinks.getNameInData(), "collectedDataLinks");
		assertTrue(collectedLinks.getChildren().isEmpty());
	}

	@Test
	public void testCollectLinksGroupWithOneLink() {
		// data
		DataGroup dataGroup = DataGroup.withNameInData("bush");

		DataGroup dataTestLink = DataGroup.withNameInData("testLink");

		DataAtomic linkedRecordType = DataAtomic.withNameInDataAndValue("linkedRecordType", "bush");
		dataTestLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = DataAtomic.withNameInDataAndValue("linkedRecordId", "bush1");
		dataTestLink.addChild(linkedRecordId);
		dataGroup.addChild(dataTestLink);

		DataGroup collectedLinks = linkCollector.collectLinks("bush", dataGroup, "recordType",
				"recordId");

		assertEquals(collectedLinks.getNameInData(), "collectedDataLinks");
		List<DataElement> linkList = collectedLinks.getChildren();
		assertEquals(linkList.size(), 1);

	}
}
