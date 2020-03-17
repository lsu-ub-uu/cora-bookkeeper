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
import java.util.Map;

import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterImp implements RecordPartFilter {

	@Override

	public DataGroup filterReadRecordPartsUsingPermissions(DataGroup dataGroup,
			Map<String, String> recordPartConstraints, List<String> recordPartReadPermissions) {

		for (String key : recordPartConstraints.keySet()) {
			removeChildIfNoPermission(dataGroup, key, recordPartReadPermissions);
		}

		return dataGroup;
	}

	private void removeChildIfNoPermission(DataGroup dataGroup, String key,
			List<String> recordPartReadPermissions) {
		if (noPermissionExist(recordPartReadPermissions, key)) {
			dataGroup.removeFirstChildWithNameInData(key);
		}
	}

	private boolean noPermissionExist(List<String> recordPartReadPermissions, String key) {
		return !recordPartReadPermissions.contains(key);
	}

	@Override
	public DataGroup replaceRecordPartsUsingPermissions(DataGroup originalDataGroup,
			DataGroup changedDataGroup, Map<String, String> recordPartConstraints,
			List<String> recordPartPermissions) {
		// TODO Auto-generated method stub
		return null;
	}

}
