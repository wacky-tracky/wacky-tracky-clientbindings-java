package wackyTracky.clientbindings.java;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import wackyTracky.clientbindings.java.api.Session;

public class WtRequest {
	public enum ConnError {
		CONN_REFUSED, HTTP_404, HTTP_500, IOException, UNKNOWN_HOST_DNS("Could not find host in DNS"), REQ_WHILE_OFFLINE("Tried to make a HTTP request while offline mode is set."), HTTP_403;

		public final String description;

		private ConnError() {
			this.description = this.toString();
		}

		private ConnError(String description) {
			this.description = description;
		}
	}

	public static class ConnException extends Exception {
		private final WtResponse resp;

		public ConnException(WtResponse wtResponse) {
			this.resp = wtResponse;
		}

		public String getDescription() {
			return this.resp.err.description;
		}

		public boolean isOneOf(ConnError... possibleCauses) {
			for (ConnError possibleCause : possibleCauses) {
				if (possibleCause == this.resp.err) {
					return true;
				}
			}

			return false;
		}

		public void println() {
			System.err.println(this.toString());
		}

		@Override
		public String toString() {
			ObjectFieldSerializer ofs = new ObjectFieldSerializer(this.resp);
			ofs.include("status");
			ofs.include("err");

			return "ConnException: " + ofs.toString();
		}
	}

	private WtResponse resp;

	private final UriBuilder builder;
	private final Session session;

	private static int lastConnId = 0;
	private final int connId;

	public WtRequest(Session session, String method) {
		this.connId = WtRequest.lastConnId++;

		this.session = session;
		this.builder = UriBuilder.fromPath(method);
		this.builder.port(session.getPort());
		this.builder.host(session.getServerAddress());
		this.builder.scheme("http");
	}

	public void addArgumentInt(String string, int id) {
		this.addArgumentString(string, Integer.toString(id));
	}

	public void addArgumentString(String key, String val) {
		this.builder.queryParam(key, val);
	}

	public WtResponse go() {
		URI uri = this.builder.build();

		System.out.println("httpconn " + this.connId + " req: " + uri.toString());

		WtResponse res = new WtResponse(uri, this.session, this.connId);

		System.out.println("httpconn " + this.connId + " res: " + res);

		return res;
	}

	public WtResponse response() {
		if (this.resp == null) {
			this.submit();
		}

		return this.resp;
	}

	public WtRequest submit() {
		this.resp = this.go();

		return this;
	}

	@Override
	public String toString() {
		return this.builder.build().toString();
	}

}
