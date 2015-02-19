package epc.metadataformat;

import java.util.List;

/**
 * MetadataGroup handles metadata groups.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataGroup extends MetadataElement {

	private final List<String> attributeReference;
	private final List<MetadataChildReference> childReferences;

	public MetadataGroup(String id, String dataId, String textId,
			String deffTextId, List<String> attributeReference,
			List<MetadataChildReference> childReferences) {
		super(id, dataId, textId, deffTextId);
		this.attributeReference = attributeReference;
		this.childReferences = childReferences;
	}

	public List<String> getAttributeReferences() {
		return attributeReference;
	}

	public List<MetadataChildReference> getChildReferences() {
		return childReferences;
	}

}
