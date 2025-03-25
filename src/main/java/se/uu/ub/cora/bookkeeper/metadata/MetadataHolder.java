/*
 * Copyright 2023, 2025 Uppsala University Library
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

/**
 * MetadataHolder holds all information about MetadataFormats MetadataGroups and MetadataVariables
 */

public interface MetadataHolder {

	/**
	 * addMetadataElement adds an element to the internal holder of elements, if the element already
	 * exists should it be replaced
	 * 
	 * @param metadataElement
	 *            A MetadataElement to add to the internal holder
	 */
	void addMetadataElement(MetadataElement metadataElement);

	/**
	 * getMetadataElement returns the requested MetadataElement based on the parameter elementId
	 * <p>
	 * if the element does not exist, SHOULD an DataMissingException be thrown
	 * 
	 * @param elementId
	 *            A String with the id of the Metadata element to get
	 * @throws DataMissingException
	 *             if the element does not exist
	 * @return The requested MetadataElement
	 */
	MetadataElement getMetadataElement(String elementId);

	/**
	 * deleteMetadataElement removes the MetadataElement with the given elementId from the holder
	 * 
	 * @param elementId
	 *            A String with the id of the Metadata element to delete
	 */
	void deleteMetadataElement(String elementId);

	/**
	 * containsElement checks if the holder contains an element with the given elementId
	 * 
	 * @param elementId
	 *            A String with the id of the Metadata element to check for
	 * @return A boolean indicating if the element is in the holder
	 */
	boolean containsElement(String elementId);

}