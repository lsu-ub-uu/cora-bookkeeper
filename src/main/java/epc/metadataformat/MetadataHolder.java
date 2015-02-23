package epc.metadataformat;

import java.util.HashMap;
import java.util.Map;

/**
 * MetadataHolder holds all information about MetadataFormats MetadataGroups and
 * MetadataVariables
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataHolder {

	private Map<String, MetadataElement> metadata = new HashMap<>();

	/**
	 * addMetadataElement adds an element to the internal holder of elements
	 * 
	 * @param metadataElement
	 *            A MetadataElement to add to the internal holder
	 */
	public void addMetadataElement(MetadataElement metadataElement) {
		metadata.put(metadataElement.getId(), metadataElement);
	}

	/**
	 * getMetadataElement returns the requested MetadataElement based on the
	 * parameter elementId
	 * 
	 * @param elementId
	 *            A String with the id of the Metadata element to get
	 * @return The requested MetadataElement
	 */
	public MetadataElement getMetadataElement(String elementId) {
		return metadata.get(elementId);
	}
}
