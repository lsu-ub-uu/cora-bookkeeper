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

import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RecordListTest {
	@Test
	public void testInit() {
		String containsRecordTypes = "metadata";
		RecordList recordList = RecordList.withContainRecordsOfType(containsRecordTypes);
		assertEquals(recordList.getContainRecordsOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		DataRecord record = new DataRecord();
		recordList.addRecord(record);
		List<DataRecord> records = recordList.getRecords();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testTotalNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setTotalNo("2");
		assertEquals(recordList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setFromNo("0");
		assertEquals(recordList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		RecordList recordList = RecordList.withContainRecordsOfType("metadata");
		recordList.setToNo("2");
		assertEquals(recordList.getToNo(), "2");
	}
}
