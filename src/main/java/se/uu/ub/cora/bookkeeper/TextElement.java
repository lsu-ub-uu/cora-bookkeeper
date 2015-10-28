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

package se.uu.ub.cora.bookkeeper;

/**
 * TextElement holds information about a text and the translation of that text
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public final class TextElement {

	private final String id;
	private final TranslationHolder translationHolder;

	public static TextElement withIdAndTranslationHolder(String id,
			TranslationHolder translationHolder) {
		return new TextElement(id, translationHolder);
	}

	private TextElement(String id, TranslationHolder translationHolder) {
		this.id = id;
		this.translationHolder = translationHolder;
	}

	public String getId() {
		return id;
	}

	public TranslationHolder getTranslations() {
		return translationHolder;
	}

	public String getTranslationByLanguage(String language) {
		return translationHolder.getTranslation(language);
	}

}
