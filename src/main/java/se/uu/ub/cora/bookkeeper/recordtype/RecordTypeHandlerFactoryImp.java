/*
 * Copyright 2020, 2025, 2026 Uppsala University Library
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.bookkeeper.recordtype;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.recordtype.internal.RecordTypeHandlerImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class RecordTypeHandlerFactoryImp implements RecordTypeHandlerFactory {

	@Override
	public RecordTypeHandler factorUsingRecordTypeId(String recordTypeId) {
		RecordStorage recordStorage = RecordStorageProvider.getRecordStorage();
		MetadataStorageView metadataStorage = MetadataStorageProvider.getStorageView();
		RecordType recordType = metadataStorage.getRecordType(recordTypeId);

		return RecordTypeHandlerImp.usingRecordStorage(recordType, recordStorage);
	}

	@Override
	public RecordTypeHandler factorUsingDataRecordGroup(DataRecordGroup dataRecordGroup) {
		try {
			return tryToFactorUsingDataRecordGroup(dataRecordGroup);
		} catch (se.uu.ub.cora.data.DataMissingException e) {
			throw new DataMissingException(
					"RecordTypeHandler could not be created because of missing data: "
							+ e.getMessage());
		}
	}

	private RecordTypeHandler tryToFactorUsingDataRecordGroup(DataRecordGroup dataRecordGroup) {
		RecordStorage recordStorage = RecordStorageProvider.getRecordStorage();
		MetadataStorageView metadataStorage = MetadataStorageProvider.getStorageView();
		RecordType recordType = metadataStorage.getRecordType(dataRecordGroup.getType());

		return RecordTypeHandlerImp.usingHandlerFactoryRecordStorageValidationTypeId(recordType,
				recordStorage, dataRecordGroup.getValidationType());
	}
}