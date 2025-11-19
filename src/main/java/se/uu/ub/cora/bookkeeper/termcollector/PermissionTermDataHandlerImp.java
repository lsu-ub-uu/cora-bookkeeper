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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.collected.PermissionTerm;

public class PermissionTermDataHandlerImp implements PermissionTermDataHandler {

	private CollectTermHolder collectTermHolder;
	private Map<String, List<PermissionTerm>> previousPermissionTermsById;
	private Map<String, List<PermissionTerm>> currentPermissionTermsById;

	@Override
	public List<PermissionTerm> getMixedPermissionTermValuesConsideringModeState(
			List<PermissionTerm> previousPermissions, List<PermissionTerm> currentPermissions) {
		collectTermHolder = getCollectTermHolder();

		previousPermissionTermsById = groupById(previousPermissions);
		currentPermissionTermsById = groupById(currentPermissions);

		return getMixedPermissionTerms();
	}

	private CollectTermHolder getCollectTermHolder() {
		MetadataStorageView storageView = MetadataStorageProvider.getStorageView();
		return storageView.getCollectTermHolder();
	}

	private Map<String, List<PermissionTerm>> groupById(List<PermissionTerm> list) {
		return list.stream().collect(Collectors.groupingBy(PermissionTerm::id));
	}

	private List<PermissionTerm> getMixedPermissionTerms() {
		return currentPermissionTermsById.entrySet().stream().flatMap(
				currentPermissionTerm -> getCurrentOrPreviousTermDependingOnPermissionTermMode(
						currentPermissionTerm).stream())
				.toList();
	}

	private List<PermissionTerm> getCurrentOrPreviousTermDependingOnPermissionTermMode(
			Map.Entry<String, List<PermissionTerm>> currentPermissionTerm) {
		String id = currentPermissionTerm.getKey();
		if (isStatePermissionTerm(id)) {
			return getPreviousPermissionTerms(id);
		}
		return currentPermissionTerm.getValue();

	}

	private List<PermissionTerm> getPreviousPermissionTerms(String id) {
		if (previousPermissionTermsById.containsKey(id)) {
			return previousPermissionTermsById.get(id);
		}
		return Collections.emptyList();

	}

	private boolean isStatePermissionTerm(String id) {
		var metaPermissionTerm = (se.uu.ub.cora.bookkeeper.metadata.PermissionTerm) collectTermHolder
				.getCollectTermById(id);
		return metaPermissionTerm.mode.equals(STATE);
	}

}
