package wackyTracky.clientbindings.java;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import wackyTracky.clientbindings.java.api.Session;

public class WtRequest {
	public enum ConnError {
		CONN_REFUSED, HTTP_404, HTTP_500, IOException;
	}

	private WtResponse resp;

	private final UriBuilder builder;
	private final Session session;

	public WtRequest(Session session, String method) {
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

		System.out.println("req: " + uri.toString());

		WtResponse res = new WtResponse(uri, this.session);

		System.out.println("res: " + res);

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
