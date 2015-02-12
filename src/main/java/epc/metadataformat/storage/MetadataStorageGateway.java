package epc.metadataformat.storage;

import epc.metadataformat.CoherentMetadata;

/**
 * MetadataStorageGateway is the gateway interface from the metadata system to
 * the storage system. This interface makes the storage details decoupled from
 * the logic surrounding the metadata.
 * 
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public interface MetadataStorageGateway {

	public CoherentMetadata loadAllMetadata();

}
