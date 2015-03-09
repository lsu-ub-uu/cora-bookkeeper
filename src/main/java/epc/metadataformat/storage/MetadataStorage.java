package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataStorage is the gateway interface from the metadata system to
 * the storage system. This interface makes the storage details decoupled from
 * the logic surrounding the metadata.
 * 
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public interface MetadataStorage {

	/**
	 * getAllMetadata returns all metadata in the system as one coherent unit
	 * 
	 * @return A CoherentMetadata loaded with all metadata in the system.
	 */
	CoherentMetadata getAllMetadata();

}
