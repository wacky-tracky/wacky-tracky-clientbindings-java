package wttest;

import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtResponse;
import wackyTracky.clientbindings.java.api.Session;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;
import wackyTracky.clientbindings.java.model.ListOfLists;

import com.google.gson.JsonObject;

public class TestListManagement {
	@Test
	public void testCreateDeleteList() throws Exception {
		Session session = new UnitTestingSession(true);

		WtRequest reqLists = session.reqCreateList("foo");
		reqLists.submit();
		reqLists.response().assertStatusOkAndJson();

		JsonObject res = reqLists.response().getContentJsonObject();
		int newId = res.get("newListId").getAsInt();

		ItemList itemList = session.reqGetList(newId);

		Assert.assertEquals("foo", itemList.title);

		WtRequest reqDelete = session.reqDeleteList(itemList);
		reqDelete.submit().response().assertStatusOkAndJson();
	}

	@Test
	public void testListItems() throws Exception {
		UnitTestingSession session = new UnitTestingSession(true);
		ListOfLists lol = session.getListLists();
		ItemList list = lol.getByRandom();

		Assert.assertNotNull(list);

		for (int i = 0; i < list.container.size(); i++) {
			Item item = new Item(UUID.randomUUID().toString());

			list.container.addItem(item);
		}

		session.reqListItems(list);

		System.out.println("list: " + list);

		Item root = list.container.getRandomItem();

		root.container.addItem(new Item((UUID.randomUUID().toString())));
	}

	@Test
	public void testListLists() throws Exception {
		UnitTestingSession session = new UnitTestingSession(true);

		ListOfLists lol = session.getListLists();

		System.out.println(lol);

		Assert.assertTrue(lol.hasLists());
	}

	@Test
	public void testListRename() throws Exception {
		UnitTestingSession session = new UnitTestingSession();

		ListOfLists listOfLists = session.getListLists();

		if (listOfLists.isEmpty()) {
			throw new Exception("No valid lists to test with");
		}

		int randomId = (new Random()).nextInt(listOfLists.getLists().size());

		ItemList list = listOfLists.getLists().get(randomId);

		WtResponse resp = session.reqListUpdate(list, "random-" + UUID.randomUUID()).response();

		System.out.println(resp.content);

		Assert.assertTrue(resp.isStatusOkAndJson());
	}
}
