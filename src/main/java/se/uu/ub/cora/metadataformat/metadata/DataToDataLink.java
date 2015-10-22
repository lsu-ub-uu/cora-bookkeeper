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

import se.uu.ub.cora.metadataformat.data.DataGroup;

public final class DataToDataLink extends MetadataElement {
	private String targetRecordType;
	private DataGroup linkedPath;

	public static DataToDataLink withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType(
			String id, String nameInData, String textId, String defTextId,
			String targetRecordType) {
		return new DataToDataLink(id, nameInData, textId, defTextId, targetRecordType);
	}

	private DataToDataLink(String id, String nameInData, String textId, String defTextId,
			String targetRecordType) {
		super(id, nameInData, textId, defTextId);
		this.targetRecordType = targetRecordType;
	}

	public String getTargetRecordType() {
		return targetRecordType;
	}

	public void setLinkedPath(DataGroup linkedPath) {
		this.linkedPath = linkedPath;
	}

	public DataGroup getLinkedPath() {
		return linkedPath;
	}

}
