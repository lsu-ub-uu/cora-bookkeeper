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

package se.uu.ub.cora.bookkeeper.termcollector;

import java.util.List;

import se.uu.ub.cora.data.collected.PermissionTerm;

public interface PermissionTermDataHandler {
	/**
	 * Returns a list of PermissionTerm values where any element in
	 * {@code currentPermissions} that has mode STATE is replaced by the corresponding
	 * element from {@code previousPermissions}.
	 *
	 * This method does not modify the input lists.
	 *
	 * @param previousPermissions
	 *            a list of {@link PermissionTerm} representing the previously stored
	 *            record.
	 * @param currentPermissions
	 *            a list of {@link PermissionTerm} representing the incoming update
	 *            for the record.
	 * @return a new list of {@link PermissionTerm} with elements from
	 *         {@code currentPermissions}, but with any elements having mode STATE
	 *         replaced by the corresponding element from {@code previousPermissions}
	 *         when available.
	 */
	List<PermissionTerm> getMixedPermissionTermValuesConsideringModeState(
			List<PermissionTerm> previousPermissions, List<PermissionTerm> currentPermissions);

}