/*
 * Copyright 2015, 2018 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.Data;

public final class DataList {
	private String containDataOfType;
	private List<Data> list = new ArrayList<>();
	private String totalNo;
	private String fromNo;
	private String toNo;

	public static DataList withContainDataOfType(String containsDataOfType) {
		return new DataList(containsDataOfType);
	}

	private DataList(String containDataOfType) {
		this.containDataOfType = containDataOfType;
	}

	public String getContainDataOfType() {
		return containDataOfType;
	}

	public void addData(Data data) {
		list.add(data);
	}

	public List<Data> getDataList() {
		return list;
	}

	public void setTotalNo(String totalNo) {
		this.totalNo = totalNo;
	}

	public String getTotalNo() {
		return totalNo;
	}

	public void setFromNo(String fromNo) {
		this.fromNo = fromNo;

	}

	public String getFromNo() {
		return fromNo;
	}

	public void setToNo(String toNo) {
		this.toNo = toNo;

	}

	public String getToNo() {
		return toNo;
	}

}
