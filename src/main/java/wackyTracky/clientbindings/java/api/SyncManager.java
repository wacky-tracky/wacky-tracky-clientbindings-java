package wackyTracky.clientbindings.java.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import wackyTracky.clientbindings.java.WtConnMonitor;
import wackyTracky.clientbindings.java.WtRequest.ConnError;
import wackyTracky.clientbindings.java.WtRequest.ConnException;
import wackyTracky.clientbindings.java.WtResponse;
import wackyTracky.clientbindings.java.model.DataStore;
import wackyTracky.clientbindings.java.model.Item;
import wackyTracky.clientbindings.java.model.ItemList;
import wackyTracky.clientbindings.java.model.PendingAction;

import com.google.gson.JsonObject;

public class SyncManager {
	private final DataStore datastore;
	private final Session session;

	private final Vector<Exception> exceptions = new Vector<Exception>();

	public SyncManager(DataStore ds, Session session) {
		this.datastore = ds;
		this.session = session;
	}

	private void create(ItemList l) throws Exception {
		WtResponse resCreate = this.session.reqCreateList(l.title).response();
		resCreate.assertStatusOkAndJson();

		JsonObject o = resCreate.getContentJsonObject();

		ItemList serverVersion = this.session.reqGetList(Integer.parseInt(o.get("newListId").toString()));
		l.merge(serverVersion);
	}

	private void deleteList(ItemList list) throws Exception {
		WtResponse respDel = this.session.reqDeleteList(list).response();
		respDel.assertStatusOkAndJson();
	}

	public void syncNow() {
		if (WtConnMonitor.isOffline()) {
			return;
		}

		ArrayList<ItemList> toLocallyRemove = new ArrayList<ItemList>();
		HashMap<ItemList, ArrayList<Item>> toLocallyRemoveItems = new HashMap<ItemList, ArrayList<Item>>();

		for (ItemList l : this.datastore.listOfLists.getLists()) {
			toLocallyRemoveItems.put(l, new ArrayList<Item>());

			try {
				if (l.id == 0) {
					ItemList byTitle = this.session.reqGetListByTitle(l.title);

					if (byTitle == null) {
						l.pendingAction = PendingAction.CREATE;
					} else {
						l.id = byTitle.id;
					}

				}
			} catch (ConnException e) {
				continue;
			}

			if (l.pendingAction != PendingAction.NONE) {
				try {
					switch (l.pendingAction) {
					case CREATE:
						this.create(l);
						break;
					case DELETE:
						this.deleteList(l);
						toLocallyRemove.add(l);

						break;
					case NONE:
						break;
					default:
						System.out.println("SyncManager does not handle: " + l.pendingAction);
					}
				} catch (Exception e) {
					this.exceptions.add(e);
				}
			}

			for (Item i : new ArrayList<Item>(l.getItems())) {
				try {
					switch (i.pendingAction) {
					case DELETE:
						this.session.reqDeleteItem(i).response().assertStatusOkAndJson();

						l.remove(i);
						break;
					case CREATE:
						this.session.reqCreateItem(l, i.content);

						break;
					case NONE:
						break;
					default:
						System.out.println("Unsupported pending action: " + i.pendingAction);
					}
				} catch (ConnException e) {
					if (e.isOneOf(ConnError.REQ_WHILE_OFFLINE)) {
						continue;
					} else {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		this.datastore.listOfLists.removeAll(toLocallyRemove);

	}
}
