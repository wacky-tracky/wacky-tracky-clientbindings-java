package wttest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

import wackyTracky.clientbindings.java.model.ItemList;

public class TestCreateItem {
	@Test
	@Ignore
	public void testCreateItem() throws Exception {
		UnitTestingSession session = new UnitTestingSession();

		ItemList l = session.reqGetList(1441);

		session.reqCreateItem(l, "test content");

		session.reqListItems(l);
		Assert.assertTrue(l.hasItems());
	}
}
