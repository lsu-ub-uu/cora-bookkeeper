package se.uu.ub.cora.metadataformat.metadata;

public class MetadataGroupChild extends MetadataGroup {

	private String parentId;

	public MetadataGroupChild(String id, String nameInData, String textId, String defTextId,
			String parentId) {
		super(id, nameInData, textId, defTextId);
		this.parentId = parentId;

	}

	public String getRefParentId() {
		return parentId;
	}

}
