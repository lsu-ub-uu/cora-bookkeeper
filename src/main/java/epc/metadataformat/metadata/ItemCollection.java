/**
 *  Collection.java - 
 *
 *   This file is part of FreeReg. 
 *   For details see <https://sourceforge.net/projects/freereg/>.
 *	 Copyright (C) 2015 Olov McKie.
 *
 *   FreeReg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   FreeReg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with FreeReg.  If not, see <http://www.gnu.org/licenses/>.
 */
package epc.metadataformat.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection is the metadataformat class that handles Collection Items
 * 
 * @author olov
 * @since 7.0.0
 */
public class ItemCollection extends MetadataElement {

	private final List<String> collectionItemReferences = new ArrayList<>();

	/**
	 * constructor
	 * 
	 * @param id
	 * @param dataId
	 */
	public ItemCollection(String id, String dataId, String textId,
			String defTextId) {
		super(id, dataId, textId, defTextId);
	}

	public java.util.Collection<String> getCollectionItemReferences() {
		return collectionItemReferences;
	}

	public void addItemReference(String itemId) {
		collectionItemReferences.add(itemId);
	}
}
