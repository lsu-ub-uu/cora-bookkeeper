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
package se.uu.ub.cora.bookkeeper.metadata;

import java.util.Arrays;

/**
 * Constraints also known as restrictions are set in metadata as a relation between a data group,
 * and its children. There are two types of restrictions, read and write. A read restriction
 * prevents data from leaving the server and a write restriction prevents data from beeing updated
 * on the server.<br>
 * <br>
 * A set read restriction implies a write restriction on the same record part, because a user is not
 * allowed to change information that it can not see.
 */
public enum ConstraintType {
	/**
	 * Write constraints prevents a data part from beeing updated on the server.
	 */
	WRITE("write"),
	/**
	 * Read constratins preventes a data part from leaving the server. A set read restriction
	 * implies a write restriction on the same record part, because a user is not allowed to change
	 * information that it can not see.
	 */
	READ_WRITE("readWrite");

	public final String nameInData;

	ConstraintType(String nameInData) {
		this.nameInData = nameInData;
	}

	/**
	 * fromString is used to get a ConstraintType from the name (value) used in data
	 * 
	 * @param nameInData,
	 *            A string with how the ConstraintType is represented in data
	 * @return A ConstraintType matching the nameInData
	 */
	public static ConstraintType fromString(String nameInData) {
		return Arrays.stream(ConstraintType.values()).filter(v -> v.nameInData.equals(nameInData))
				.findFirst().orElse(null);
	}
}
