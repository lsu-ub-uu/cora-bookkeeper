/*
 * Copyright 2017 Uppsala University Library
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CollectedTermHolder {

	private Map<String, List<CollectedTerm>> collectedTerms = new HashMap<>();
	public final String recordType;
	public final String recordId;

	private CollectedTermHolder(String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
	}

	public static CollectedTermHolder createCollectedTermHolderWithRecordTypeAndRecordId(
			String recordType, String recordId) {
		return new CollectedTermHolder(recordType, recordId);
	}

	public void addCollectedTerm(CollectedTerm collectedTerm) {
		if (hasNotCollectedTermsWithType(collectedTerm.type)) {
			collectedTerms.put(collectedTerm.type, new ArrayList<>());
		}
		collectedTerms.get(collectedTerm.type).add(collectedTerm);
	}

	public List<CollectedTerm> getByCollectedTermType(String collectedTermType) {
		checkCollectedTermTypeExists(collectedTermType);
		return collectedTerms.get(collectedTermType);
	}

	private void checkCollectedTermTypeExists(String collectedTermType) {
		if (hasNotCollectedTermsWithType(collectedTermType)) {
			throw new DataMissingException(
					"No collectedTerms exists of requested type:" + collectedTermType);
		}
	}

	public boolean hasNotCollectedTermsWithType(String collectedTermType) {
		return !collectedTerms.containsKey(collectedTermType);
	}

}
