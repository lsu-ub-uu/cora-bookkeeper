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

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DataRecordTest {
	private DataRecord dataRecord;

	@BeforeMethod
	public void beforeMethod() {
		dataRecord = new DataRecord();
	}

	@Test
	public void testKeys() {
		dataRecord.addKey("KEY");
		assertTrue(dataRecord.containsKey("KEY"));
	}

	@Test
	public void testDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataRecord.setDataGroup(dataGroup);
		assertEquals(dataRecord.getDataGroup(), dataGroup);
	}

	@Test
	public void testGetKeys() {
		dataRecord.addKey("KEY1");
		dataRecord.addKey("KEY2");
		Set<String> keys = dataRecord.getKeys();
		assertTrue(keys.contains("KEY1"));
		assertTrue(keys.contains("KEY2"));
	}

}
