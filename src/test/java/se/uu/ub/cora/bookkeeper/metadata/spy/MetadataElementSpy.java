/*
 * Copyright 2023, 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata.spy;

import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MetadataElementSpy implements MetadataElement {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public MetadataElementSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someNameInData");
		MRV.setDefaultReturnValuesSupplier("getTextId", () -> "someTextId");
		MRV.setDefaultReturnValuesSupplier("getDefTextId", () -> "someDefTextId");
		MRV.setDefaultReturnValuesSupplier("getAttributeReferences",
				() -> List.of("attributeReference001"));
	}

	@Override
	public String getId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getNameInData() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getTextId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getDefTextId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public List<String> getAttributeReferences() {
		return (List<String>) MCR.addCallAndReturnFromMRV();
	}

}
