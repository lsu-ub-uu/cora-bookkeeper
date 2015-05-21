package epc.metadataformat.metadata;

import java.util.ArrayList;
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

	private final List<String> attributeReferences = new ArrayList<>();
	private final List<MetadataChildReference> childReferences = new ArrayList<>();

	public static MetadataGroup withIdAndDataIdAndTextIdAndDefTextId(String id, String dataId,
			String textId, String defTextId) {
		return new MetadataGroup(id, dataId, textId, defTextId);
	}

	protected MetadataGroup(String id, String dataId, String textId, String defTextId) {
		super(id, dataId, textId, defTextId);
	}

	public List<String> getAttributeReferences() {
		return attributeReferences;
	}

	public List<MetadataChildReference> getChildReferences() {
		return childReferences;
	}

	public void addAttributeReference(String attributeReferenceId) {
		attributeReferences.add(attributeReferenceId);

	}

	public void addChildReference(MetadataChildReference metadataChildReference) {
		childReferences.add(metadataChildReference);
	}

}
