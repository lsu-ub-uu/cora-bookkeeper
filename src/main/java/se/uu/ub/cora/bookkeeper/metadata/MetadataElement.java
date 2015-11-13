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

package se.uu.ub.cora.bookkeeper.metadata;

import java.util.Collections;
import java.util.List;

/**
 * MetadataElement is an abstract class that holds the common attributes
 * associated with metadataElements
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public abstract class MetadataElement {

	private final String id;
	private final String nameInData;
	private final String textId;
	private final String defTextId;

	protected MetadataElement(String id, String nameInData, String textId, String defTextId) {
		this.id = id;
		this.nameInData = nameInData;
		this.textId = textId;
		this.defTextId = defTextId;
	}

	public String getId() {
		return id;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getTextId() {
		return textId;
	}

	public String getDefTextId() {
		return defTextId;
	}

	public List<String> getAttributeReferences() {
		return Collections.emptyList();
	}
}
