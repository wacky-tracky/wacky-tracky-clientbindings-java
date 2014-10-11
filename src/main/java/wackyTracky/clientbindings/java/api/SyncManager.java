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
import wackyTracky.clientbindings.java.model.ItemContainer;
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

		for (ItemList list : this.datastore.listOfLists.getLists()) {
			toLocallyRemoveItems.put(list, new ArrayList<Item>());

			try {
				if (list.id == 0) {
					ItemList byTitle = this.session.reqGetListByTitle(list.title);

					if (byTitle == null) {
						list.pendingAction = PendingAction.CREATE;
					} else {
						list.id = byTitle.id;
					}

				}
			} catch (ConnException e) {

			}

			if (list.pendingAction != PendingAction.NONE) {
				try {
					switch (list.pendingAction) {
					case CREATE:
						this.create(list);
						break;
					case DELETE:
						this.deleteList(list);
						toLocallyRemove.add(list);

						break;
					case NONE:
						break;
					default:
						System.out.println("SyncManager does not handle: " + list.pendingAction);
					}
				} catch (Exception e) {
					this.exceptions.add(e);
				}
			}

			syncItemContainer(list.container);
		}

		this.datastore.listOfLists.removeAll(toLocallyRemove);

	}
	
	private void syncItemContainer(ItemContainer itemContainer) {
		for (Item i : new ArrayList<Item>(itemContainer.getItems())) {
			if (i.container.hasItems()) {    
				syncItemContainer(i.container);
			} 
			
			try {
				switch (i.pendingAction) {     
				case DELETE:
					this.session.reqDeleteItem(i).response().assertStatusOkAndJson();

					itemContainer.remove(i);
					break;
				case CREATE:  
					this.session.reqCreateItem(itemContainer.getParent(), i.content);

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
}
