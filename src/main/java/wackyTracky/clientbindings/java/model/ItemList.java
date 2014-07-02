package wackyTracky.clientbindings.java.model;

import java.util.Vector;

import net.minidev.json.JSONObject;
import wackyTracky.clientbindings.java.ObjectFieldSerializer;

public class ItemList {
	public int id;
	public String title;

	private final Vector<Item> items = new Vector<Item>();

	public ItemList() {}

	public ItemList(JSONObject itemJson) {
		this.title = (String) itemJson.get("title");
		this.id = (Integer) itemJson.get("id");
	}

	public void add(Item item) {
		this.items.add(item);
	}

	public void clear() {
		this.items.clear();
	}

	public Vector<Item> getItems() {
		return this.items;
	}

	public boolean hasItems() {
		return !this.items.isEmpty();
	}

	public int size() {
		return this.items.size();
	}

	@Override
	public String toString() {
		return ObjectFieldSerializer.with(this).include("title", "id").toString();
	}
}
