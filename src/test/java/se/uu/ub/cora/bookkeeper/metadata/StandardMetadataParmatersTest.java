package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

public class StandardMetadataParmatersTest {

	@Test
	public void testStandardParameters() {

		String id = "someId";
		String nameInData = "someNameInData";
		TextContainer textContainer = TextContainer.usingTextIdAndDefTextId("someText",
				"someDefText");
		StandardMetadataParameters parameters = StandardMetadataParameters
				.usingIdNameInDataAndTextContainer(id, nameInData, textContainer);

		assertEquals(parameters.id, id);
		assertEquals(parameters.nameInData, nameInData);
		assertEquals(parameters.textId, textContainer.textId);
		assertEquals(parameters.defTextId, textContainer.defTextId);

	}

	@Test
	public void testStandardParametersTextContainerNull() {
		String id = "someId";
		String nameInData = "someNameInData";
		StandardMetadataParameters parameters = StandardMetadataParameters
				.usingIdNameInDataAndTextContainer(id, nameInData, null);

		assertEquals(parameters.id, id);
		assertEquals(parameters.nameInData, nameInData);
		assertNull(parameters.textId);
		assertNull(parameters.defTextId);

	}
}
