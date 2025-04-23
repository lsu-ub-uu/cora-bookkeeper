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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;

public class TextHolderImp implements TextHolder {

	private Map<String, TextElement> textElements = new HashMap<>();

	@Override
	public void addTextElement(TextElement textElement) {
		textElements.put(textElement.getId(), textElement);
	}

	@Override
	public TextElement getTextElement(String textId) {
		throwExceptionIfTextElementIsMissing(textId);
		return textElements.get(textId);
	}

	private void throwExceptionIfTextElementIsMissing(String textId) {
		if (!textElements.containsKey(textId)) {
			throw createDataMissingException(textId);
		}
	}

	private DataMissingException createDataMissingException(String textId) {
		String message = MessageFormat
				.format("Text with id: {0} could not be found in the text holder.", textId);
		return new DataMissingException(message);
	}

	@Override
	public void deleteTextElement(String textId) {
		textElements.remove(textId);
	}

	@Override
	public boolean containsTextElement(String textId) {
		return textElements.containsKey(textId);
	}

}
