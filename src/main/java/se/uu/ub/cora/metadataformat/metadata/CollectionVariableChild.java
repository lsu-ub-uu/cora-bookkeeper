package se.uu.ub.cora.metadataformat.metadata;

public class CollectionVariableChild extends CollectionVariable {

	private String refParentId;
	private String finalValue;

	public CollectionVariableChild(String id, String nameInData, String textId, String defTextId,
			String refCollectionId, String refParentId) {
		super(id, nameInData, textId, defTextId, refCollectionId);
		this.refParentId = refParentId;

	}

	public String getRefParentId() {
		return refParentId;
	}

	public void setFinalValue(String finalValue) {
		this.finalValue = finalValue;
	}

	public String getFinalValue() {
		return finalValue;
	}
}
