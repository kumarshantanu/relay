package kumarshantanu.relay.impl;


/**
 * A <tt>Mailbox.poll()</tt> returns parameterized <tt>PollType</tt>. A
 * PollConverter lets you extract the message and (String) correlationID
 * elements from it.
 * 
 * @author shantanu
 *
 * @param <RequestType>
 * @param <PollType>
 */
public interface PollConverter<RequestType, PollType> {

	public RequestType getMessage(PollType encoded);

	public String getCorrelationID(PollType encoded);

}
