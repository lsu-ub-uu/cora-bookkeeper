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

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.text.TextElement;
import se.uu.ub.cora.bookkeeper.text.TextHolder;
import se.uu.ub.cora.bookkeeper.text.TextHolderProvider;
import se.uu.ub.cora.bookkeeper.text.Translation;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;

class DataCollectionVarDecorator implements DataChildDecorator {
	private MetadataHolder metadataHolder;
	private TextHolder textHolder;
	private CollectionVariable collectionVar;

	public DataCollectionVarDecorator(CollectionVariable collectionVar) {
		this.collectionVar = collectionVar;
	}

	@Override
	public void decorateData(DataChild dataChild) {
		metadataHolder = MetadataHolderProvider.getHolder();
		textHolder = TextHolderProvider.getHolder();
		decorateDataAtomic((DataAtomic) dataChild);
	}

	private void decorateDataAtomic(DataAtomic atomic) {
		ItemCollection referredCollection = (ItemCollection) metadataHolder
				.getMetadataElement(collectionVar.getRefCollectionId());

		var streamOfItemReferences = referredCollection.getCollectionItemReferences().stream();
		var streamOfCollectionItems = streamOfItemReferences.map(getItemFromMetadataHolder());
		var oFirstMatching = streamOfCollectionItems.filter(matchItemValue(atomic)).findFirst();
		oFirstMatching.ifPresent(addValueTextAsAttributeOnDataAtomic(atomic));
	}

	private Function<String, CollectionItem> getItemFromMetadataHolder() {
		return ref -> (CollectionItem) metadataHolder.getMetadataElement(ref);
	}

	private Predicate<CollectionItem> matchItemValue(DataAtomic atomic) {
		return colItem -> colItem.getNameInData().equals(atomic.getValue());
	}

	private Consumer<CollectionItem> addValueTextAsAttributeOnDataAtomic(DataAtomic atomic) {
		return colItem -> {
			TextElement textElement = textHolder.getTextElement(colItem.getTextId());
			decorateAtomicDataWithTexts(atomic, textElement);
		};
	}

	private void decorateAtomicDataWithTexts(DataChild dataChild, TextElement textElement) {
		Set<Translation> translations = textElement.getTranslations();
		addTranslationsAsAttributes(dataChild, translations);
	}

	private void addTranslationsAsAttributes(DataChild dataChild, Set<Translation> translations) {
		translations.forEach(translation -> addAttribute(dataChild, translation));
	}

	private void addAttribute(DataChild dataChild, Translation translation) {
		String language = attributeNameWithPrefix(translation.language());
		String text = translation.text();
		dataChild.addAttributeByIdWithValue(language, text);
	}

	private String attributeNameWithPrefix(String language) {
		return "_value_" + language;
	}

	public Object onlyForTestGetMetadataElement() {
		return collectionVar;
	}
}
