package wackyTracky.clientbindings.java.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ItemList {

	public int id;

	public String title;

	public final ItemContainer container = new ItemContainer();

	public PendingAction pendingAction = PendingAction.NONE;

	public ItemList() {
		this.pendingAction = PendingAction.CREATE;
	}

	public ItemList(JsonObject itemJson) {
		this.title = itemJson.get("title").getAsString();
		this.id = itemJson.get("id").getAsInt();
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
