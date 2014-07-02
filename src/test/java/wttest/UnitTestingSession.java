package wttest;

import net.minidev.json.parser.ParseException;
import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.api.Session;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;

public class UnitTestingSession extends Session {

	public UnitTestingSession() throws Exception {
		this(true);
	}

	public UnitTestingSession(boolean authenticate) throws Exception {
		if (authenticate) {
			WtRequest reqAuthenticate = this.reqAuthenticate();
			reqAuthenticate.response().assertStatusOkAndJson();
			reqAuthenticate.response().saveCookiesInSession();
		}
	}

	public WtRequest reqAuthenticate() throws Exception {
		return super.reqAuthenticate("unittest", "unittest");
	}

	public void reqCreateItem(ItemList l, String content) throws Exception {
		WtRequest req = new WtRequest(this, "createTask");
		req.addArgumentInt("parentId", l.id);
		req.addArgumentString("parentType", "list");
		req.addArgumentString("content", content);
		req.submit();

		System.out.println(req.response());

		req.response().assertStatusOkAndJson();

		Item i = new Item(content);
		l.add(i);
	}

	public ItemList reqGetList(int listId) {
		WtRequest reqGetList = new WtRequest(this, "getList");
		reqGetList.addArgumentString("listId", Integer.toString(listId));
		reqGetList.submit();

		System.out.println(reqGetList.response().getContent());

		try {
			return new ItemList(reqGetList.response().getContentJsonObject());
		} catch (ParseException e) {
			return null;
		}
	}

}
