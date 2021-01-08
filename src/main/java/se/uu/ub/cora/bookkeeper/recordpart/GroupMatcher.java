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

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.validator.MetadataMatchData;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataGroup;

public class GroupMatcher implements Matcher {

	private MetadataMatchData dataMatcher;
	private DataGroup dataGroup;
	private MetadataGroup metadataGroup;
	private DataGroup foundChild = null;

	public GroupMatcher(MetadataMatchData dataMatcher, DataGroup dataGroup,
			MetadataGroup metadataGroup) {
		this.dataMatcher = dataMatcher;
		this.dataGroup = dataGroup;
		this.metadataGroup = metadataGroup;
	}

	@Override
	public boolean groupHasMatchingDataChild() {
		if (noChildWasFound()) {
			tryToFindMatchingChild();
		}
		return childWasFound();
	}

	@Override
	public DataGroup getMatchingDataChild() {
		if (childWasFound()) {
			return foundChild;
		}
		tryToFindMatchingChild();
		if (noChildWasFound()) {
			throw new DataMissingException("No matching child found");

		}
		return foundChild;

	}

	private boolean childWasFound() {
		return foundChild != null;
	}

	private boolean noChildWasFound() {
		return foundChild == null;
	}

	private void tryToFindMatchingChild() {
		String metadataNameInData = metadataGroup.getNameInData();
		List<DataGroup> allGroupsWithNameInData = dataGroup
				.getAllGroupsWithNameInData(metadataNameInData);
		for (DataGroup child : allGroupsWithNameInData) {
			ValidationAnswer answer = dataMatcher.metadataSpecifiesData(metadataGroup, child);
			if (answer.dataIsValid()) {
				foundChild = child;
			}
		}
	}

	public MetadataMatchData getMetadataMatchData() {
		return dataMatcher;
	}
}
