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

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;

public class TextHolderProvider {

	private TextHolderProvider() {
		// not called
		throw new UnsupportedOperationException();
	}

	private static TextHolder textHolder;
	private static TextHolderPopulator textHolderPopulator;

	public static TextHolder getHolder() {
		synchronized (TextHolderProvider.class) {
			startMetadtaHolderIfNotStarted();
			return textHolder;
		}
	}

	private static void startMetadtaHolderIfNotStarted() {
		if (textHolder == null) {
			if (textHolderPopulator == null) {
				textHolderPopulator = new TextHolderPopulatorImp();
			}
			textHolder = textHolderPopulator.createAndPopulateTextHolderFromMetadataStorage();
		}
	}

	/**
	 * dataChanged method is intended to inform the instance provider about data that is changed in
	 * storage. This is to make it possible to implement a cached storage and update relevant
	 * records when data is changed. This change can be done by processes running in the same system
	 * or by processes running on other servers.
	 * 
	 * @param id
	 *            A String with the records id
	 * @param action
	 *            A String with the action of how the data was changed ("create", "update" or
	 *            "delete").
	 */
	public static void dataChanged(String id, String action) {
		dataChangedSynchonizedWithGetHolder(id, action);
	}

	private static void dataChangedSynchonizedWithGetHolder(String id, String action) {
		synchronized (TextHolderProvider.class) {
			possiblyUpdateTextHolderWithLatestDataChanges(id, action);
		}
	}

	private static void possiblyUpdateTextHolderWithLatestDataChanges(String id, String action) {
		if (null != textHolder) {
			updateTextHolderWithLatestDataChanges(id, action);
		}
	}

	private static void updateTextHolderWithLatestDataChanges(String id, String action) {
		if ("delete".equals(action)) {
			textHolder.deleteTextElement(id);
		} else {
			updateTextHolderWithLatestDataFromStorage(id);
		}
	}

	private static void updateTextHolderWithLatestDataFromStorage(String id) {
		MetadataStorageView storageView = MetadataStorageProvider.getStorageView();
		TextElement textElement = storageView.getTextElement(id);
		textHolder.addTextElement(textElement);
	}

	/**
	 * Sets a TextHolder that will be returned. This possibility to set a TextHolder is provided to
	 * enable testing of using the TextHolder in other classes and is not intented to be used in
	 * production.
	 * 
	 * @param textHolder
	 *            A TextHolder to use return to the caller of getHolder
	 */
	public static void onlyForTestSetHolder(TextHolder textHolderIn) {
		textHolder = textHolderIn;
	}

	static void onlyForTestSetTextHolderPopulator(TextHolderPopulator textHolderPopulatorIn) {
		textHolderPopulator = textHolderPopulatorIn;

	}

	static TextHolderPopulator onlyForTestGetTextHolderPopulator() {
		return textHolderPopulator;
	}
}
