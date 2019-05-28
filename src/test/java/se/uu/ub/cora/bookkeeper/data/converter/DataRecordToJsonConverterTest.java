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

package se.uu.ub.cora.bookkeeper.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataRecordToJsonConverterTest {
	@Test
	public void testToJson() {
		DataGroup dataGroup = DataGroup.withNameInData("groupNameInData");
		DataRecord dataRecord = DataRecord.withDataGroup(dataGroup);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForDataRecord(jsonFactory, dataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}}}");
	}

	@Test
	public void testToJsonWithKey() {
		DataGroup dataGroup = DataGroup.withNameInData("groupNameInData");
		DataRecord dataRecord = DataRecord.withDataGroup(dataGroup);
		dataRecord.addKey("KEY1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForDataRecord(jsonFactory, dataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\"]" + "}}");
	}

	@Test
	public void testToJsonWithKeys() {
		DataGroup dataGroup = DataGroup.withNameInData("groupNameInData");
		DataRecord dataRecord = DataRecord.withDataGroup(dataGroup);
		dataRecord.addKey("KEY1");
		dataRecord.addKey("KEY2");
		dataRecord.addKey("KEY3");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForDataRecord(jsonFactory, dataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\",\"KEY2\",\"KEY3\"]" + "}}");
	}

}
