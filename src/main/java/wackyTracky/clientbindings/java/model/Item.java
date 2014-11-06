package wackyTracky.clientbindings.java.model;

import wackyTracky.clientbindings.java.ObjectFieldSerializer;
import wackyTracky.clientbindings.java.model.ItemContainer.ItemContainerParent;

import com.google.gson.JsonObject;

public class Item implements ItemContainerParent {
	public int id;
	public String content;
	public PendingAction pendingAction = PendingAction.NONE;

	public ItemContainer container;

	public Item(JsonObject o) {
		this.container = new ItemContainer(this);
		this.content = o.get("content").getAsString();
		this.id = o.get("id").getAsInt();
	}

	public Item(String content2) {
		this.container = new ItemContainer(this);
		this.content = content2;
		this.pendingAction = PendingAction.CREATE;
	}

	@Override
	public ItemContainer getContainer() {
		return this.container;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getServerType() {
		return "item";
	}

	public void merge(Item item) {
		this.content = item.content;
	}

	public void println() {
		System.out.println("\t" + this);
	}

	@Override
	public String toString() {
		ObjectFieldSerializer o = new ObjectFieldSerializer(this);
		o.include("content");
		o.include("id");

		return o.toString();
	}
}
