package se.uu.ub.cora.metadataformat.presentation;

import java.util.ArrayList;
import java.util.List;

public class PresentationGroup implements PresentationElement {

	private String id;
	private String refGroupId;
	private List<PresentationChildReference> childReferences = new ArrayList<>();

	public PresentationGroup(String id, String refGroupId) {
		this.id = id;
		this.refGroupId = refGroupId;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getRefGroupId() {
		return refGroupId;
	}

	public void addChild(PresentationChildReference childReference) {
		childReferences.add(childReference);
	}

	public List<PresentationChildReference> getChildReferences() {
		return childReferences;
	}

}
