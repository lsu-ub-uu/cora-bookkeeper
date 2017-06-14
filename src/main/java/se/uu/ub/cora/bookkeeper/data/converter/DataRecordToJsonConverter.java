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

import se.uu.ub.cora.bookkeeper.data.DataRecord;
import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public final class DataRecordToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private DataRecord dataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;

	public static DataRecordToJsonConverter usingJsonFactoryForDataRecord(JsonBuilderFactory jsonFactory,
			DataRecord dataRecord) {
		return new DataRecordToJsonConverter(jsonFactory, dataRecord);
	}

	private DataRecordToJsonConverter(JsonBuilderFactory jsonFactory, DataRecord dataRecord) {
		this.jsonBuilderFactory = jsonFactory;
		this.dataRecord = dataRecord;
		recordJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {
		convertMainDataGroup();
		convertKeys();
		return createTopLevelJsonObjectWithRecordAsChild();
	}

	private void convertMainDataGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory.createForDataElement(jsonBuilderFactory,
				dataRecord.getDataGroup());
		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);
	}

	private void convertKeys() {
		if (recordHasKeys()) {
			addKeysToRecord();
		}
	}

	private boolean recordHasKeys() {
		return !dataRecord.getKeys().isEmpty();
	}

	private void addKeysToRecord() {
		JsonArrayBuilder keyBuilder = jsonBuilderFactory.createArrayBuilder();
		for (String key : dataRecord.getKeys()) {
			keyBuilder.addString(key);
		}
		recordJsonObjectBuilder.addKeyJsonArrayBuilder("keys", keyBuilder);
	}

	private JsonObjectBuilder createTopLevelJsonObjectWithRecordAsChild() {
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("record", recordJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

}
