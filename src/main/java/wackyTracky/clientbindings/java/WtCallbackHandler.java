package wackyTracky.clientbindings.java;

import wackyTracky.clientbindings.java.WtRequest.ConnException;

public abstract class WtCallbackHandler {
	protected WtRequest request;

	public abstract void onException(ConnException connException);

	public abstract void onSuccess();;

	public void setRequest(WtRequest wtRequest) {
		this.request = wtRequest;
	}

	public void submit() throws ConnException {
		if (this.request != null) {
			this.request.response().assertStatusOkAndJson();
		}
	}
}
