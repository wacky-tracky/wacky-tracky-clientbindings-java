package wackyTracky.clientbindings.java.model;

import net.minidev.json.JSONObject;

public class Item {
	public String content;

	public Item(JSONObject o) {
		this.content = (String) o.get("content");
	}

	public Item(String content2) {
		this.content = content2;
	}
}
