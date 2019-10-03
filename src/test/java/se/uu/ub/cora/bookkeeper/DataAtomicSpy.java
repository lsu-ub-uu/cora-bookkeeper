package se.uu.ub.cora.bookkeeper;

import se.uu.ub.cora.data.DataAtomic;

public class DataAtomicSpy implements DataAtomic {

	public String nameInData;
	public String value;
	public String repeatId;

	public DataAtomicSpy(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	public DataAtomicSpy(String nameInData, String value, String repeatId) {
		this.nameInData = nameInData;
		this.value = value;
		this.repeatId = repeatId;

	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	@Override
	public String getValue() {
		return value;
	}

}
