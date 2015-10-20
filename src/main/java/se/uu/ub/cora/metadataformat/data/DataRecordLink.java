package se.uu.ub.cora.metadataformat.data;

public final class DataRecordLink implements DataElement {
	private String nameInData;
	private String recordType;
	private String recordId;
	private String repeatId;

	public static DataRecordLink withNameInDataAndRecordTypeAndRecordId(String nameInData,
			String recordType, String recordId) {
		return new DataRecordLink(nameInData, recordType, recordId);
	}

	private DataRecordLink(String nameInData, String recordType, String recordId) {
		this.nameInData = nameInData;
		this.recordType = recordType;
		this.recordId = recordId;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public String getRecordType() {
		return recordType;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	public String getRepeatId() {
		return repeatId;
	}

}
