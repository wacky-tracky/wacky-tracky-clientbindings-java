package wackyTracky.clientbindings.java.model;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class ItemContainer {
	public interface Listener {
		public void onItemAdded(Item i);

		public void onItemRemoved(Item i);

		public void onListChanged();

	}
	
	public interface ItemContainerParent {

		int getId();
		String getServerType();
		ItemContainer getContainer(); 
	}

	private ItemContainerParent parent;
	 
	public ItemContainer(Item parent) {
		this.parent = parent;
	}
	
	public ItemContainer(ItemList parent) {
		this.parent = parent; 
	}

	private final Vector<Item> items = new Vector<Item>();

	public final transient Vector<Listener> listeners = new Vector<Listener>();

	public void addItem(Item item) {
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
			l.onListChanged();
		}
	}

	public int getItemCount() {
		return this.items.size();
	}

	public Vector<Item> getItems() {
		return this.items;
	}

	public Item getRandomItem() {
		if (this.items.isEmpty()) {
			throw new IllegalArgumentException("no items in container");
		}

		int index = (new Random()).nextInt(this.items.size());

		return this.items.get(index);
	}

	public boolean hasItems() {
		return !this.items.isEmpty();
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
 
	public ItemContainerParent getParent() {
		return null;
	}
}
