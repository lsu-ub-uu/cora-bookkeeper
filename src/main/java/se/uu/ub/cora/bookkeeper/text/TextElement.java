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
package se.uu.ub.cora.bookkeeper.text;

import java.util.Set;

public interface TextElement {

	/**
	 * getId returns the id of the text element
	 * 
	 * @return A String with the id of the text element
	 */
	String getId();

	/**
	 * getTranslations returns all the translations of the text element
	 * 
	 * @return A Set with all the translations of the text element
	 */
	Set<Translation> getTranslations();

	/**
	 * getTranslationByLanguage returns the translation of the text element in the given language
	 * 
	 * @param language
	 *            A String with the language of the translation to get
	 * @return A String with the translation of the text element in the given language
	 */
	String getTranslationByLanguage(String language);

	/**
	 * addTranslation adds a translation to the text element
	 * 
	 * @param language
	 *            A String with the language of the translation to add
	 * @param text
	 *            A String with the text of the translation to add
	 */
	void addTranslation(String language, String text);

}