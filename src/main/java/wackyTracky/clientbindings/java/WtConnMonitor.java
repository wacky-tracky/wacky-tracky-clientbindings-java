package wackyTracky.clientbindings.java;

import java.util.Vector;

import wackyTracky.clientbindings.java.WtRequest.ConnError;

public class WtConnMonitor {
	public static interface Listener {
		void onError(ConnError err, int reqno);

		void onOk();
	}

	public static ConnError lastError = null;
	public transient static Vector<Listener> listeners = new Vector<Listener>();

	public static boolean offline = false;

	public static void goOffline() {
		WtConnMonitor.offline = true;
	}

	public static boolean isOffline() {
		return offline;
	}

	public static void toggleForceOffline() {
		WtConnMonitor.offline = !WtConnMonitor.offline;
		System.out.println("Offline is now: " + offline);
	}

	public static String toStaticString() {
		ObjectFieldSerializer serializer = new ObjectFieldSerializer(WtConnMonitor.class);
		serializer.include("lastError", "forceOffline");

		return serializer.toString();
	}

	public static void updateStatus(ConnError err, int reqno) {
		System.out.println("connMon: " + WtConnMonitor.toStaticString());

		for (Listener l : WtConnMonitor.listeners) {
			if (err == null) {
				l.onOk();
			} else {
				l.onError(err, reqno);
			}
		}

		WtConnMonitor.lastError = err;
	}
}
