/*
 * Copyright 2015, 2018, 2019 Uppsala University Library
 * Copyright 2025 Olov McKie
 * 
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import se.uu.ub.cora.data.DataRecordGroup;

public final class DataToMetadataConverterFactoryImp implements DataToMetadataConverterFactory {

	public static DataToMetadataConverterFactoryImp forDataGroups() {
		return new DataToMetadataConverterFactoryImp();
	}

	@Override
	public DataToMetadataConverter factorForDataContainingMetadata(DataRecordGroup data) {
		if ("metadata".equals(data.getNameInData())) {
			return createConverterBasedOnMetadataType(data);
		}
		throw DataConversionException.withMessage(
				"No converter found for DataRecordGroup with nameInData: " + data.getNameInData());
	}

	private DataToMetadataConverter createConverterBasedOnMetadataType(DataRecordGroup data) {
		String type = data.getType();
		if ("group".equals(type)) {
			return DataToMetadataGroupConverter.fromDataRecordGroup(data);
		}
		if ("collectionItem".equals(type)) {
			return DataToCollectionItemConverter.fromDataRecordGroup(data);
		}
		if ("collectionVariable".equals(type)) {
			return DataToCollectionVariableConverter.fromDataRecordGroup(data);
		}
		if ("itemCollection".equals(type)) {
			return DataToItemCollectionConverter.fromDataRecordGroup(data);
		}
		if ("textVariable".equals(type)) {
			return DataToTextVariableConverter.fromDataRecordGroup(data);
		}
		if ("numberVariable".equals(type)) {
			return DataToNumberVariableConverter.fromDataRecordGroup(data);
		}
		if ("recordLink".equals(type)) {
			return DataToRecordLinkConverter.fromDataRecordGroup(data);
		}
		if ("resourceLink".equals(type)) {
			return DataToResourceLinkConverter.fromDataRecordGroup(data);
		}
		throw DataConversionException
				.withMessage("No converter found for DataRecordGroup with type: " + type);
	}
}
