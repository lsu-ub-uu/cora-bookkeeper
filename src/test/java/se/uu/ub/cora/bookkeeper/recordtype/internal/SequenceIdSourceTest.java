/*
 * Copyright 2025 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.storage.StorageException;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class SequenceIdSourceTest {

	private static final String SEQUENCE_TYPE = "sequence";
	private static final String SEQUENCE_ID = "someSequenceId";
	private static final String DEFINITION_ID = "someDefinitionId";
	private IdSource idSource;
	private RecordStorageSpy storage;
	private DataGroupTermCollectorSpy termCollector;
	private DataRecordLinkCollectorSpy linkCollector;
	private DataRecordGroupSpy sequenceRecordGroup;
	private DataFactorySpy dataFactory;
	private int supplierCount = 0;

	@BeforeMethod
	private void beforeMethod() {
		supplierCount = 0;
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		setUpStorageReadSequence();
		linkCollector = new DataRecordLinkCollectorSpy();
		termCollector = new DataGroupTermCollectorSpy();
		idSource = new SequenceIdSource(storage, SEQUENCE_ID, DEFINITION_ID, termCollector,
				linkCollector);
	}

	private void setUpStorageReadSequence() {
		sequenceRecordGroup = new DataRecordGroupSpy();
		sequenceRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "0", "currentNumber");
		sequenceRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");

		storage = new RecordStorageSpy();
		storage.MRV.setSpecificReturnValuesSupplier("read", () -> sequenceRecordGroup,
				SEQUENCE_TYPE, SEQUENCE_ID);
	}

	@Test
	public void testReadSequenceAndStore() {
		String id = idSource.getId();

		assertReadCurrentNumberInSequence();
		assertIncrementCurrentNumber();
		assertUpdateSequence();

		assertEquals(id, "1");
	}

	private void assertIncrementCurrentNumber() {
		var newCurrentNumber = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "currentNumber", "1");
		sequenceRecordGroup.MCR.assertParameters("removeFirstChildWithNameInData", 0,
				"currentNumber");
		sequenceRecordGroup.MCR.assertParameters("addChild", 0, newCurrentNumber);
	}

	@Test
	public void testGetId_AnotherCurrentNumber() {
		sequenceRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "500", "currentNumber");

		String id = idSource.getId();

		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "currentNumber",
				"501");
		assertEquals(id, "501");
	}

	private void assertReadCurrentNumberInSequence() {
		var dataRecordGroup = (DataRecordGroupSpy) storage.MCR.assertCalledParametersReturn("read",
				SEQUENCE_TYPE, SEQUENCE_ID);
		dataRecordGroup.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"currentNumber");
	}

	private void assertUpdateSequence() {
		var collectedTerms = (CollectTerms) termCollector.MCR
				.assertCalledParametersReturn("collectTerms", DEFINITION_ID, sequenceRecordGroup);

		DataGroupSpy sequenceGroup = (DataGroupSpy) dataFactory.MCR.assertCalledParametersReturn(
				"factorGroupFromDataRecordGroup", sequenceRecordGroup);
		var collectedLinks = linkCollector.MCR.assertCalledParametersReturn("collectLinks",
				DEFINITION_ID, sequenceGroup);

		storage.MCR.assertParameters("update", 0, SEQUENCE_TYPE, SEQUENCE_ID, sequenceGroup,
				collectedTerms.storageTerms, collectedLinks, "someDataDivider");
	}

	@Test
	public void testOnConlictRetry() {
		setUpConflictOnUpdate();

		idSource.getId();

		storage.MCR.assertNumberOfCallsToMethod("read", 3);
		linkCollector.MCR.assertNumberOfCallsToMethod("collectLinks", 3);
		// OBS it should be 3, but due the problem before
		storage.MCR.assertNumberOfCallsToMethod("update", 1);
	}

	private void setUpConflictOnUpdate() {
		Supplier<?> supplierThrowStorageExceptionOnFirstCall = this::throwStorageExceptionOnFirstCall;

		// OBS: Since update do not return, it was not possible to link a supplier that returned
		// different options on each call. We use linkCollector instead even though it is update
		// that should throw StoerageException
		linkCollector.MRV.setDefaultReturnValuesSupplier("collectLinks",
				supplierThrowStorageExceptionOnFirstCall);
	}

	private Object throwStorageExceptionOnFirstCall() {
		supplierCount++;
		if (supplierCount <= 2) {
			throw StorageException.withMessage("fromSpy");
		}
		return Collections.emptySet();
	}

	@Test
	public void testOnConlictRetry_wait() {
		setUpConflictOnUpdate();

		long startTime = System.currentTimeMillis();

		idSource.getId();

		long elapsedTime = System.currentTimeMillis() - startTime;

		assertTrue(elapsedTime >= 10, "Expected wait of at least 2 x 5 ms: " + elapsedTime);
	}
}
