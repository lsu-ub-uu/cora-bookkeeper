package epc.metadataformat.metadata;

public class MetadataGroupChild extends MetadataGroup {

	private String parentId;

	public MetadataGroupChild(String id, String dataId, String textId, String defTextId,
			String parentId) {
		super(id, dataId, textId, defTextId);
		this.parentId = parentId;

	}

	public String getRefParentId() {
		return parentId;
	}

}
