package kumarshantanu.relay.impl;

import java.io.Serializable;

import kumarshantanu.relay.Callback;

public class MsgCallback<RequestType, ReturnType> implements Serializable {

	/**
	 * serialVersionUUID
	 */
	private static final long serialVersionUID = -3899549514857623740L;

	public final RequestType message;
	public final Callback<ReturnType> handler;

	public MsgCallback(RequestType message, Callback<ReturnType> handler) {
		this.message = message;
		this.handler = handler;
	}

}
