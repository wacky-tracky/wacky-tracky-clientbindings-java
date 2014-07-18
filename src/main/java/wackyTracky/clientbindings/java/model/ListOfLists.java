package wackyTracky.clientbindings.java.model;

import java.util.Iterator;
import java.util.Vector;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ListOfLists {
	public interface Listener {
		public void fireNewList();
	}

	private final Vector<ItemList> lists = new Vector<ItemList>();

	public final Vector<Listener> listeners = new Vector<Listener>();

	public ListOfLists() {}

	public ListOfLists(JSONArray contentJson) {
		Iterator<Object> it = contentJson.iterator();

		while (it.hasNext()) {
			Object o = it.next();

			if (o instanceof JSONObject) {
				ItemList list = new ItemList((JSONObject) o);

				this.lists.add(list);
			}
		}
	}

	public ItemList add(ItemList itemList) {
		this.lists.add(itemList);

		for (Listener l : this.listeners) {
			l.fireNewList();
		}

		return itemList;
	}

	private boolean containsId(int id) {
		return this.getById(id) != null;
	}

	public ItemList getById(int id) {
		for (ItemList list : this.lists) {
			if (list.id == id) {
				return list;
			}
		}

		return null;
	}

	public Vector<ItemList> getLists() {
		return this.lists;
	}

	public boolean hasLists() {
		return !this.lists.isEmpty();
	}

	public boolean isEmpty() {
		return this.lists.isEmpty();
	}

	public void merge(ListOfLists listLists) {
		for (ItemList list : listLists.lists) {
			if (this.containsId(list.id)) {
				this.getById(list.id).merge(list);
			} else {
				this.add(list);
			}
		}
	}

	public void println() {
		System.out.println("List of lists");

		for (ItemList list : this.lists) {
			list.println();
		}
	}

	@Override
	public String toString() {
		return this.lists.toString();
	}
}
