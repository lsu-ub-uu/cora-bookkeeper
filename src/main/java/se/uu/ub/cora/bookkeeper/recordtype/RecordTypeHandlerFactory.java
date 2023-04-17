/*
 * Copyright 2020, 2023 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;

/**
 * RecordTypeHandlerFactory is a factor class that creates new RecordTypeHandlers.
 */
public interface RecordTypeHandlerFactory {
	/**
	 * 
	 * @param dataGroup
	 * @Deprecated use {@link #factorUsingDataRecordGroup(DataRecordGroup)} instead
	 */
	@Deprecated(forRemoval = true)
	RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup);

	/**
	 * factorUsingRecordTypeId method factors a RecordTypeHandler using recordTypeId.
	 * 
	 * @param recordTypeId
	 *            is an String with the id of the recordtype that the handler is created with
	 * @Deprecated use {@link #factorUsingDataRecordGroup(DataRecordGroup)} instead
	 */
	@Deprecated(forRemoval = true)
	RecordTypeHandler factorUsingRecordTypeId(String recordTypeId);

	/**
	 * factorUsingDataRecordGroup creates a recordTypeHandler based on the information in the
	 * provided DataRecordGroup, that holds data about the record currently beeing handled.
	 * 
	 * @param dataRecordGroup
	 *            is a DataRecordGroup that holds information about the record currently beeing
	 *            handled
	 * @return A {@link RecordTypeHandler} for the record beeing handleds recordType
	 */
	RecordTypeHandler factorUsingDataRecordGroup(DataRecordGroup dataRecordGroup);
}
