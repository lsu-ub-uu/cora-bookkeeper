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
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataGroupRedactorSpy implements DataGroupRedactor {

	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();

	public List<Boolean> removeHasBeenCalledList = new ArrayList<>();

	private int noOfCalls = 0;

	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(DataGroup dataGroup,
			Set<Constraint> recordPartConstraints, Set<String> recordPartPermissions) {
		MCR.addCall("dataGroup", dataGroup, "recordPartConstraints", recordPartConstraints,
				"recordPartPermissions", recordPartPermissions);
		DataGroupForDataRedactorSpy dataGroupSpy = new DataGroupForDataRedactorSpy("spyNameInData");

		setRemoveHasBeenCalled(dataGroupSpy);
		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;

	}

	private void setRemoveHasBeenCalled(DataGroupForDataRedactorSpy dataGroupSpy) {
		if (removeHasBeenCalledList.isEmpty()) {
			dataGroupSpy.removeHasBeenCalled = false;
		} else {
			dataGroupSpy.removeHasBeenCalled = removeHasBeenCalledList.get(noOfCalls);
		}
		noOfCalls++;
	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(DataGroup originalDataGroup,
			DataGroup changedDataGroup, Set<Constraint> recordPartConstraints,
			Set<String> recordPartPermissions) {
		MCR.addCall("originalDataGroup", originalDataGroup, "changedDataGroup", changedDataGroup,
				"recordPartConstraints", recordPartConstraints, "recordPartPermissions",
				recordPartPermissions);

		DataGroupForDataRedactorSpy dataGroupSpy = new DataGroupForDataRedactorSpy("spyNameInData");

		setRemoveHasBeenCalled(dataGroupSpy);

		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;
	}

}
