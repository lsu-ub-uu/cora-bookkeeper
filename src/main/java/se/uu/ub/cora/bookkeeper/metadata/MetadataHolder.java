/*
 * Copyright 2023 Uppsala University Library
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
	 * addMetadataElement adds an element to the internal holder of elements
	 * 
	 * @param metadataElement
	 *            A MetadataElement to add to the internal holder
	 */
	void addMetadataElement(MetadataElement metadataElement);

	/**
	 * getMetadataElement returns the requested MetadataElement based on the parameter elementId
	 * 
	 * @param elementId
	 *            A String with the id of the Metadata element to get
	 * @return The requested MetadataElement
	 */
	MetadataElement getMetadataElement(String elementId);

}