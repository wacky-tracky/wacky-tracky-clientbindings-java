package wttest;

import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtResponse;
import wackyTracky.clientbindings.java.api.Session;
import wackyTracky.clientbindings.java.model.ItemList;
import wackyTracky.clientbindings.java.model.ListOfLists;

public class TestListManagement {
	@Test
	@Ignore
	public void testCreateDeleteList() throws Exception {
		Session session = new UnitTestingSession(true);

		WtRequest reqLists = session.reqCreateList("foo");
		reqLists.submit();
		reqLists.response().assertStatusOkAndJson();

		ItemList itemList = new ItemList(reqLists.response().getContentJsonObject());

		Assert.assertEquals("foo", itemList.title);
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
