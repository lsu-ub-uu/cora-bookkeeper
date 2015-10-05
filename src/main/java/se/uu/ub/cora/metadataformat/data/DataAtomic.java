package se.uu.ub.cora.metadataformat.data;

public final class DataAtomic implements DataElement {

	private String nameInData;
	private String value;

	public static DataAtomic withNameInDataAndValue(String nameInData, String value) {
		return new DataAtomic(nameInData, value);
	}

	private DataAtomic(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public String getValue() {
		return value;
	}

}
