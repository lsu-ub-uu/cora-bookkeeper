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

package se.uu.ub.cora.bookkeeper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.text.Translation;

public class TranslationHolder {
	private Map<String, String> translations = new LinkedHashMap<>();

	public void addTranslation(String languageId, String text) {
		translations.put(languageId, text);
	}

	public String getTranslation(String languageId) {
		return translations.get(languageId);
	}

	public Set<Translation> getTranslations() {
		return translations.entrySet().stream()
				.map(entry -> new Translation(entry.getKey(), entry.getValue()))
				.collect(java.util.stream.Collectors.toSet());
	}
}
