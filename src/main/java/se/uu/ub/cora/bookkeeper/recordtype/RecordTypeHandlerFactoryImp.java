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
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class RecordTypeHandlerFactoryImp implements RecordTypeHandlerFactory {
	private RecordStorage recordStorage;

	@Override
	public RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup) {
		if (recordStorage == null) {
			recordStorage = RecordStorageProvider.getRecordStorage();
		}

		return RecordTypeHandlerImp.usingRecordStorageAndDataGroup(this, recordStorage, dataGroup);
	}

	@Override
	public RecordTypeHandler factorUsingRecordTypeId(String recordTypeId) {
		if (recordStorage == null) {
			recordStorage = RecordStorageProvider.getRecordStorage();
		}

		return RecordTypeHandlerImp.usingRecordStorageAndRecordTypeId(this, recordStorage,
				recordTypeId);
	}

}
