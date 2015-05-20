package epc.metadataformat.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MetadataChildReferenceTest {
	private MetadataChildReference metadataChildReference;

	@BeforeMethod
	public void beforeMethod() {
		metadataChildReference = MetadataChildReference.withReferenceIdAndRepeatMinAndRepeatMax(
				"metadataChildReference", 1, MetadataChildReference.UNLIMITED);
	}

	@Test
	public void testInit() {
		assertEquals(metadataChildReference.getReferenceId(), "metadataChildReference",
				"ChildReference should be the value set in the constructor");

		assertEquals(metadataChildReference.getRepeatMin(), 1,
				"RepeatMin should be the value set in the constructor");

		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE,
				"RepeatMax should be the value set in the constructor");

	}

	@Test
	public void testRepeatMinKey() {
		metadataChildReference.setRepeatMinKey("REPEAT_MIN");
		assertEquals(metadataChildReference.getRepeatMinKey(), "REPEAT_MIN");
	}

	@Test
	public void testSecretTrue() {
		metadataChildReference.setSecret(true);
		assertTrue(metadataChildReference.isSecret());
	}

	@Test
	public void testSecretFalse() {
		metadataChildReference.setSecret(false);
		assertFalse(metadataChildReference.isSecret());
	}

	@Test
	public void testSecretKey() {
		metadataChildReference.setSecretKey("SECRET");
		assertEquals(metadataChildReference.getSecretKey(), "SECRET");
	}

	@Test
	public void testReadOnlyTrue() {
		metadataChildReference.setReadOnly(true);
		assertTrue(metadataChildReference.isReadOnly());
	}

	@Test
	public void testReadOnlyFalse() {
		metadataChildReference.setReadOnly(false);
		assertFalse(metadataChildReference.isReadOnly());
	}

	@Test
	public void testReadOnlyKey() {
		metadataChildReference.setReadOnlyKey("READ_ONLY");
		assertEquals(metadataChildReference.getReadOnlyKey(), "READ_ONLY");
	}
}
