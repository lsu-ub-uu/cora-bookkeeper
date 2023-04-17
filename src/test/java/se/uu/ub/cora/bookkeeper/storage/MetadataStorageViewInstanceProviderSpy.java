/*
 * Copyright 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.storage;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MetadataStorageViewInstanceProviderSpy implements MetadataStorageViewInstanceProvider {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public MetadataStorageViewInstanceProviderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getStorageView", MetadataStorageViewSpy::new);
		MRV.setDefaultReturnValuesSupplier("getOrderToSelectImplementionsBy", () -> 0);
	}

	@Override
	public MetadataStorageView getStorageView() {
		return (MetadataStorageView) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public int getOrderToSelectImplementionsBy() {
		return (int) MCR.addCallAndReturnFromMRV();
	}

}
