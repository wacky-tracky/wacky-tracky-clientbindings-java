package wackyTracky.clientbindings.java.model;

import java.util.Iterator;
import java.util.Vector;

import wackyTracky.clientbindings.java.ObjectFieldSerializer;

import com.google.gson.JsonObject;

public class ItemList {
	public interface Listener {
		public void onItemAdded(Item i);

		public void onItemRemoved(Item i);

		public void onListChanged(ItemList list);

	}

	public int id;

	public String title;

	private final Vector<Item> items = new Vector<Item>();

	public transient Vector<Listener> listeners = new Vector<Listener>();

	public PendingAction pendingAction = PendingAction.NONE;

	public ItemList() {
		this.pendingAction = PendingAction.CREATE;
	}

	public ItemList(JsonObject itemJson) {
		this.title = itemJson.get("title").getAsString();
		this.id = itemJson.get("id").getAsInt();
	}

	public void add(Item item) {
		for (Iterator<Item> it = this.items.iterator(); it.hasNext();) {
			// remove local-only items (id=0) with duplicate content
			Item existing = it.next();

			if ((existing.id == 0) && existing.content.equals(item.content)) {
				it.remove();
				continue;
			}

			if (existing.id == item.id) {
				if (existing.pendingAction == PendingAction.NONE) {
					existing.merge(item);
					existing.pendingAction = PendingAction.NONE;

					for (Listener l : this.listeners) {
						l.onItemAdded(item);
					}

					return;
				}
			}
		}

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
		return this.items.size();
	}

	public Vector<Item> getItems() {
		return this.items;
	}

	public boolean hasItems() {
		return !this.items.isEmpty();
	}

	public void merge(ItemList foreignList) {
		this.title = foreignList.title;
		this.pendingAction = foreignList.pendingAction;

		this.fireListChanged();
	}

	public void println() {
		System.out.println(this);

		for (Item i : this.getItems()) {
			i.println();
		}
	}

	public void remove(Item i) {
		this.items.remove(i);

		for (Listener l : this.listeners) {
			l.onItemRemoved(i);
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
