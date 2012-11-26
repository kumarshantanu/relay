package kumarshantanu.relay.impl;

import kumarshantanu.relay.Callback;

public interface MsgAdapter<RequestType, ReturnType, MailboxType> {

	public MailboxType convert(RequestType request, Callback<ReturnType> callback);

}