package kumarshantanu.relay.impl;


public interface PollConverter<RequestType, PollType> {

	public RequestType getMessage(PollType encoded);

	public String getCorrelationID(PollType encoded);

}
