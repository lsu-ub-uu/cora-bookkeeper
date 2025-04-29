/*
 * Copyright 2023, 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.text;

import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class TextHolderPopulatorTest {
	private TextHolderPopulatorImp populator;
	private MetadataStorageViewInstanceProviderSpy instanceProvider;
	private MetadataStorageViewSpy metadataStorageView;
	private List<TextElement> textElements;
	private TextElementSpy textElement1;
	private TextElementSpy textElement2;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setUpMetadataStorageProviderToReturnStorageViewSpy();

		populator = new TextHolderPopulatorImp();
	}

	private void setUpMetadataStorageProviderToReturnStorageViewSpy() {
		instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);
		metadataStorageView = new MetadataStorageViewSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageView);

		textElements = new ArrayList<>();
		metadataStorageView.MRV.setDefaultReturnValuesSupplier("getTextElements",
				() -> textElements);
		textElement1 = new TextElementSpy();
		textElement1.MRV.setDefaultReturnValuesSupplier("getId", () -> "1");
		textElement2 = new TextElementSpy();
		textElement2.MRV.setDefaultReturnValuesSupplier("getId", () -> "2");
		textElements.add(textElement1);
		textElements.add(textElement2);
	}

	@Test
	public void testLazyInit() {
		assertNothingIsInitialized();
	}

	private void assertNothingIsInitialized() {
		instanceProvider.MCR.assertMethodNotCalled("getStorageView");
	}

	@Test
	public void testCreateLoadsAndUsesTextStorage() {
		TextHolder mh = populator.createAndPopulateTextHolderFromMetadataStorage();

		assertTextStorageCreatedAndUsed(mh);
	}

	private void assertTextStorageCreatedAndUsed(TextHolder mh) {
		instanceProvider.MCR.assertMethodWasCalled("getStorageView");

		metadataStorageView.MCR.assertMethodWasCalled("getTextElements");
		assertSame(mh.getTextElement("1"), textElement1);
		assertSame(mh.getTextElement("2"), textElement2);
	}
}
