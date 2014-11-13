package wttest;

import org.junit.Assert;
import org.junit.Test;

import wackyTracky.clientbindings.java.model.DataStore;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;

public class TestDataStoreSerialization {
	private DataStore ds = new DataStore("testSerialisation");

	@Test
	public void testLoad() {
		this.ds.clear();

		Assert.assertEquals(0, this.ds.listOfLists.getCount());

		this.ds = DataStore.load();

		Assert.assertNotNull(this.ds);
//		Assert.assertEquals(2, this.ds.listOfLists.getCount());

		this.ds.println();
	}

	@Test
	public void testSave() {
		this.ds.clear();

		ItemList listGeneral = this.ds.listOfLists.add(new ItemList());
		listGeneral.title = "general";

		listGeneral.container.addItem(new Item("general one"));
		listGeneral.container.addItem(new Item("general two"));
		listGeneral.container.addItem(new Item("general three"));

		ItemList listTwo = this.ds.listOfLists.add(new ItemList());
		listTwo.title = "shopping";

		listTwo.container.addItem(new Item("shopping one"));
		listTwo.container.addItem(new Item("shopping two"));
		listTwo.container.addItem(new Item("shopping three"));

		this.ds.save();
	}
}
