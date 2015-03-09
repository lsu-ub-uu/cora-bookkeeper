package epc.metadataformat;

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

	private final List<String> attributeReference = new ArrayList<>();
	private final List<MetadataChildReference> childReferences = new ArrayList<>();

	public static MetadataGroup withIdAndDataIdAndTextIdAndDeffTextId(String id, String dataId,
			String textId, String deffTextId) {
		return new MetadataGroup(id, dataId, textId, deffTextId);
	}

	private MetadataGroup(String id, String dataId, String textId, String deffTextId) {
		super(id, dataId, textId, deffTextId);
	}

	public List<String> getAttributeReferences() {
		return attributeReference;
	}

	public List<MetadataChildReference> getChildReferences() {
		return childReferences;
	}

	public void addAttributeReference(String attributeReferenceId) {
		attributeReference.add(attributeReferenceId);

	}

	public void addChildReference(MetadataChildReference metadataChildReference) {
		childReferences.add(metadataChildReference);
	}

}
