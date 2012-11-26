package kumarshantanu.relay.impl;

import kumarshantanu.relay.Callback;

public class DummyCallback<ReturnType> implements Callback<ReturnType> {

	public void onReturn(ReturnType value) {
		// do nothing
	}

	public void onException(Exception ex) {
		ex.printStackTrace();
	}

}
