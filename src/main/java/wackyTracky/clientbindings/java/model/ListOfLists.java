package wackyTracky.clientbindings.java.model;

import java.util.Iterator;
import java.util.Vector;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ListOfLists {
	private final Vector<ItemList> lists = new Vector<ItemList>();

	public ListOfLists(JSONArray contentJson) {
		System.out.println(contentJson);

		Iterator<Object> it = contentJson.iterator();

		while (it.hasNext()) {
			Object o = it.next();

			if (o instanceof JSONObject) {
				ItemList list = new ItemList((JSONObject) o);

				this.lists.add(list);
			}
		}
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

	@Override
	public String toString() {
		return this.lists.toString();
	}
}
