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

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;

/**
 * TextHolder holds all texts in cora.
 */
public interface TextHolder {

	/**
	 * addTextElement adds an element to the internal holder of texts, if the text already exists it
	 * should be replaced
	 * 
	 * @param textElement
	 *            A TextElement to add to the internal holder.
	 */
	void addTextElement(TextElement textElement);

	/**
	 * getTextElement returns the requested TextElement based on the parameter textId
	 * <p>
	 * if the element does not exist, SHOULD an DataMissingException be thrown
	 * 
	 * @param textId
	 *            A String with the id of the Text element to get
	 * @throws DataMissingException
	 *             if the text does not exist
	 * @return The requested MetadataElement
	 */
	TextElement getTextElement(String textId);

	/**
	 * deleteTextElement removes the TextElement with the given textId from the holder
	 * 
	 * @param textId
	 *            A String with the id of the Text element to delete
	 */
	void deleteTextElement(String textId);

	/**
	 * containsTextElement checks if the holder contains a text with the given textId
	 * 
	 * @param textId
	 *            A String with the id of the Text element to check for
	 * @return A boolean indicating whether the text is in the holder or not.
	 */
	boolean containsTextElement(String textId);

}
