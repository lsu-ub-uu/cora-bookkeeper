package epc.metadataformat.data;

public class DataAtomic implements DataElement {

	private String dataId;
	private String value;

	public DataAtomic(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;
	}

	public String getDataId() {
		return dataId;
	}

	public String getValue() {
		return value;
	}

}
