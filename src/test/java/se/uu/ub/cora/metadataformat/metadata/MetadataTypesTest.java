package se.uu.ub.cora.metadataformat.metadata;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.metadata.MetadataTypes;


public class MetadataTypesTest {
	@Test
	public void testEnum() {
		// small hack to get 100% coverage on enum
		MetadataTypes.valueOf(MetadataTypes.GROUP.toString());
	}
}
