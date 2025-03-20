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

import java.util.ArrayList;
import java.util.List;

/**
 * MetadataGroup handles metadata groups.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataGroup extends MetadataElementAbstract {

	private final List<String> attributeReferences = new ArrayList<>();
	private final List<MetadataChildReference> childReferences = new ArrayList<>();
	private String refParentId;

	public static MetadataGroup withIdAndNameInDataAndTextIdAndDefTextId(String id,
			String nameInData, String textId, String defTextId) {
		return new MetadataGroup(id, nameInData, textId, defTextId);
	}

	protected MetadataGroup(String id, String nameInData, String textId, String defTextId) {
		super(id, nameInData, textId, defTextId);
	}

	@Override
	public List<String> getAttributeReferences() {
		return attributeReferences;
	}

	public List<MetadataChildReference> getChildReferences() {
		return childReferences;
	}

	public void addAttributeReference(String attributeReferenceId) {
		attributeReferences.add(attributeReferenceId);

	}

	public void addChildReference(MetadataChildReference metadataChildReference) {
		childReferences.add(metadataChildReference);
	}

	public void setRefParentId(String refParentId) {
		this.refParentId = refParentId;

	}

	public String getRefParentId() {
		return refParentId;
	}

}
