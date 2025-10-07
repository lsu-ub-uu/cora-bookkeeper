/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordtype.internal;

public interface IdSourceFactory {

	/**
	 * Creates an IdSource that generates ids based on the current timestamp and the type.
	 * 
	 * @param string
	 *            The type of the record type. The type will be part of the generated id in
	 *            {@link IdSource}
	 * 
	 * @return An IdSource that generates ids based on the current timestamp and type.
	 */
	IdSource factorTimestampIdSource(String type);

}
