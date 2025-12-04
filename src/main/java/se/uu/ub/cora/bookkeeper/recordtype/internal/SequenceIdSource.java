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

import java.util.Set;

import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.collected.Link;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageException;

public class SequenceIdSource implements IdSource {

	private static final String CURRENT_NUMBER = "currentNumber";
	private RecordStorage storage;
	private String sequenceId;
	private DataGroupTermCollector termCollector;
	private DataRecordLinkCollector linkCollector;
	private String definitionId;

	public SequenceIdSource(RecordStorage storage, String sequenceId, String definitionId,
			DataGroupTermCollector termCollector, DataRecordLinkCollector linkCollector) {
		this.storage = storage;
		this.sequenceId = sequenceId;
		this.definitionId = definitionId;
		this.termCollector = termCollector;
		this.linkCollector = linkCollector;
	}

	@Override
	public String getId() {
		return tryToGetNextIdAndUpdateSequence();
	}

	private String tryToGetNextIdAndUpdateSequence() {
		try {
			return getNextIdAndUpdateSequence();
		} catch (StorageException _) {
			wait(5);
			return tryToGetNextIdAndUpdateSequence();
		}
	}

	private void wait(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException _) {
			Thread.currentThread().interrupt();
		}
	}

	private String getNextIdAndUpdateSequence() {
		DataRecordGroup dataRecordGroup = readSequence();
		String nextIdAsString = incrementNumber(dataRecordGroup);
		updateSequenceRecordGroup(dataRecordGroup, nextIdAsString);
		updateSequence(dataRecordGroup);
		return nextIdAsString;
	}

	private DataRecordGroup readSequence() {
		return storage.read("sequence", sequenceId);
	}

	private String incrementNumber(DataRecordGroup dataRecordGroup) {
		String currentNumber = dataRecordGroup.getFirstAtomicValueWithNameInData(CURRENT_NUMBER);
		long currentNumberAsLong = Long.parseLong(currentNumber);
		return String.valueOf(currentNumberAsLong + 1);
	}

	private void updateSequenceRecordGroup(DataRecordGroup dataRecordGroup, String nextIdAsString) {
		DataAtomic newCurrentNumber = DataProvider
				.createAtomicUsingNameInDataAndValue(CURRENT_NUMBER, nextIdAsString);
		dataRecordGroup.removeFirstChildWithNameInData(CURRENT_NUMBER);
		dataRecordGroup.addChild(newCurrentNumber);
	}

	private void updateSequence(DataRecordGroup dataRecordGroup) {
		CollectTerms collectTerms = termCollector.collectTerms(definitionId, dataRecordGroup);
		DataGroup recordAsDataGroup = DataProvider.createGroupFromRecordGroup(dataRecordGroup);
		Set<Link> collectLinks = linkCollector.collectLinks(definitionId, recordAsDataGroup);
		storage.update("sequence", sequenceId, recordAsDataGroup, collectTerms.storageTerms,
				collectLinks, dataRecordGroup.getDataDivider());
	}

	public RecordStorage onlyForTestGetRecordStorage() {
		return storage;
	}

	public String onlyForTestSequenceId() {
		return sequenceId;
	}

	public String onlyForTestDefinitionId() {
		return definitionId;
	}

	public DataGroupTermCollector onlyForTestTermCollector() {
		return termCollector;
	}

	public DataRecordLinkCollector onlyForTestLinkCollector() {
		return linkCollector;
	}
}
