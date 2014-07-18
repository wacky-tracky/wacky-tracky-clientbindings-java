package wackyTracky.clientbindings.java.model;

import net.minidev.json.JSONObject;
import wackyTracky.clientbindings.java.ObjectFieldSerializer;

public class Item {
	public String content;

	public Item(JSONObject o) {
		this.content = (String) o.get("content");
	}

	public Item(String content2) {
		this.content = content2;
	}

	public void println() {
		System.out.println("\t" + this);
	}

	@Override
	public String toString() {
		ObjectFieldSerializer o = new ObjectFieldSerializer(this);
		o.include("content");

		return o.toString();
	}
}
