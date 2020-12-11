package se.uu.ub.cora.bookkeeper.recordpart;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupWrapper implements DataGroup {
	List<String> removedNameInDatas = new ArrayList<>();
	private DataGroup updatedDataGroup;

	public DataGroupWrapper(DataGroup updatedDataGroup) {
		this.updatedDataGroup = updatedDataGroup;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		removedNameInDatas.add(childNameInData);
		return updatedDataGroup.removeAllChildrenWithNameInDataAndAttributes(childNameInData,
				childAttributes);
		// return false;
	}
}
