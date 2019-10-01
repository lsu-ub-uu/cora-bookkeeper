package se.uu.ub.cora.bookkeeper;

import se.uu.ub.cora.data.DataAtomic;

public class DataAtomicSpy implements DataAtomic {

	public String nameInData;
	public String value;

	public DataAtomicSpy(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public String getRepeatId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() {
		return value;
	}

}
