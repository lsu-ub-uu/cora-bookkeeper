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

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.validator.MetadataStorageStub;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataFactory;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.collected.Link;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class DataRecordLinkCollectorTest {
	private DataFactory dataFactory;
	private DataRecordLinkCollector linkCollector;
	private MetadataStorageView metadataStorage;
	private LoggerFactorySpy loggerFactory;

	@BeforeMethod
	public void setUp() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		metadataStorage = new MetadataStorageStub();

		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorage);
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		linkCollector = new DataRecordLinkCollectorImp();
	}

	@Test
	public void testCollectLinksGroupWithoutLink() {
		DataGroup dataGroup = new DataGroupOldSpy("bush");
		Set<Link> collectedLinks = linkCollector.collectLinks("bush", dataGroup);
		assertEquals(collectedLinks.size(), 0);
	}

	@Test
	public void testCollectLinksGroupWithOneLink() {
		DataGroup dataGroup = new DataGroupOldSpy("bush");
		DataGroup dataTestLink = new DataGroupOldSpy("testLink");

		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "bush");
		dataTestLink.addChild(linkedRecordType);

		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "bush1");
		dataTestLink.addChild(linkedRecordId);
		dataGroup.addChild(dataTestLink);

		Set<Link> collectedLinks = linkCollector.collectLinks("bush", dataGroup);
		assertEquals(collectedLinks.size(), 1);
		Object[] collectedLinksArray = collectedLinks.toArray();
		Link link = (Link) collectedLinksArray[0];
		assertEquals(link.type(), "bush");
		assertEquals(link.id(), "bush1");

	}

}
