package se.uu.ub.cora.bookkeeper.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.storage.MetadataStorage;

public class MetadataStorageForDataValidatorSpy implements MetadataStorage {

	public List<DataGroup> recordTypes = new ArrayList<>();
	public boolean getMetadataElementsWasCalled = false;

	@Override
	public Collection<DataGroup> getCollectTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getMetadataElements() {
		getMetadataElementsWasCalled = true;
		return Collections.emptyList();
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		recordTypes = new ArrayList<>();
		DataGroup recordType = new DataGroupSpy("someRecordType");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someRecordId"));
		recordType.addChild(recordInfo);

		recordTypes.add(recordType);
		return recordTypes;
	}

	@Override
	public Collection<DataGroup> getTexts() {
		// TODO Auto-generated method stub
		return null;
	}

}
