/*
 * Copyright 2020, 2023 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.recordtype.internal;

import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactory;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeHandlerFactorySpy implements RecordTypeHandlerFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeHandlerFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup", RecordTypeHandlerSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorUsingRecordTypeId", RecordTypeHandlerSpy::new);
	}

	// @Override
	// public RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup) {
	// return (RecordTypeHandler) MCR.addCallAndReturnFromMRV("dataGroup", dataGroup);
	// }

	@Override
	public RecordTypeHandler factorUsingRecordTypeId(String recordTypeId) {
		return (RecordTypeHandler) MCR.addCallAndReturnFromMRV("recordTypeId", recordTypeId);
	}

	@Override
	public RecordTypeHandler factorUsingDataRecordGroup(DataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
		return null;
	}

}
