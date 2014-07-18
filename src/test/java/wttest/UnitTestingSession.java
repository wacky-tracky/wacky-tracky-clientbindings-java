package wttest;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.api.Session;

public class UnitTestingSession extends Session {

	public UnitTestingSession() throws Exception {
		this(true);
	}

	public UnitTestingSession(boolean authenticate) throws Exception {
		if (authenticate) {
			WtRequest reqAuthenticate = this.reqAuthenticate();
			reqAuthenticate.response().assertStatusOkAndJson();
			reqAuthenticate.response().saveCookiesInSession();
		}
	}

	public WtRequest reqAuthenticate() throws Exception {
		return super.reqAuthenticate("unittest", "unittest");
	}

}
