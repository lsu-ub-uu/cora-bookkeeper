package se.uu.ub.cora.bookkeeper.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionItemTest {
	@Test
	public void testInit() {
		CollectionItem collectionItem = new CollectionItem("id", "nameInData", "textId", "defTextId");

		Assert.assertEquals(collectionItem.getId(), "id",
				"Id should have the value set in the constructor");

		Assert.assertEquals(collectionItem.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		Assert.assertEquals(collectionItem.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		Assert.assertEquals(collectionItem.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

	}
}
