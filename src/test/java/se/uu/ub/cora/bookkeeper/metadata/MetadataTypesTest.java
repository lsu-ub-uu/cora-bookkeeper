package se.uu.ub.cora.bookkeeper.metadata;

import org.testng.annotations.Test;


public class MetadataTypesTest {
	@Test
	public void testEnum() {
		// small hack to get 100% coverage on enum
		MetadataTypes.valueOf(MetadataTypes.GROUP.toString());
	}
}
