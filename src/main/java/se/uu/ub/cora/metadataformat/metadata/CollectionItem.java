/**
 *  CollectionItem.java - 
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
package se.uu.ub.cora.metadataformat.metadata;

/**
 * CollectionItem is the metadataformat class that handles Collection Items
 * 
 * @author olov
 * @since 7.0.0
 */
public class CollectionItem extends MetadataElement {

	/**
	 * constructor
	 * 
	 * @param id
	 * @param nameInData
	 */
	public CollectionItem(String id, String nameInData, String textId, String defTextId) {
		super(id, nameInData, textId, defTextId);
	}
}
