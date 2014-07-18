package wackyTracky.clientbindings.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import wackyTracky.clientbindings.java.WtRequest.ConnError;
import wackyTracky.clientbindings.java.WtRequest.ConnException;
import wackyTracky.clientbindings.java.api.Session;

public class WtResponse {
	private class ConnErrorException extends Exception {
		public ConnError err;

		public ConnErrorException(ConnError e) {
			this.err = e;
		}
	}

	public int responseCode = -1;
	public String content;
	public ConnError err;
	public String contentType = "undefined";
	private HttpURLConnection conn = null;

	private final Session session;

	public WtResponse(URI uri, Session session, int connid) {
		this.session = session;

		try {
			if (WtConnMonitor.isOffline()) {
				throw new ConnErrorException(ConnError.REQDURINGFORCEOFFLINE);
			}

			this.conn = (HttpURLConnection) uri.toURL().openConnection();
			this.conn.setRequestMethod("GET");

			for (String cookie : session.cookieMonster) {
				this.conn.setRequestProperty("Cookie", cookie);
				System.out.println("httpconn " + connid + " Sending cookie: " + cookie);
			}

			this.contentType = this.conn.getHeaderField("Content-Type");
			this.responseCode = this.conn.getResponseCode();

			if (this.conn.getResponseCode() != 200) {
				this.content = Util.convertStreamToString(this.conn.getErrorStream());
			} else {
				this.content = Util.convertStreamToString(this.conn.getInputStream());
			}
		} catch (FileNotFoundException e) {
			this.err = ConnError.HTTP_404;
		} catch (ConnectException e) {
			this.err = ConnError.CONN_REFUSED;
		} catch (UnknownHostException e) {
			this.err = ConnError.UNKNOWN_HOST_DNS;
		} catch (IOException e) {
			System.out.println(e);
			if (e.getMessage().contains("code: 500")) {
				try {
					this.content = (String) this.conn.getContent();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.err = ConnError.HTTP_500;

			} else {
				this.err = ConnError.IOException;
			}
		} catch (ConnErrorException e) {
			this.err = e.err;
		}

		WtConnMonitor.updateStatus(this.err);
	}

	public void assertStatusOkAndJson() throws ConnException {
		if (!this.isStatusOkAndJson()) {
			throw new ConnException(this);
		}
	}

	public String getContent() {
		return this.content;
	}

	public JSONArray getContentJsonArray() throws ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(this.content);

		return a;
	}

	public JSONObject getContentJsonObject() throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(this.content);

		return o;
	}

	public String getContentType() {
		return this.contentType;
	}

	public boolean isContentTypeJson() {
		return this.getContentType().equals("application/json");
	}

	public boolean isStatusOk() {
		return this.responseCode == 200;
	}

	public boolean isStatusOkAndJson() {
		return this.isStatusOk() && this.getContentType().equals("application/json");
	}

	public void saveCookiesInSession() {
		for (String cookie : this.conn.getHeaderFields().get("Set-Cookie")) {
			this.session.cookieMonster.add(cookie);
			System.out.println("saving cookie: " + cookie);
		}
	}

	@Override
	public String toString() {
		return new ObjectFieldSerializer(this).include("status").toString();
	}
}
