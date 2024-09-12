/*
 * Copyright 2020, 2024 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataRecordGroup;

/**
 * DataRedactor defines methods needed to hide or remove confidential parts of a DataGroup before
 * storing or returning it.
 */
public interface DataRedactor {
	/**
	 * removeChildrenForConstraintsWithoutPermissions is used to remove children from the entered
	 * DataGroup, based on the set of entered contraints that is not matched by the entered
	 * permissions. The set of constraints consists of the nameInData for the children that has
	 * constraints, so children are removed based on their nameInData. If an empty set of
	 * constraints is entered should no children be removed.
	 * 
	 * @param metadataId
	 * 
	 * @param dataRecordGroup
	 *            DataRecordGroup to remove children from
	 * @param recordPartConstraints
	 *            Set of constraints, containing nameInData, to restrict access to
	 * @param recordPartPermissions
	 *            Set of permissions for the dataGroup.
	 * @return DataGroup which might have had children removed
	 */
	DataRecordGroup removeChildrenForConstraintsWithoutPermissions(String metadataId,
			DataRecordGroup dataRecordGroup, Set<Constraint> recordPartConstraints,
			Set<String> recordPartPermissions);

	/**
	 * replaceChildrenForConstraintsWithoutPermissions is used to replace children with the original
	 * value of the children. The children will be replaced ONLY when the the permission does not
	 * match the constraints. The set of constraints consists of the nameInData for the children
	 * that has constraints, so children are replaced based on their nameInData. If an empty set of
	 * constraints is entered should no children be removed.
	 * 
	 * @param metadataId
	 * 
	 * @param originalDataRecordGroup
	 *            Is the current version of the datagroup, replaced data is copied from this group.
	 * @param changedDataRecordGroup
	 *            Is the datagGroup that might be changed by this method.
	 * @param recordPartConstraints
	 *            Set of constraints valid for the dataGroupType.
	 * @param recordPartPermissions
	 *            Set of permissions for the dataGroupType.
	 * @return DataRecordGroup which might have had children replaced
	 */
	DataRecordGroup replaceChildrenForConstraintsWithoutPermissions(String metadataId,
			DataRecordGroup originalDataRecordGroup, DataRecordGroup changedDataRecordGroup,
			Set<Constraint> recordPartConstraints, Set<String> recordPartPermissions);

}
