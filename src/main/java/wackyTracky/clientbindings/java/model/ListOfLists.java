package wackyTracky.clientbindings.java.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ListOfLists implements Iterable<ItemList> {
	public interface Listener {
		public void fireNewList();

		public void onListRemoved(ItemList list);
	}

	private final ArrayList<ItemList> lists = new ArrayList<ItemList>();

	public transient final Vector<Listener> listeners = new Vector<Listener>();

	public ListOfLists() {
	}

	public ListOfLists(JsonArray contentJson) {
		Iterator<JsonElement> it = contentJson.iterator();

		while (it.hasNext()) {
			JsonElement o = it.next();

			if (o.isJsonObject()) {
				ItemList list = new ItemList((JsonObject) o);

				this.lists.add(list);
			}
		}
	}

	public ItemList add(ItemList itemList) {
		for (Iterator<ItemList> it = this.lists.iterator(); it.hasNext();) {
			ItemList existing = it.next();

			if ((existing.id == 0) && existing.title.equals(itemList.title)) {
				it.remove();
			}
		}

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

	public ItemList getByRandom() {
		int index = (new Random()).nextInt(this.lists.size());

		return this.lists.get(index);
	}

	public int getCount() {
		return this.lists.size();
	}

	public ArrayList<ItemList> getLists() {
		return this.lists;
	}

	public boolean hasLists() {
		return !this.lists.isEmpty();
	}

	public boolean isEmpty() {
		return this.lists.isEmpty();
	}

	@Override
	public Iterator<ItemList> iterator() {
		return this.lists.iterator();
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

	public void remove(ItemList list) {
		this.lists.remove(list);

		for (Listener l : this.listeners) {
			l.onListRemoved(list);
		}
	}

	public void removeAll(ArrayList<ItemList> toLocallyRemove) {
		for (ItemList toRemove : toLocallyRemove) {
			this.remove(toRemove);
		}
	}

	@Override
	public String toString() {
		return this.lists.toString();
	}
}
