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

import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class MatcherFactorySpy implements MatcherFactory {

	public List<MatcherSpy> returnedMatchers = new ArrayList<>();
	public List<DataGroup> dataGroups = new ArrayList<>();
	public List<MetadataGroup> metadataGroups = new ArrayList<>();
	public boolean hasMatchingChild = true;
	public List<Boolean> hasMatchingChildList = new ArrayList<>();
	private int noOfCalls = 0;
	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public Matcher factor(DataGroup dataGroup, MetadataGroup metadataGroup) {
		MCR.addCall("dataGroup", dataGroup, "metadataGroup", metadataGroup);

		dataGroups.add(dataGroup);
		metadataGroups.add(metadataGroup);
		MatcherSpy returnedMatcher = new MatcherSpy();
		setHasMatchingChild(returnedMatcher);
		returnedMatchers.add(returnedMatcher);

		MCR.addReturned(returnedMatcher);
		return returnedMatcher;
	}

	private void setHasMatchingChild(MatcherSpy returnedMatcher) {
		if (hasMatchingChildList.isEmpty()) {
			returnedMatcher.hasMatchingChild = hasMatchingChild;
		} else {
			returnedMatcher.hasMatchingChild = hasMatchingChildList.get(noOfCalls);
		}
		// returnedMatcher.attributesToReplacedDataGroup = attributesToMatchedDataGroup;
		noOfCalls++;
	}

}
