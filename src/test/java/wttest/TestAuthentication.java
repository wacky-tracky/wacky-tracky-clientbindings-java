package wttest;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtResponse;
import wackyTracky.clientbindings.java.api.Session;

import com.google.gson.JsonObject;

public class TestAuthentication {
	@BeforeClass
	public static void setupCookieManager() {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}

	@Test
	public void testAuthentication() throws Exception {
		Session session = new Session();
		WtRequest req = session.reqAuthenticate("unittest", "unittest");
		req.go();

		Assert.assertEquals("http://hosted.wacky-tracky.com:8082/authenticate?username=unittest&password=unittest", req.toString());
		Assert.assertNull(req.response().err);
		Assert.assertTrue(req.response().isContentTypeJson());
		Assert.assertEquals("unittest", req.response().getContentJsonObject().get("username").getAsString());

	}

	@Test
	public void testInit() throws Exception {
		Session session = new Session();
		WtRequest reqInit = session.init();

		reqInit.submit();
		Assert.assertTrue(reqInit.response().isContentTypeJson());
		Assert.assertTrue(reqInit.response().getContentJsonObject().has("username"));
		Assert.assertTrue(reqInit.response().getContentJsonObject().get("username").isJsonNull());

		WtResponse respLogin = session.reqAuthenticate("unittest", "unittest").submit().response();
		Assert.assertTrue(respLogin.isStatusOk());

		reqInit.submit();
		JsonObject init = reqInit.response().getContentJsonObject();

		Assert.assertTrue(reqInit.response().isStatusOk());
		Assert.assertTrue(reqInit.response().isContentTypeJson());
		Assert.assertEquals("unittest", init.get("username").getAsString());

		WtResponse respLogout = session.logout().submit().response();

		Assert.assertTrue(respLogout.isStatusOk());

		WtResponse respInit2 = session.init().response();
		JsonObject init2 = respInit2.getContentJsonObject();

		Assert.assertTrue(init2.get("username").isJsonNull());
	}

	@Test
	public void testLogout() {

	}

	@Test
	@Ignore
	public void testRegister() {
		Session session = new Session();
		WtRequest req = session.register("unittest", "unittest@localhost.localdomain", "unittest");

		System.out.println(req.response());
		Assert.assertTrue(req.response().isStatusOk());
	}
}
