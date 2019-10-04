package se.uu.ub.cora.bookkeeper.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.data.DataAtomic;
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
		// List<DataGroup> elements = new ArrayList<>();
		// DataGroup dataGroup = DataGroup.withNameInData("someElement");
		// elements.add(dataGroup);
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
		DataGroup recordType = DataGroup.withNameInData("someRecordType");
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "someRecordId"));
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
