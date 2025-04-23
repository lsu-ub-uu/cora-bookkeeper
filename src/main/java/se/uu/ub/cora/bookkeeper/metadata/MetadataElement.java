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
package se.uu.ub.cora.bookkeeper.metadata;

import java.util.List;

/**
 * MetadataElement is an interface for all metadata elements in Cora.
 * <p>
 * Metadata elements are used to define the structure and content of metadata in Cora.
 * <p>
 * The interface provides methods to access the id, nameInData, textID, defTextId and attribute
 * references of a metadata element.
 */
public interface MetadataElement {

	/**
	 * Returns the ID of the metadata element.
	 *
	 * @return the ID of the metadata element
	 */
	String getId();

	/**
	 * Returns the nameInData of the metadata element in data.
	 *
	 * @return the nameInData of the metadata element in data
	 */
	String getNameInData();

	/**
	 * Returns the textId of the metadata element.
	 *
	 * @return the textId of the metadata element
	 */
	String getTextId();

	/**
	 * Returns the defTextId of the metadata element.
	 *
	 * @return the defTextId of the metadata element
	 */
	String getDefTextId();

	/**
	 * Returns a list of attribute references associated with the metadata element.
	 *
	 * @return a list of attribute references
	 */
	List<String> getAttributeReferences();

}