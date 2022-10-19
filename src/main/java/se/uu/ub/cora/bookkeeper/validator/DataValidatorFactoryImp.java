/*
 * Copyright 2021 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.validator;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

public class DataValidatorFactoryImp implements DataValidatorFactory {

	@Override
	public DataValidator factor(MetadataStorageView metadataStorage,
			Map<String, DataGroup> recordTypeHolder, MetadataHolder metadataHolder) {
		DataElementValidatorFactory dataElementValidatorFactory = new DataElementValidatorFactoryImp(
				recordTypeHolder, metadataHolder);
		return new DataValidatorImp(metadataStorage, dataElementValidatorFactory, recordTypeHolder);
	}

}
