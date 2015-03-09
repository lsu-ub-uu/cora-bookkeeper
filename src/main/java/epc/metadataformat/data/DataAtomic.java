package epc.metadataformat.data;

public final class DataAtomic implements DataElement {

	private String dataId;
	private String value;

	public static DataAtomic withDataIdAndValue(String dataId, String value) {
		return new DataAtomic(dataId, value);
	}

	private DataAtomic(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;
	}

	@Override
	public String getDataId() {
		return dataId;
	}

	public String getValue() {
		return value;
	}

}
