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
		Assert.assertEquals(2, this.ds.listOfLists.getCount());

		this.ds.println();
	}

	@Test
	public void testSave() {
		this.ds.clear();

		ItemList listGeneral = this.ds.listOfLists.add(new ItemList());
		listGeneral.title = "general";

		listGeneral.add(new Item("general one"));
		listGeneral.add(new Item("general two"));
		listGeneral.add(new Item("general three"));

		ItemList listTwo = this.ds.listOfLists.add(new ItemList());
		listTwo.title = "shopping";

		listTwo.add(new Item("shopping one"));
		listTwo.add(new Item("shopping two"));
		listTwo.add(new Item("shopping three"));

		this.ds.save();
	}
}
