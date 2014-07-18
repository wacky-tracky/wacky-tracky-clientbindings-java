package wttest;

import org.junit.Test;

import wackyTracky.clientbindings.java.model.DataStore;
import wackyTracky.clientbindings.java.model.ItemList;

public class TestDataStoreSave {
	@Test
	public void test() {
		DataStore.instance.clear();
		ItemList listGeneral = DataStore.instance.listOfLists.add(new ItemList());
		listGeneral.title = "general";

		DataStore.instance.save();
	}
}
