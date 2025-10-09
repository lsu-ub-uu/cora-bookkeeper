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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollectorImp;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class IdSourceFactoryTest {

	private static final String TYPE = "someType";
	private IdSourceFactory factory;

	@BeforeMethod
	private void beforeMethod() {
		factory = new IdSourceFactoryImp();
	}

	@Test
	public void testFactorTimestampIdSource() {
		IdSource timestamp = factory.factorTimestampIdSource(TYPE);

		assertTrue(timestamp instanceof TimeStampIdSource);
	}

	@Test
	public void testFactorTimestampIdSource_TypePassedToIdsource() {
		IdSource timestamp = factory.factorTimestampIdSource(TYPE);

		String passedType = ((TimeStampIdSource) timestamp).onlyForTestGetType();
		assertEquals(passedType, TYPE);
	}

	@Test
	public void testFactorSequenceIdSource() {
		IdSource sequence = factory.factorSequenceIdSource(new RecordStorageSpy(), "someSequenceId",
				"someDefinitionId");

		assertTrue(sequence instanceof SequenceIdSource);
	}

	@Test
	public void testFactorSequenceIdSource_passedParameters() {
		RecordStorageSpy storage = new RecordStorageSpy();
		IdSource sequence = factory.factorSequenceIdSource(storage, "someSequenceId",
				"someDefinitionId");

		SequenceIdSource sequenceIdSource = (SequenceIdSource) sequence;
		assertSame(sequenceIdSource.onlyForTestGetRecordStorage(), storage);
		assertSame(sequenceIdSource.onlyForTestSequenceId(), "someSequenceId");
		assertSame(sequenceIdSource.onlyForTestDefinitionId(), "someDefinitionId");
		assertTrue(
				sequenceIdSource.onlyForTestTermCollector() instanceof DataGroupTermCollectorImp);
		assertTrue(
				sequenceIdSource.onlyForTestLinkCollector() instanceof DataRecordLinkCollectorImp);

	}
}
