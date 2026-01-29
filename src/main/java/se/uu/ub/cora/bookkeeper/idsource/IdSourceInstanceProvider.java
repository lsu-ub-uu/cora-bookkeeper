/*
 * Copyright 2026 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.idsource;

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.initialize.SelectType;

public interface IdSourceInstanceProvider extends SelectType {

	/**
	 * getIdSource should be implemented in such a way that it returns an IdSource of the requested
	 * type.
	 * 
	 * @param recordType
	 *            A {@link RecordType} record containing information information of a record type
	 *            which is required in order to create the IdSources
	 * 
	 * @return An IdSource of the requested type.
	 */
	IdSource getIdSource(RecordType recordType);

}
