/*
 * Copyright 2015, 2025 Uppsala University Library
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

import java.util.Set;

import se.uu.ub.cora.bookkeeper.TranslationHolder;

/**
 * TextElement holds information about a text and the translation of that text
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public final class TextElementImp implements TextElement {

	private final String id;
	private final TranslationHolder translationHolder;

	public static TextElementImp withId(String id) {
		return new TextElementImp(id);
	}

	private TextElementImp(String id) {
		this.id = id;
		this.translationHolder = new TranslationHolder();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Set<Translation> getTranslations() {
		return translationHolder.getTranslations();
	}

	@Override
	public String getTranslationByLanguage(String language) {
		return translationHolder.getTranslation(language);
	}

	@Override
	public void addTranslation(String language, String text) {
		translationHolder.addTranslation(language, text);
	}

}
