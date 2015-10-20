/*
 * Copyright 2015 Uppsala University Library
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
package se.uu.ub.cora.metadataformat.metadata;

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
