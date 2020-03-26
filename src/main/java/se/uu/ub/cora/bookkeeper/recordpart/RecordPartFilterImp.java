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

import java.util.List;
import java.util.Set;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterImp implements RecordPartFilter {
 
	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(DataGroup dataGroup,
			Set<String> constraints, Set<String> permissions) {
		for (String constraint : constraints) {
			possiblyRemoveChildIfNoPermission(dataGroup, constraint, permissions);
		}
		return dataGroup;
	}

	private void possiblyRemoveChildIfNoPermission(DataGroup dataGroup, String constraint,
			Set<String> permissions) {
		if (noPermissionAndChildExists(dataGroup, constraint, permissions)) {
			dataGroup.removeAllChildrenWithNameInData(constraint);
		}
	}

	private boolean noPermissionAndChildExists(DataGroup dataGroup, String constraint,
			Set<String> permissions) {
		return noPermissionExist(permissions, constraint)
				&& dataGroup.containsChildWithNameInData(constraint);
	}

	private boolean noPermissionExist(Set<String> permissions, String constraint) {
		return !permissions.contains(constraint);
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(DataGroup originalDataGroup,
			DataGroup updatedDataGroup, Set<String> constraints, Set<String> permissions) {
		for (String constraint : constraints) {
			possiblyReplaceChildIfNoPermission(originalDataGroup, updatedDataGroup, permissions,
					constraint);
		}
		return updatedDataGroup;
	}

	private void possiblyReplaceChildIfNoPermission(DataGroup originalDataGroup,
			DataGroup updatedDataGroup, Set<String> permissions, String constraint) {
		if (noPermissionAndChildExists(updatedDataGroup, constraint, permissions)) {
			replaceChild(originalDataGroup, updatedDataGroup, constraint);
		}
	}

	private void replaceChild(DataGroup originalDataGroup, DataGroup updatedDataGroup,
			String constraint) {
		updatedDataGroup.removeAllChildrenWithNameInData(constraint);
		List<DataElement> allChildren = originalDataGroup.getAllChildrenWithNameInData(constraint);
		updatedDataGroup.addChildren(allChildren);
	}
}
