package wttest;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import wackyTracky.clientbindings.java.WtRequest;
import wackyTracky.clientbindings.java.WtResponse;
import wackyTracky.clientbindings.java.api.Session;

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
		Assert.assertEquals("unittest", req.response().getContentJsonObject().get("username"));

	}

	@Test
	public void testInit() throws Exception {
		Session session = new Session();
		WtRequest reqInit = session.init();

		reqInit.submit();
		Assert.assertTrue(reqInit.response().isContentTypeJson());
		Assert.assertNull(reqInit.response().getContentJsonObject().get("username"));

		WtResponse respLogin = session.reqAuthenticate("unittest", "unittest").submit().response();
		Assert.assertTrue(respLogin.isStatusOk());

		reqInit.submit();
		JSONObject init = reqInit.response().getContentJsonObject();

		Assert.assertTrue(reqInit.response().isStatusOk());
		Assert.assertTrue(reqInit.response().isContentTypeJson());
		Assert.assertEquals("unittest", init.get("username"));

		WtResponse respLogout = session.logout().submit().response();

		Assert.assertTrue(respLogout.isStatusOk());

		WtResponse respInit2 = session.init().response();
		JSONObject init2 = respInit2.getContentJsonObject();

		Assert.assertNull(init2.get("username"));
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
