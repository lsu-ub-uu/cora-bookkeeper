package se.uu.ub.cora.metadataformat.data;

public class DataRecordLink implements DataElement {
	private String nameInData;
	private String recordType;
	private String recordId;

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

}
