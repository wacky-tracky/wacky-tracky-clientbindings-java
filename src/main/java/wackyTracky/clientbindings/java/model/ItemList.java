package wackyTracky.clientbindings.java.model;

import wackyTracky.clientbindings.java.model.ItemContainer.ItemContainerParent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ItemList implements ItemContainerParent {
	public int id;

	public String title;

	public transient final ItemContainer container;

	public PendingAction pendingAction = PendingAction.NONE;

	public ItemList() {
		this.container = new ItemContainer(this);
		this.pendingAction = PendingAction.CREATE;
	}

	public ItemList(JsonObject itemJson) {
		this.container = new ItemContainer(this);
		this.title = itemJson.get("title").getAsString();
		this.id = itemJson.get("id").getAsInt();
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
		return "list";
	}

	public void merge(ItemList foreignList) {
		this.title = foreignList.title;
		this.pendingAction = foreignList.pendingAction;

		this.container.fireListChanged();
	}

	public void println() {
		System.out.println(this);

		for (Item i : this.container.getItems()) {
			i.println();
		}
	}

	@Override
	public String toString() {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		return g.toJson(this);
	}
}
