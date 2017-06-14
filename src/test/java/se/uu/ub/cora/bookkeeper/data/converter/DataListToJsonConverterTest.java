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

package se.uu.ub.cora.bookkeeper.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataList;
import se.uu.ub.cora.bookkeeper.data.DataRecord;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataListToJsonConverterTest {
	@Test
	public void testToJson() {
		DataList dataList = DataList.withContainDataOfType("place");
		DataGroup dataGroup = DataGroup.withNameInData("groupId");
		DataRecord dataRecord = DataRecord.withDataGroup(dataGroup);
		dataList.addData(dataRecord);
		dataList.setTotalNo("1");
		dataList.setFromNo("0");
		dataList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataListToJsonConverter recordListToJsonConverter = DataListToJsonConverter.usingJsonFactoryForDataList(jsonFactory, dataList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString,
				"{\"dataList\":{\"fromNo\":\"0\",\""
						+ "data\":[{\"record\":{\"data\":{\"name\":\"groupId\"}}}],"
						+ "\"totalNo\":\"1\",\"containDataOfType\":\"place\",\"toNo\":\"1\"}}");
	}

	@Test
	public void testToJsonWithGroup() {
		DataList dataList = DataList.withContainDataOfType("place");
		DataGroup dataGroup = DataGroup.withNameInData("groupId");
		dataList.addData(dataGroup);
		dataList.setTotalNo("1");
		dataList.setFromNo("0");
		dataList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataListToJsonConverter recordListToJsonConverter = DataListToJsonConverter.usingJsonFactoryForDataList(jsonFactory, dataList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString,
				"{\"dataList\":{\"fromNo\":\"0\",\"" + "data\":[{\"name\":\"groupId\"}],"
						+ "\"totalNo\":\"1\",\"containDataOfType\":\"place\",\"toNo\":\"1\"}}");
	}
}
