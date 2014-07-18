package wackyTracky.clientbindings.java.model;

import java.util.Vector;

import net.minidev.json.JSONObject;
import wackyTracky.clientbindings.java.ObjectFieldSerializer;

public class ItemList {
	public interface Listener {
		public void onItemAdded(Item i);

		public void onListChanged(ItemList list);

	}

	public int id;

	public String title;

	private final Vector<Item> items = new Vector<Item>();

	public Vector<Listener> listeners = new Vector<Listener>();

	public boolean existsOnServer = true;

	private int count = 0;

	public ItemList() {}

	public ItemList(JSONObject itemJson) {
		this.title = (String) itemJson.get("title");
		this.id = (Integer) itemJson.get("id");
		this.count = (Integer) itemJson.get("count");
	}

	public void add(Item item) {
		this.items.add(item);

		for (Listener l : this.listeners) {
			l.onItemAdded(item);
		}
	}

	public void clear() {
		this.items.clear();
	}

	public void fireListChanged() {
		for (Listener l : this.listeners) {
			l.onListChanged(this);
		}
	}

	public int getItemCount() {
		return this.count;
	}

	public Vector<Item> getItems() {
		return this.items;
	}

	public boolean hasItems() {
		return !this.items.isEmpty();
	}

	public void merge(ItemList foreignList) {
		this.title = foreignList.title;
		this.existsOnServer = foreignList.existsOnServer;

		this.fireListChanged();
	}

	public void println() {
		System.out.println(this);

		for (Item i : this.getItems()) {
			i.println();
		}
	}

	public int size() {
		return this.items.size();
	}

	@Override
	public String toString() {
		String list = ObjectFieldSerializer.with(this).include("title", "id").toString();
		list += " " + this.items.size();
		return list;
	}

}
