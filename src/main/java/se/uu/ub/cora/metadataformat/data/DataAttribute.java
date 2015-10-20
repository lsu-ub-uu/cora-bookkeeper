package se.uu.ub.cora.metadataformat.data;

public final class DataAttribute {

	private String nameInData;
	private String value;

	public static DataAttribute withNameInDataAndValue(String nameInData, String value) {
		return new DataAttribute(nameInData, value);
	}

	private DataAttribute(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getValue() {
		return value;
	}

}
