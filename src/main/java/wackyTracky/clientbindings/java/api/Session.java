package wackyTracky.clientbindings.java.api;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtRequest.ConnException;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemContainer.ItemContainerParent;
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

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(password.getBytes());
			req.addArgumentString("password", new BigInteger(1, md.digest()).toString(16));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		return req;
	}
  
	public void reqCreateItem(ItemContainerParent parent  , String content) throws Exception {
		WtRequest req = new WtRequest(this, "createTask");
		req.addArgumentInt("parentId", parent.getId());
		req.addArgumentString("parentType", parent.getServerType());
		req.addArgumentString("content", content);  
		req.submit();

		System.out.println(req.response());

		req.response().assertStatusOkAndJson();

		Item i = new Item(content);
		i.pendingAction = PendingAction.NONE;
		parent.getContainer().addItem(i); 
	}

	public WtRequest reqCreateList(String title) {
		WtRequest wtCreate = new WtRequest(this, "createList");
		wtCreate.addArgumentString("title", title);
		return wtCreate;
	}

	public void reqCreateSubItem(Item itemRoot, String content) throws ConnException {
		WtRequest req = new WtRequest(this, "createTask");
		req.addArgumentInt("parentId", itemRoot.id);
		req.addArgumentString("parentType", "item");
		req.addArgumentString("content", content);
		req.submit();

		req.response().assertStatusOkAndJson();

		Item i = new Item(content);
		itemRoot.pendingAction = PendingAction.NONE;
		itemRoot.container.addItem(i);
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

	public ItemList reqGetListByTitle(String title) throws ConnException {
		WtRequest reqGetList = new WtRequest(this, "getListByTitle");
		reqGetList.addArgumentString("listTitle", title);
		reqGetList.submit();

		reqGetList.response().assertStatusOkAndJson();

		try {
			return new ItemList(reqGetList.response().getContentJsonObject());
		} catch (Exception e) {
			return null;
		}
	}

	public void reqListItems(Item parentItem) throws ConnException {
		WtRequest reqGetListItems = new WtRequest(this, "listTasks");
		reqGetListItems.addArgumentInt("task", parentItem.id);
		reqGetListItems.addArgumentString("sort", "content");
		reqGetListItems.submit();

		reqGetListItems.response().assertStatusOkAndJson();

		System.out.println(reqGetListItems.response().getContent());

		for (int i = 0; i < reqGetListItems.response().getContentJsonArray().size(); i++) {
			JsonObject o = reqGetListItems.response().getContentJsonArray().get(i).getAsJsonObject();

			Item item = new Item(o);

			System.out.println(o);

			parentItem.container.addItem(item);
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

			l.container.addItem(item);
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
