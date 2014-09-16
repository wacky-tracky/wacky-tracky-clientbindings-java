package wackyTracky.clientbindings.java.api;

import java.util.Vector;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtRequest.ConnException;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;
import wackyTracky.clientbindings.java.model.ListOfLists;
import wackyTracky.clientbindings.java.model.PendingAction;

import com.google.gson.JsonObject;

public class Session {
	public final Vector<String> cookieMonster = new Vector<String>();
	private final String serverAddress;

	public Session() {
		this.serverAddress = "hosted.wacky-tracky.com";
	}

	public Session(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public ListOfLists getListLists() throws ConnException {
		WtRequest reqListLists = this.reqListLists();
		reqListLists.response().assertStatusOkAndJson();

		ListOfLists lol = new ListOfLists(reqListLists.response().getContentJsonArray());

		return lol;
	}

	public int getPort() {
		return 8082;
	}

	public String getServerAddress() {
		return this.serverAddress;
	}

	public WtRequest init() {
		WtRequest req = new WtRequest(this, "init");

		return req;
	}

	public WtRequest logout() {
		WtRequest req = new WtRequest(this, "logout");

		return req;
	}

	public WtRequest register(String username, String email, String password) {
		WtRequest req = new WtRequest(this, "register");
		req.addArgumentString("username", username);
		req.addArgumentString("email", email);
		req.addArgumentString("password", password);

		return req.submit();
	}

	public WtRequest reqAuthenticate(String username, String password) {
		WtRequest req = new WtRequest(this, "authenticate");
		req.addArgumentString("username", username);
		req.addArgumentString("password", password);

		return req;
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
		i.pendingAction = PendingAction.NONE;
		l.add(i);
	}

	public WtRequest reqCreateList(String title) {
		WtRequest wtCreate = new WtRequest(this, "createList");
		wtCreate.addArgumentString("title", title);
		return wtCreate;
	}

	public WtRequest reqDeleteItem(Item i) {
		WtRequest req = new WtRequest(this, "deleteTask");
		req.addArgumentInt("id", i.id);

		return req;
	}

	public WtRequest reqDeleteList(ItemList list) {
		WtRequest req = new WtRequest(this, "deleteList");
		req.addArgumentInt("id", list.id);

		return req;
	}

	public ItemList reqGetList(int listId) throws ConnException {
		WtRequest reqGetList = new WtRequest(this, "getList");
		reqGetList.addArgumentString("listId", Integer.toString(listId));
		reqGetList.submit();

		reqGetList.response().assertStatusOkAndJson();

		try {
			return new ItemList(reqGetList.response().getContentJsonObject());
		} catch (Exception e) {
			return null;
		}
	}

	public void reqListItems(ItemList l) throws ConnException {
		WtRequest reqGetListItems = new WtRequest(this, "listTasks");
		reqGetListItems.addArgumentInt("list", l.id);
		reqGetListItems.addArgumentString("sort", "content");
		reqGetListItems.submit();

		reqGetListItems.response().assertStatusOkAndJson();

		System.out.println(reqGetListItems.response().getContent());

		for (int i = 0; i < reqGetListItems.response().getContentJsonArray().size(); i++) {
			JsonObject o = reqGetListItems.response().getContentJsonArray().get(i).getAsJsonObject();

			Item item = new Item(o);

			System.out.println(o);

			l.add(item);
		}
	}

	public WtRequest reqListLists() {
		WtRequest req = new WtRequest(this, "listLists");

		return req;
	}

	public WtRequest reqListUpdate(ItemList list, String newName) {
		WtRequest req = new WtRequest(this, "listUpdate");
		req.addArgumentInt("list", list.id);
		req.addArgumentString("title", newName);
		req.addArgumentString("sort", "");
		req.addArgumentString("timeline", "false");

		return req;
	}
}
