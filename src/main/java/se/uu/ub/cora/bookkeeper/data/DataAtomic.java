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

public final class DataAtomic implements DataElement {

	private String nameInData;
	private String value;
	private String repeatId;

	public static DataAtomic withNameInDataAndValue(String nameInData, String value) {
		return new DataAtomic(nameInData, value);
	}

	public static DataAtomic withNameInDataAndValueAndRepeatId(String nameInData, String value, String repeatId)
	{
		return new DataAtomic(nameInData, value, repeatId);
	}

	private DataAtomic(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	private DataAtomic(String nameInData, String value, String repeatId) {
		this.nameInData = nameInData;
		this.value = value;
		this.repeatId = repeatId;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public String getValue() {
		return value;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	public String getRepeatId() {
		return repeatId;
	}

}
