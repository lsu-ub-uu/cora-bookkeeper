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

package se.uu.ub.cora.bookkeeper.data;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;

public class DataListTest {
	@Test
	public void testInit() {
		String containsDataOfType = "metadata";
		DataList dataList = DataList.withContainDataOfType(containsDataOfType);
		assertEquals(dataList.getContainDataOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		DataList dataList = DataList.withContainDataOfType("metadata");
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataRecord record = DataRecord.withDataGroup(dataGroup);
		dataList.addData(record);
		List<Data> records = dataList.getDataList();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testAddGroup() {
		DataList dataList = DataList.withContainDataOfType("someGroup");
		DataGroup group = DataGroup.withNameInData("nameInData");
		dataList.addData(group);
		List<Data> groups = dataList.getDataList();
		assertEquals(groups.get(0), group);
	}

	@Test
	public void testTotalNo() {
		DataList dataList = DataList.withContainDataOfType("metadata");
		dataList.setTotalNo("2");
		assertEquals(dataList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		DataList dataList = DataList.withContainDataOfType("metadata");
		dataList.setFromNo("0");
		assertEquals(dataList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		DataList dataList = DataList.withContainDataOfType("metadata");
		dataList.setToNo("2");
		assertEquals(dataList.getToNo(), "2");
	}
}
