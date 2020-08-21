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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorImp implements DataRedactor {

	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(DataGroup dataGroup,
			Set<Constraint> constraints, Set<String> permissions) {
		for (Constraint constraint : constraints) {
			possiblyRemoveChildIfNoPermission(dataGroup, constraint, permissions);
		}
		return dataGroup;
	}

	private void possiblyRemoveChildIfNoPermission(DataGroup dataGroup, Constraint constraint,
			Set<String> permissions) {
		if (noPermissionExist(permissions, constraint.getNameInData())) {
			removeMatchingChildren(dataGroup, constraint);
		}
	}

	private DataAttribute[] getAttributesAsArray(Constraint constraint) {
		List<DataAttribute> attributes = new ArrayList<>(constraint.getDataAttributes());
		return attributes.stream().toArray(DataAttribute[]::new);
	}

	private boolean noPermissionExist(Set<String> permissions, String constraint) {
		return !permissions.contains(constraint);
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(DataGroup originalDataGroup,
			DataGroup updatedDataGroup, Set<Constraint> constraints, Set<String> permissions) {
		for (Constraint constraint : constraints) {
			possiblyReplaceChildIfNoPermission(originalDataGroup, updatedDataGroup, permissions,
					constraint);
		}
		return updatedDataGroup;
	}

	private void possiblyReplaceChildIfNoPermission(DataGroup originalDataGroup,
			DataGroup updatedDataGroup, Set<String> permissions, Constraint constraint) {
		if (noPermissionExist(permissions, constraint.getNameInData())) {
			replaceChild(originalDataGroup, updatedDataGroup, constraint);
		}
	}

	private void replaceChild(DataGroup originalDataGroup, DataGroup updatedDataGroup,
			Constraint constraint) {
		removeMatchingChildren(updatedDataGroup, constraint);
		List<DataElement> allChildren = originalDataGroup
				.getAllChildrenWithNameInData(constraint.getNameInData());
		updatedDataGroup.addChildren(allChildren);
	}

	private void removeMatchingChildren(DataGroup updatedDataGroup, Constraint constraint) {
		DataAttribute[] attributeArray = getAttributesAsArray(constraint);
		updatedDataGroup.removeAllChildrenWithNameInDataAndAttributes(constraint.getNameInData(),
				attributeArray);
	}
}
