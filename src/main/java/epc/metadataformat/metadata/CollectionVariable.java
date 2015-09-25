/**
 *  CollectionVariable.java - 
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

/**
 * CollectionVariable is the metadataformat class that handles Regular Expressions
 * 
 * @author olov
 * @since 7.0.0
 */
public class CollectionVariable extends MetadataElement {

	private String refCollectionId;

	public CollectionVariable(String id, String nameInData, String textId, String defTextId,
			String refCollectionId) {
		super(id, nameInData, textId, defTextId);
		this.refCollectionId = refCollectionId;
	}

	public String getRefCollectionId() {
		return refCollectionId;
	}
}
