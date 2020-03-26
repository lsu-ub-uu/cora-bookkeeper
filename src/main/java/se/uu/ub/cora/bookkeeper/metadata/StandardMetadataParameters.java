/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata;

public class StandardMetadataParameters {

	public String id;
	public String nameInData;
	public String textId;
	public String defTextId;

	private StandardMetadataParameters(String id, String nameInData, TextContainer textContainer) {
		this.id = id;
		this.nameInData = nameInData;
		possiblySetTexts(textContainer);
	}

	private final void possiblySetTexts(TextContainer textContainer) {
		if (textContainer != null) {
			textId = textContainer.textId;
			defTextId = textContainer.defTextId;
		}
	}

	public static StandardMetadataParameters usingIdNameInDataAndTextContainer(String id,
			String nameInData, TextContainer textContainer) {
		return new StandardMetadataParameters(id, nameInData, textContainer);
	}

}
