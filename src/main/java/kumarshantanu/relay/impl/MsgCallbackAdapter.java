package kumarshantanu.relay.impl;

import kumarshantanu.relay.Callback;

public class MsgCallbackAdapter<RequestType, ReturnType>
implements MsgAdapter<RequestType, ReturnType, MsgCallback<RequestType, ReturnType>> {
	public MsgCallback<RequestType,ReturnType> convert(RequestType request, Callback<ReturnType> callback) {
		return new MsgCallback<RequestType, ReturnType>(request, callback);
	};
}