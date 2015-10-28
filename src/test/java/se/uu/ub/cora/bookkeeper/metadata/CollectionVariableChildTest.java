package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class CollectionVariableChildTest {
	@Test
	public void testInit() {
		CollectionVariableChild childVar = new CollectionVariableChild("id", "nameInData", "textId",
				"defTextId", "refCollection", "collectionVarId");

		assertEquals(childVar.getId(), "id", "Id should have the value set in the constructor");

		assertEquals(childVar.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(childVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(childVar.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertEquals(childVar.getRefCollectionId(), "refCollection",
				"RefCollectionId should have the value set in the constructor");

		assertEquals(childVar.getRefParentId(), "collectionVarId",
				"refParentId should have the value set in the constructor");

	}
}
