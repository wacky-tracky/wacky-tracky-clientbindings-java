package wackyTracky.clientbindings.java.model;

import wackyTracky.clientbindings.java.ObjectFieldSerializer;

import com.google.gson.JsonObject;

public class Item {
	public int id;
	public String content;
	public PendingAction pendingAction = PendingAction.NONE;

	public ItemContainer container = new ItemContainer();

	public Item(JsonObject o) {
		this.content = o.get("content").getAsString();
		this.id = o.get("id").getAsInt();
	}

	public Item(String content2) {
		this.content = content2;
		this.pendingAction = PendingAction.CREATE;
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
