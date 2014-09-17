package wackyTracky.clientbindings.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;

import wackyTracky.clientbindings.java.WtRequest.ConnError;
import wackyTracky.clientbindings.java.WtRequest.ConnException;
import wackyTracky.clientbindings.java.api.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	public static String userAgent = "wtClientbindingsJava";

	private final Session session;

	public WtResponse(URI uri, Session session, int reqno) {
		this.session = session;

		try {
			if (WtConnMonitor.isOffline()) {
				throw new ConnErrorException(ConnError.REQ_WHILE_OFFLINE);
			}

			this.conn = (HttpURLConnection) uri.toURL().openConnection();
			this.conn.setRequestProperty("User-Agent", userAgent);
			this.conn.setRequestMethod("GET");

			for (String cookie : session.cookieMonster) {
				this.conn.setRequestProperty("Cookie", cookie);
				System.out.println("httpconn " + reqno + " Sending cookie: " + cookie);
			}

			this.contentType = this.conn.getHeaderField("Content-Type");
			this.responseCode = this.conn.getResponseCode();

			if (this.conn.getResponseCode() != 200) {
				this.content = Util.convertStreamToString(this.conn.getErrorStream());

				if (this.isContentTypeJson()) {
					JsonObject o = this.getContentJsonObject();

					if (o.has("uniqueType")) {
						switch (o.get("uniqueType").getAsString()) {
						case "user-not-found":
							throw new ConnErrorException(ConnError.USER_NOT_FOUND);
						case "user-wrong-password":
							throw new ConnErrorException(ConnError.USER_WRONG_PASSWORD);
						}
					}
				}

				switch (this.conn.getResponseCode()) {
				case 500:
					throw new ConnErrorException(ConnError.HTTP_500);
				case 403:
					throw new ConnErrorException(ConnError.HTTP_403);
				default:
					throw new IOException("Unhandled response code: " + this.conn.getResponseCode());
				}
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

			this.err = ConnError.IOException;
		} catch (ConnErrorException e) {
			this.err = e.err;
		}

		WtConnMonitor.updateStatus(this.err, reqno);
	}

	public void assertStatusOkAndJson() throws ConnException {
		if (!this.isStatusOkAndJson()) {
			throw new ConnException(this);
		}
	}

	public String getContent() {
		return this.content;
	}

	public JsonArray getContentJsonArray() {
		JsonElement a = this.getContentJsonElement();

		if (a.isJsonArray()) {
			return (JsonArray) a;
		} else {
			return null;
		}
	}

	public JsonElement getContentJsonElement() {
		JsonParser parser = new JsonParser();
		JsonElement a = parser.parse(this.content);

		return a;
	}

	public JsonObject getContentJsonObject() {
		JsonElement a = this.getContentJsonElement();

		if (a.isJsonObject()) {
			return (JsonObject) a;
		} else {
			return null;
		}
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
