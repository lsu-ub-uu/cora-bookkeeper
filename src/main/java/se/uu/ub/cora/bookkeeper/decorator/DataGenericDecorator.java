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

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.text.TextElement;
import se.uu.ub.cora.bookkeeper.text.TextHolder;
import se.uu.ub.cora.bookkeeper.text.Translation;
import se.uu.ub.cora.data.DataChild;

class DataGenericDecorator implements DataChildDecorator {

	private MetadataElement metadataElement;
	private TextHolder textHolder;
	private DataChildDecorator extraDecorator;

	public DataGenericDecorator(MetadataElement metadataElement, TextHolder textHolder) {
		this.metadataElement = metadataElement;
		this.textHolder = textHolder;
	}

	@Override
	public void decorateData(DataChild dataChild) {
		decorateDataChildWithTexts(dataChild);
		possiblyRunExtraDecorator(dataChild);
	}

	private void possiblyRunExtraDecorator(DataChild dataChild) {
		if (null != extraDecorator) {
			extraDecorator.decorateData(dataChild);
		}
	}

	private void decorateDataChildWithTexts(DataChild dataChild) {
		String textId = metadataElement.getTextId();
		TextElement textElement = textHolder.getTextElement(textId);
		Set<Translation> translations = textElement.getTranslations();
		addTranslationsAsAttributes(dataChild, translations);
	}

	private void addTranslationsAsAttributes(DataChild dataChild, Set<Translation> translations) {
		translations.forEach(translation -> addAttribute(dataChild, translation));
	}

	private void addAttribute(DataChild dataChild, Translation translation) {
		String language = attributeNameWithUnderscore(translation.language());
		String text = translation.text();
		dataChild.addAttributeByIdWithValue(language, text);
	}

	private String attributeNameWithUnderscore(String language) {
		return "_" + language;
	}

	public MetadataElement onlyForTestGetMetadataElement() {
		return metadataElement;
	}

	public TextHolder onlyForTestGetTextHolder() {
		return textHolder;
	}

	public void setExtraDecorator(DataChildDecorator extraChildDecorator) {
		this.extraDecorator = extraChildDecorator;
	}
}
