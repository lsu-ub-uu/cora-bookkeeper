/*
 * Copyright 2026 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.validator;

import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class CollectionVariableSpy extends CollectionVariable {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public CollectionVariableSpy() {
		super(null, null, null, null, null);
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someNameInData");
		MRV.setDefaultReturnValuesSupplier("getRefCollectionId", () -> "someRefCollectionId");
		MRV.setDefaultReturnValuesSupplier("getRefParentId", () -> "someRefParentId");
		MRV.setDefaultReturnValuesSupplier("getFinalValue", () -> "someFinalValue");
		MRV.setDefaultReturnValuesSupplier("getAttributeReferences", Collections::emptyList);
	}

	@Override
	public String getNameInData() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getRefCollectionId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void setRefParentId(String refParentId) {
		MCR.addCall("refParentId", refParentId);
	}

	@Override
	public String getRefParentId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void setFinalValue(String finalValue) {
		MCR.addCall("finalValue", finalValue);
	}

	@Override
	public String getFinalValue() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public void addAttributeReference(String attributeReferenceId) {
		MCR.addCall("attributeReferenceId", attributeReferenceId);
	}

	@Override
	public List<String> getAttributeReferences() {
		return (List<String>) MCR.addCallAndReturnFromMRV();
	}
}
