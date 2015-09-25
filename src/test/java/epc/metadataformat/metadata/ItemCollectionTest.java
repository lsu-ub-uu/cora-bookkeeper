package epc.metadataformat.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ItemCollectionTest {
	@Test
	public void testInit() {

		ItemCollection itemCollection = new ItemCollection("id", "nameInData", "textId", "defTextId");
		itemCollection.addItemReference("item1Ref");
		Assert.assertEquals(itemCollection.getId(), "id",
				"Id should have the value set in the constructor");

		Assert.assertEquals(itemCollection.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		Assert.assertEquals(itemCollection.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		Assert.assertEquals(itemCollection.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		Assert.assertNotNull(itemCollection.getCollectionItemReferences(),
				"CollectionItemReferences should not be null");

	}
}
