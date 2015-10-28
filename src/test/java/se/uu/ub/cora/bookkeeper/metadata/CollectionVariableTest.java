package se.uu.ub.cora.bookkeeper.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionVariableTest {
	@Test
	public void testInit() {
		CollectionVariable colVar = new CollectionVariable("id", "nameInData", "textId", "defTextId",
				"refCollection");

		Assert.assertEquals(colVar.getId(), "id", "Id should have the value set in the constructor");

		Assert.assertEquals(colVar.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		Assert.assertEquals(colVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		Assert.assertEquals(colVar.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		Assert.assertEquals(colVar.getRefCollectionId(), "refCollection",
				"RefCollectionId should have the value set in the constructor");
	}
}
