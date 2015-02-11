package epc.metadataformat.storage;

import java.util.HashMap;
import java.util.Map;

import epc.metadataformat.MetadataElement;
import epc.metadataformat.MetadataHolder;
import epc.metadataformat.TextElement;

/**
 * MetadataStorageInMemory is a memory implementation of MetadataStorageGateway,
 * its intended use is mainly for testing.
 * 
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 *
 * @since 0.1
 *
 */
public class MetadataStorageInMemory implements MetadataStorageGateway {
	private Map<String, TextElement> texts = new HashMap<>();
	private Map<String, MetadataElement> metadataElements = new HashMap<>();

	@Override
	public void storeText(String textId, TextElement textElement) {
		texts.put(textId, textElement);

	}

	@Override
	public MetadataHolder loadAllMetadata() {
		return new MetadataHolder(texts, metadataElements);
	}

}
