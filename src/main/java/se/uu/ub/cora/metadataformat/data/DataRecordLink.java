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

package se.uu.ub.cora.metadataformat.data;

public final class DataRecordLink implements DataElement {
	private String nameInData;
	private String recordType;
	private String recordId;
	private String repeatId;
	private String linkedRepeatId;

	public static DataRecordLink withNameInDataAndRecordTypeAndRecordId(String nameInData,
			String recordType, String recordId) {
		return new DataRecordLink(nameInData, recordType, recordId);
	}

	private DataRecordLink(String nameInData, String recordType, String recordId) {
		this.nameInData = nameInData;
		this.recordType = recordType;
		this.recordId = recordId;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getRecordType() {
		return recordType;
	}

	public String getRecordId() {
		return recordId;
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

}
