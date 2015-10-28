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

package se.uu.ub.cora.bookkeeper.data;

public final class DataRecordLink implements DataElement {
	private String nameInData;
	private String linkedRecordType;
	private String linkedRecordId;
	private String repeatId;
	private String linkedRepeatId;
	private DataGroup linkedPath;

	public static DataRecordLink withNameInDataAndLinkedRecordTypeAndLinkedRecordId(String nameInData,
																					String linkedRecordType, String linkedRecordId) {
		return new DataRecordLink(nameInData, linkedRecordType, linkedRecordId);
	}

	private DataRecordLink(String nameInData, String linkedRecordType, String linkedRecordId) {
		this.nameInData = nameInData;
		this.linkedRecordType = linkedRecordType;
		this.linkedRecordId = linkedRecordId;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getLinkedRecordType() {
		return linkedRecordType;
	}

	public String getLinkedRecordId() {
		return linkedRecordId;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	public String getRepeatId() {
		return repeatId;
	}

	public void setLinkedRepeatId(String linkedRepeatId) {
		this.linkedRepeatId = linkedRepeatId;
	}

	public String getLinkedRepeatId() {
		return linkedRepeatId;
	}

	public void setLinkedPath(DataGroup linkedPath) {
		this.linkedPath = linkedPath;

	}

	public DataGroup getLinkedPath() {
		return linkedPath;
	}

}
