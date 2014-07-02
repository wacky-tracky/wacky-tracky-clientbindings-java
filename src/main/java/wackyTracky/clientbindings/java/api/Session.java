package wackyTracky.clientbindings.java.api;

import java.util.Vector;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;
import wackyTracky.clientbindings.java.model.ListOfLists;

public class Session {
	public final Vector<String> cookieMonster = new Vector<String>();
	private final String serverAddress;

	public Session() {
		this.serverAddress = "hosted.wacky-tracky.com";
	}

	public Session(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public ListOfLists getListLists() throws Exception {
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

	public WtRequest reqAuthenticate(String username, String password) throws Exception {
		WtRequest req = new WtRequest(this, "authenticate");
		req.addArgumentString("username", username);
		req.addArgumentString("password", password);

		return req;
	}

	public WtRequest reqCreateList(String title) {
		WtRequest wtCreate = new WtRequest(this, "createList");
		wtCreate.addArgumentString("title", title);
		return wtCreate;
	}

	public void reqListItems(ItemList l) {
		WtRequest reqGetListItems = new WtRequest(this, "listTasks");
		reqGetListItems.addArgumentInt("list", l.id);
		reqGetListItems.addArgumentString("sort", "content");
		reqGetListItems.submit();

		l.clear();

		System.out.println(reqGetListItems.response().getContent());

		try {
			for (int i = 0; i < reqGetListItems.response().getContentJsonArray().size(); i++) {
				JSONObject o = (JSONObject) reqGetListItems.response().getContentJsonArray().get(i);

				Item item = new Item(o);

				l.add(item);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WtRequest reqListLists() {
		WtRequest req = new WtRequest(this, "listLists");

		return req;
	}
}
