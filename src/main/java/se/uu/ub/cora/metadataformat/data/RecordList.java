package se.uu.ub.cora.metadataformat.data;

import java.util.ArrayList;
import java.util.List;

public final class RecordList {
	private String containRecordsOfType;
	private List<DataRecord> records = new ArrayList<>();
	private String totalNo;
	private String fromNo;
	private String toNo;

	public static RecordList withContainRecordsOfType(String containsRecordTypes) {
		return new RecordList(containsRecordTypes);
	}

	private RecordList(String containRecordsOfType) {
		this.containRecordsOfType = containRecordsOfType;
	}

	public String getContainRecordsOfType() {
		return containRecordsOfType;
	}

	public void addRecord(DataRecord record) {
		records.add(record);
	}

	public List<DataRecord> getRecords() {
		return records;
	}

	public void setTotalNo(String totalNo) {
		this.totalNo = totalNo;
	}

	public String getTotalNo() {
		return totalNo;
	}

	public void setFromNo(String fromNo) {
		this.fromNo = fromNo;

	}

	public String getFromNo() {
		return fromNo;
	}

	public void setToNo(String toNo) {
		this.toNo = toNo;

	}

	public String getToNo() {
		return toNo;
	}

}
