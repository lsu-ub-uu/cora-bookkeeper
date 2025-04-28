/*
 * Copyright 2017, 2019, 2023, 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;

final class TextHolderPopulatorImp implements TextHolderPopulator {

	@Override
	public TextHolder createAndPopulateTextHolderFromMetadataStorage() {
		MetadataStorageView metadataStorageView = MetadataStorageProvider.getStorageView();
		TextHolder textHolder = new TextHolderImp();

		Collection<TextElement> textElements = metadataStorageView.getTextElements();
		convertDataToTextElementsAndAddThemToTextHolder(textElements, textHolder);
		return textHolder;
	}

	private void convertDataToTextElementsAndAddThemToTextHolder(
			Collection<TextElement> textElements, TextHolder textHolder) {
		for (TextElement textElement : textElements) {
			textHolder.addTextElement(textElement);
		}
	}
}