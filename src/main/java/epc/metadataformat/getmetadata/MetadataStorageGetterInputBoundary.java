package epc.metadataformat.getmetadata;

import epc.metadataformat.CoherentMetadata;

/**
 * GetMetadataInputBoundry is the boundry for accessing GetMetadata from other
 * projects
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 
 *
 */
public interface MetadataStorageGetterInputBoundary {
	/**
	 * getAllMetadata returns all metadata for the whole system, as a
	 * CoherentMetadata populated with metadataFormat, Presentation, Collections
	 * and Texts
	 * 
	 * @return A CoherentMetadata with all metadata for the entire system
	 */
	CoherentMetadata getAllMetadata();
}
