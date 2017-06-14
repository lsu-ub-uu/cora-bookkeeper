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

import se.uu.ub.cora.bookkeeper.data.DataAttribute;
import se.uu.ub.cora.bookkeeper.data.DataPart;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public final class JsonToDataAttributeConverter implements JsonToDataConverter {

	private JsonObject jsonObject;

	static JsonToDataAttributeConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataAttributeConverter(jsonObject);
	}

	private JsonToDataAttributeConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataPart toInstance() {
		try {
			return tryToInstantiate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
		}
	}

	private DataPart tryToInstantiate() {
		validateJsonData();
		String nameInData = getNameInDataFromJsonObject();
		JsonString value = (JsonString) jsonObject.getValue(nameInData);
		return DataAttribute.withNameInDataAndValue(nameInData, value.getStringValue());
	}

	private String getNameInDataFromJsonObject() {
		return jsonObject.keySet().iterator().next();
	}

	private void validateJsonData() {
		validateOnlyOneKeyValuePairAtTopLevel();
		validateNameInDataValueIsString();
	}

	private void validateOnlyOneKeyValuePairAtTopLevel() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Attribute data can only contain one key value pair");
		}
	}

	private void validateNameInDataValueIsString() {
		String nameInData = getNameInDataFromJsonObject();
		JsonValue value = jsonObject.getValue(nameInData);
		if (!(value instanceof JsonString)) {
			throw new JsonParseException(
					"Value of attribute data \"" + nameInData + "\" must be a String");
		}
	}

}
