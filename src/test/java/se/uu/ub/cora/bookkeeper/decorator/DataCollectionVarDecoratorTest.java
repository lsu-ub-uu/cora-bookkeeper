/*
 * Copyright 2025 Uppsala University Library
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

import static org.testng.Assert.assertSame;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.text.TextElementSpy;
import se.uu.ub.cora.bookkeeper.text.TextHolderProvider;
import se.uu.ub.cora.bookkeeper.text.Translation;
import se.uu.ub.cora.data.spies.DataAtomicSpy;

public class DataCollectionVarDecoratorTest {
	private MetadataHolderSpy metadataHolder;
	private TextHolderSpy textHolder;
	private DataCollectionVarDecorator decorator;
	private DataAtomicSpy dataAtomic;
	private CollectionVariable collectionVar;
	private MetadataMatchDataFactorySpy metadataMatchFactory;
	private MetadataMatchDataSpy matcher;

	@BeforeMethod
	public void beforMethod() {
		metadataHolder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolder);
		textHolder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(textHolder);
		setUpMetadataMatchFactory();

		createItemCollectionAndAddToMetadataHolder("blue", "yellow");

		textHolder.MRV.setSpecificReturnValuesSupplier("getTextElement", this::createTextElement,
				"yellowTextId");

		createTextElement();

		createCollectionVarMetadata();
		createDataAtomic("yellow");

		decorator = new DataCollectionVarDecorator(collectionVar);
	}

	private void createItemCollectionAndAddToMetadataHolder(String... itemValues) {
		ItemCollection itemCollection = new ItemCollection("someItemColId", "someItemColNameInData",
				"someItemText", "someItemDefText");
		for (var itemValue : itemValues) {
			itemCollection.addItemReference(itemValue + "Id");
			CollectionItem collectionItem = new CollectionItem(itemValue + "Id", itemValue,
					itemValue + "TextId", itemValue + "DefTextId");
			metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
					() -> collectionItem, itemValue + "Id");
		}
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> itemCollection, "collectionVarCollectionId");

	}

	private TextElementSpy createTextElement() {
		TextElementSpy textElement = new TextElementSpy();
		Set<Translation> translations = new LinkedHashSet<>();
		translations.add(new Translation("en", "a yellow text"));
		translations.add(new Translation("sv", "en gul text"));
		textElement.MRV.setDefaultReturnValuesSupplier("getTranslations", () -> translations);
		return textElement;
	}

	private void setUpMetadataMatchFactory() {
		metadataMatchFactory = new MetadataMatchDataFactorySpy();
		createMatcher();
	}

	private void createMatcher() {
		matcher = new MetadataMatchDataSpy();
		metadataMatchFactory.MRV.setDefaultReturnValuesSupplier("factor", () -> matcher);
	}

	private void createCollectionVarMetadata() {
		collectionVar = new CollectionVariable("collectionVarId", "collectionVarNameInData",
				"collectionVarTextId", "collectionVarDefTextId", "collectionVarCollectionId");
	}

	private void createDataAtomic(String value) {
		dataAtomic = new DataAtomicSpy();
		dataAtomic.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
	}

	@Test
	public void testDecorate() {
		decorator.decorateData(dataAtomic);

		metadataHolder.MCR.assertParameters("getMetadataElement", 0, "collectionVarCollectionId");
		metadataHolder.MCR.assertParameters("getMetadataElement", 1, "blueId");
		metadataHolder.MCR.assertParameters("getMetadataElement", 2, "yellowId");
		textHolder.MCR.assertParameters("getTextElement", 0, "yellowTextId");

		dataAtomic.MCR.assertCalledParameters("addAttributeByIdWithValue", "_value_sv",
				"en gul text");
		dataAtomic.MCR.assertCalledParameters("addAttributeByIdWithValue", "_value_en",
				"a yellow text");
	}

	@Test
	public void testOnlyForTestGetMetadataElement() {
		assertSame(decorator.onlyForTestGetMetadataElement(), collectionVar);
	}

}
