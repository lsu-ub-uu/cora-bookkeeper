package epc.metadataformat.storage;

import java.util.Collection;

import epc.metadataformat.data.DataGroup;

/**
 * MetadataStorage is the gateway interface from the metadata system to the storage system. This
 * interface makes the storage details decoupled from the logic surrounding the metadata.
 * 
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public interface MetadataStorage {

	Collection<DataGroup> getMetadataElements();

	Collection<DataGroup> getPresentationElements();

	Collection<DataGroup> getTexts();

	Collection<DataGroup> getRecordTypes();
}
