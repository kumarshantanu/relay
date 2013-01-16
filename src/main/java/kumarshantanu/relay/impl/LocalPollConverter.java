package kumarshantanu.relay.impl;


public class LocalPollConverter<RequestType> implements PollConverter<RequestType, RequestType> {

	public RequestType getMessage(RequestType message) {
		return message;
	}

	public String getCorrelationID(RequestType message) {
		throw new UnsupportedOperationException(
				"Correlation-ID not supported on this poll-converter");
	}

}
