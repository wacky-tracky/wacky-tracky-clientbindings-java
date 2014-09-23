package wttest;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;

public class TestCreateItem {
	@Test
	public void testCreateItem() throws Exception {
		UnitTestingSession session = new UnitTestingSession();

		ItemList l = session.getListLists().getByRandom();

		session.reqCreateItem(l, "test content " + UUID.randomUUID().toString());

		session.reqListItems(l);
		Assert.assertTrue(l.container.hasItems());
	}

	@Test
	public void testCreateSubitem() throws Exception {
		UnitTestingSession session = new UnitTestingSession(true);

		ItemList listRandom = session.getListLists().getByRandom();
		session.reqListItems(listRandom);

		Item itemRandomRoot = listRandom.container.getRandomItem();

		session.reqCreateSubItem(itemRandomRoot, "subitem-" + UUID.randomUUID().toString());
		session.reqListItems(itemRandomRoot);

		System.out.println(listRandom);
	}
}
