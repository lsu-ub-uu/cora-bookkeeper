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

import static se.uu.ub.cora.bookkeeper.metadata.PermissionTerm.Mode.STATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.collected.PermissionTerm;

public class PermissionTermDataHandlerImp implements PermissionTermDataHandler {

	private CollectTermHolder collectTermHolder;
	private Map<String, PermissionTerm> previousPermissionsAsMap;

	@Override
	public List<PermissionTerm> getMixedPermissionTermValuesConsideringModeState(
			List<PermissionTerm> previousPermissions, List<PermissionTerm> currentPermissions) {
		collectTermHolder = getCollectTermHolder();
		previousPermissionsAsMap = convertListToMap(previousPermissions);

		return getMixedPermissionTerms(currentPermissions);
	}

	private CollectTermHolder getCollectTermHolder() {
		MetadataStorageView storageView = MetadataStorageProvider.getStorageView();
		return storageView.getCollectTermHolder();
	}

	private Map<String, PermissionTerm> convertListToMap(List<PermissionTerm> list) {
		return list.stream().collect(Collectors.toMap(PermissionTerm::id, p -> p));
	}

	private List<PermissionTerm> getMixedPermissionTerms(List<PermissionTerm> currentPermissions) {
		List<PermissionTerm> mixedPermissionTerms = new ArrayList<>();
		for (PermissionTerm permissionTerm : currentPermissions) {
			mixedPermissionTerms.add(getCorrectPermissionTermAndValue(permissionTerm));
		}
		return mixedPermissionTerms;
	}

	private PermissionTerm getCorrectPermissionTermAndValue(PermissionTerm permissionTerm) {
		if (isStatePermissionTerm(permissionTerm)) {
			return getPreviousPermissionTermAndValue(permissionTerm);
		}
		return permissionTerm;
	}

	private boolean isStatePermissionTerm(PermissionTerm permissionTerm) {
		var metaPermissionTerm = (se.uu.ub.cora.bookkeeper.metadata.PermissionTerm) collectTermHolder
				.getCollectTermById(permissionTerm.id());
		return metaPermissionTerm.mode.equals(STATE);
	}

	private PermissionTerm getPreviousPermissionTermAndValue(PermissionTerm permissionTerm) {
		return previousPermissionsAsMap.get(permissionTerm.id());
	}

}
