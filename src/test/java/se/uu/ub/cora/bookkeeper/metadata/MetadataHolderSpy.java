/*
 * Copyright 2020, 2025 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MetadataHolderSpy implements MetadataHolder {
	@SuppressWarnings("exports")
	public MethodCallRecorder MCR = new MethodCallRecorder();
	@SuppressWarnings("exports")
	public MethodReturnValues MRV = new MethodReturnValues();

	public MetadataHolderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getMetadataElement", MetadataElementSpy::new);
		MRV.setDefaultReturnValuesSupplier("containsElement", () -> true);
	}

	@Override
	public void addMetadataElement(MetadataElement metadataElement) {
		MCR.addCall("metadataElement", metadataElement);
	}

	@Override
	public MetadataElement getMetadataElement(String elementId) {
		System.err.println("elementId: " + elementId);
		return (MetadataElement) MCR.addCallAndReturnFromMRV("elementId", elementId);
	}

	@Override
	public void deleteMetadataElement(String elementId) {
		MCR.addCall("elementId", elementId);
	}

	@Override
	public boolean containsElement(String elementId) {
		return (boolean) MCR.addCallAndReturnFromMRV("elementId", elementId);
	}
}
