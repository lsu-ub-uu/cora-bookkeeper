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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;

public final class JsonToDataAtomicConverter implements JsonToDataConverter {
	private static final String REPEAT_ID = "repeatId";
	private static final int ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL = 3;
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private JsonObject jsonObject;

	static JsonToDataAtomicConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataAtomicConverter(jsonObject);
	}

	private JsonToDataAtomicConverter(JsonObject jsonObject) {
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
		return convertJsonToDataAtomic();
	}

	private void validateJsonData() {
		validateNameInData();
		validateValue();
		validateRepeatId();
		validateNoExtraElements();
	}

	private void validateNameInData() {
		if (keyMissingOrNotStringValueInJsonObject(NAME)) {
			throw new JsonParseException("Value of atomic data name must contain a String");
		}
	}

	private boolean keyMissingOrNotStringValueInJsonObject(String key) {
		return !jsonObject.containsKey(key) || !(jsonObject.getValue(key) instanceof JsonString);
	}

	private void validateValue() {
		if (keyMissingOrNotStringValueInJsonObject(VALUE)) {
			throw new JsonParseException("Value of atomic data value must contain a String");
		}
	}

	private void validateRepeatId() {
		if (jsonObject.size() == ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL
				&& keyMissingOrNotStringValueInJsonObject(REPEAT_ID)) {
			throw new JsonParseException(
					"Atomic data can only contain string value for name, value and repeatId");
		}
	}

	private void validateNoExtraElements() {
		if (jsonObject.size() > ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL) {
			throw new JsonParseException("Atomic data can only contain name, value and repeatId");
		}
	}

	private DataAtomic convertJsonToDataAtomic() {
		DataAtomic dataAtomic = createFromJsonWithNameInDataAndValue();
		addRepeatIdFromJson(dataAtomic);
		return dataAtomic;
	}

	private DataAtomic createFromJsonWithNameInDataAndValue() {
		String nameInData = getStringFromJson(NAME);
		String value = getStringFromJson(VALUE);
		return DataAtomic.withNameInDataAndValue(nameInData, value);
	}

	private String getStringFromJson(String key) {
		return jsonObject.getValueAsJsonString(key).getStringValue();
	}

	private void addRepeatIdFromJson(DataAtomic dataAtomic) {
		if (jsonObject.containsKey(REPEAT_ID)) {
			dataAtomic.setRepeatId(jsonObject.getValueAsJsonString(REPEAT_ID).getStringValue());
		}
	}
}
