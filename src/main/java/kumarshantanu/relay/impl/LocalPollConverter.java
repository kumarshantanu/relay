package kumarshantanu.relay.impl;

import kumarshantanu.relay.CorrelatedMessage;

public class LocalPollConverter<RequestType>
implements PollConverter<RequestType, CorrelatedMessage<RequestType>> {

	public RequestType getMessage(CorrelatedMessage<RequestType> encoded) {
		return encoded.message;
	}

	public String getCorrelationID(
			CorrelatedMessage<RequestType> encoded) {
		return encoded.correlationID;
	}

}
