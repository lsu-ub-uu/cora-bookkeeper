package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TextContainerTest {
	@Test
	public void testTextAndDefText() {
		String textId = "someText";
		String defTextId = "someDefText";
		TextContainer container = TextContainer.usingTextIdAndDefTextId(textId, defTextId);
		assertEquals(container.textId, "someText");
		assertEquals(container.defTextId, "someDefText");
	}
}
