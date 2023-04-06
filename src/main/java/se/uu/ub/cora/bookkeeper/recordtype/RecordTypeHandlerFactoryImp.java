/*
 * Copyright 2020 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.recordtype.internal.RecordTypeHandlerImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class RecordTypeHandlerFactoryImp implements RecordTypeHandlerFactory {
	private RecordStorage recordStorage;
	private MetadataStorageView metadataStorage;

	@Override
	public RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup) {
		ensureRecordStorageIsFetched();

		return RecordTypeHandlerImp.usingRecordStorageAndDataGroup(this, recordStorage, dataGroup);
	}

	@Override
	public RecordTypeHandler factorUsingRecordTypeId(String recordTypeId) {
		ensureRecordStorageIsFetched();

		return RecordTypeHandlerImp.usingRecordStorageAndRecordTypeId(this, recordStorage,
				recordTypeId);
	}

	private void ensureRecordStorageIsFetched() {
		if (recordStorage == null) {
			recordStorage = RecordStorageProvider.getRecordStorage();
		}
	}

	@Override
	public RecordTypeHandler factorUsingDataRecordGroup(DataRecordGroup dataRecordGroup) {
		ensureRecordStorageIsFetched();
		ensureMetadataStorageIsFetched();

		return RecordTypeHandlerImp.usingHandlerFactoryRecordStorageMetadataStorageValidationTypeId(
				this, recordStorage, metadataStorage, dataRecordGroup.getValidationType());
	}

	private void ensureMetadataStorageIsFetched() {
		if (metadataStorage == null) {
			metadataStorage = MetadataStorageProvider.getStorageView();
		}
	}

}
