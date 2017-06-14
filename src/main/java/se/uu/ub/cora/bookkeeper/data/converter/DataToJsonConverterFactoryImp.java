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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataAttribute;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataPart;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {

	@Override
	public DataToJsonConverter createForDataElement(JsonBuilderFactory factory, DataPart dataPart) {

		if (dataPart instanceof DataGroup) {
			return DataGroupToJsonConverter.usingJsonFactoryForDataGroup(factory,
					(DataGroup) dataPart);
		}
		if (dataPart instanceof DataAtomic) {
			return DataAtomicToJsonConverter.usingJsonFactoryForDataAtomic(factory,
					(DataAtomic) dataPart);
		}
		return DataAttributeToJsonConverter.usingJsonFactoryForDataAttribute(factory,
				(DataAttribute) dataPart);
	}
}
