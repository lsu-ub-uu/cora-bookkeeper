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

import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupRedactorSpy implements DataGroupRedactor {

	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataGroup removeChildrenForConstraintsWithoutPermissions(DataGroup dataGroup,
			Set<Constraint> recordPartConstraints, Set<String> recordPartPermissions) {
		MCR.addCall("dataGroup", dataGroup, "recordPartConstraints", recordPartConstraints,
				"recordPartPermissions", recordPartPermissions);
		DataGroupForDataRedactorSpy dataGroupSpy = new DataGroupForDataRedactorSpy("spyNameInData");

		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;

	}

	@Override
	public DataGroup replaceChildrenForConstraintsWithoutPermissions(DataGroup originalDataGroup,
			DataGroup changedDataGroup, Set<Constraint> recordPartConstraints,
			Set<String> recordPartPermissions) {
		MCR.addCall("originalDataGroup", originalDataGroup, "changedDataGroup", changedDataGroup,
				"recordPartConstraints", recordPartConstraints, "recordPartPermissions",
				recordPartPermissions);
		DataGroupForDataRedactorSpy dataGroupSpy = new DataGroupForDataRedactorSpy("spyNameInData");
		MCR.addReturned(dataGroupSpy);
		return dataGroupSpy;
	}

}
