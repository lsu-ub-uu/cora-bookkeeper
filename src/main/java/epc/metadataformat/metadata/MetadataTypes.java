package epc.metadataformat.metadata;

public enum MetadataTypes {

	METADATA_GROUP("metadataGroup"), METADATA_TEXTVARIABLE("metadataTextVariable");
	private MetadataTypes(String type) {
		this.type = type;
	}

	public String type;
}
