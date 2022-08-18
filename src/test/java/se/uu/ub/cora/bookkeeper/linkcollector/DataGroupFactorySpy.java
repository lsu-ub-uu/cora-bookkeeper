package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;

public class DataGroupFactorySpy implements DataGroupFactory {

	public List<String> usedNameInDatas = new ArrayList<>();

	@Override
	public DataGroup factorUsingNameInData(String nameInData) {
		usedNameInDatas.add(nameInData);
		return new DataGroupOldSpy(nameInData);
	}

	@Override
	public DataGroup factorAsLinkWithNameInDataTypeAndId(String nameInData, String recordType,
			String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

}
