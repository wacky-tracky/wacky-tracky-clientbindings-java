package wackyTracky.clientbindings.java;

import java.util.Vector;

import wackyTracky.clientbindings.java.WtRequest.ConnError;

public class WtConnMonitor {
	public static interface Listener {
		void onError(ConnError err);

		void onOk();
	}

	public static ConnError lastError = null;
	public static Vector<Listener> listeners = new Vector<Listener>();

	private static boolean offline = false;

	public static void goOffline() {
		WtConnMonitor.offline = true;
	}

	public static boolean isOffline() {
		return false;
	}

	public static void toggleForceOffline() {
		WtConnMonitor.offline = !WtConnMonitor.offline;
	}

	public static String toStaticString() {
		ObjectFieldSerializer serializer = new ObjectFieldSerializer(WtConnMonitor.class);
		serializer.include("lastError", "forceOffline");

		return serializer.toString();
	}

	public static void updateStatus(ConnError err) {
		System.out.println("connMon: " + WtConnMonitor.toStaticString());

		for (Listener l : WtConnMonitor.listeners) {
			if (err == null) {
				l.onOk();
			} else {
				l.onError(err);
			}
		}

		WtConnMonitor.lastError = err;
	}
}
