package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MetadataChildTest {
	@Test
	public void testInit() {
		MetadataChildReference metadataChild = new MetadataChildReference("childReference", 1,
				MetadataChildReference.UNLIMITED);
		assertEquals(metadataChild.getReference(), "childReference",
				"ChildReference should be the value set in the constructor");

		assertEquals(metadataChild.getRepeatMin(), 1,
				"RepeatMin should be the value set in the constructor");

		assertEquals(metadataChild.getRepeatMax(), Integer.MAX_VALUE,
				"RepeatMax should be the value set in the constructor");

	}
}
