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

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public final class DataListToJsonConverter {

	public static DataListToJsonConverter usingJsonFactoryForDataList(
			JsonBuilderFactory jsonFactory, DataList restRecordList) {
		return new DataListToJsonConverter(jsonFactory, restRecordList);
	}

	private JsonBuilderFactory jsonBuilderFactory;
	private DataList restRecordList;
	private JsonObjectBuilder recordListJsonObjectBuilder;

	private DataListToJsonConverter(JsonBuilderFactory jsonFactory, DataList restRecordList) {
		this.jsonBuilderFactory = jsonFactory;
		this.restRecordList = restRecordList;
		recordListJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {

		recordListJsonObjectBuilder.addKeyString("totalNo",
				restRecordList.getTotalNumberOfTypeInStorage());
		recordListJsonObjectBuilder.addKeyString("fromNo", restRecordList.getFromNo());
		recordListJsonObjectBuilder.addKeyString("toNo", restRecordList.getToNo());
		recordListJsonObjectBuilder.addKeyString("containDataOfType",
				restRecordList.getContainDataOfType());

		JsonArrayBuilder recordsJsonBuilder = jsonBuilderFactory.createArrayBuilder();

		for (Data data : restRecordList.getDataList()) {
			convertToJsonBuilder(recordsJsonBuilder, data);
		}

		recordListJsonObjectBuilder.addKeyJsonArrayBuilder("data", recordsJsonBuilder);

		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("dataList",
				recordListJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	private void convertToJsonBuilder(JsonArrayBuilder recordsJsonBuilder, Data data) {
		if (data instanceof DataRecord) {
			convertRecordToJsonBuilder(recordsJsonBuilder, data);
		} else {
			convertGroupToJsonBuilder(recordsJsonBuilder, data);
		}
	}

	private void convertRecordToJsonBuilder(JsonArrayBuilder recordsJsonBuilder, Data data) {
		DataRecordToJsonConverter converter = DataRecordToJsonConverter
				.usingJsonFactoryForDataRecord(jsonBuilderFactory, (DataRecord) data);
		recordsJsonBuilder.addJsonObjectBuilder(converter.toJsonObjectBuilder());
	}

	private void convertGroupToJsonBuilder(JsonArrayBuilder recordsJsonBuilder, Data data) {
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForDataGroup(jsonBuilderFactory, (DataGroup) data);
		recordsJsonBuilder.addJsonObjectBuilder(converter.toJsonObjectBuilder());
	}

}
