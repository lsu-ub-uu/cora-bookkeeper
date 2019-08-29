/*
 * Copyright 2015, 2019 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;

public final class RecordLink extends MetadataElement {
	private String linkedRecordType;
	private DataGroup linkedPath;
	private String refParentId;
	private String finalValue;
	private final List<String> attributeReferences = new ArrayList<>();

	private RecordLink(String id, String nameInData, String textId, String defTextId,
			String linkedRecordType) {
		super(id, nameInData, textId, defTextId);
		this.linkedRecordType = linkedRecordType;
	}

	public static RecordLink withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(String id,
			String nameInData, String textId, String defTextId, String linkedRecordType) {
		return new RecordLink(id, nameInData, textId, defTextId, linkedRecordType);
	}

	public String getLinkedRecordType() {
		return linkedRecordType;
	}

	public void setLinkedPath(DataGroup linkedPath) {
		this.linkedPath = linkedPath;
	}

	public DataGroup getLinkedPath() {
		return linkedPath;
	}

	public void setRefParentId(String refParentId) {
		this.refParentId = refParentId;
	}

	public String getRefParentId() {
		return refParentId;
	}

	public String getFinalValue() {
		return finalValue;
	}

	public void setFinalValue(String finalValue) {
		this.finalValue = finalValue;
	}

	@Override
	public List<String> getAttributeReferences() {
		return attributeReferences;
	}

	public void addAttributeReference(String attributeReferenceId) {
		attributeReferences.add(attributeReferenceId);

	}
}
