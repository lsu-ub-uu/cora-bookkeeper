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
package se.uu.ub.cora.bookkeeper.recordpart;

import java.util.Set;

import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterImp implements RecordPartFilter {

	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(DataGroup dataGroup,
			Set<String> recordPartConstraints, Set<String> recordPartReadPermissions) {
		for (String constraint : recordPartConstraints) {
			removeChildIfNoPermission(dataGroup, constraint, recordPartReadPermissions);
		}
		return dataGroup;
	}

	private void removeChildIfNoPermission(DataGroup dataGroup, String constraint,
			Set<String> recordPartReadPermissions) {
		if (noPermissionAndChildExists(dataGroup, constraint, recordPartReadPermissions)) {
			dataGroup.removeAllChildrenWithNameInData(constraint);
		}
	}

	private boolean noPermissionAndChildExists(DataGroup dataGroup, String constraint,
			Set<String> recordPartReadPermissions) {
		return noPermissionExist(recordPartReadPermissions, constraint)
				&& dataGroup.containsChildWithNameInData(constraint);
	}

	private boolean noPermissionExist(Set<String> recordPartReadPermissions, String constraint) {
		return !recordPartReadPermissions.contains(constraint);
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(DataGroup originalDataGroup,
			DataGroup updatedDataGroup, Set<String> recordPartConstraints,
			Set<String> recordPartPermissions) {

		for (String constraint : recordPartConstraints) {
			if (!recordPartPermissions.contains(constraint)) {

				if (updatedDataGroup.containsChildWithNameInData(constraint)) {
					updatedDataGroup.removeAllChildrenWithNameInData(constraint);
					updatedDataGroup.addChildren(
							originalDataGroup.getAllChildrenWithNameInData(constraint));
					// originalDataGroup.getAllChildrenWithNameInData(constraint);
					/// TODO: what happens if we do not have children in original? is an error
					// thrown? what does the interface say?
				}
			}
		}
		// }
		return updatedDataGroup;
	}

}
