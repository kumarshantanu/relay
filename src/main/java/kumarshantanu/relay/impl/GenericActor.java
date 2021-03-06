package kumarshantanu.relay.impl;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;

public abstract class GenericActor<RequestType, PollType> extends AbstractActor<RequestType> {

	public final AbstractMailbox<RequestType, PollType> mailbox;
	public final PollConverter<RequestType, PollType> pollConverter;

	public GenericActor(AbstractMailbox<RequestType, PollType> mailbox,
			PollConverter<RequestType, PollType> pollConverter,
			String actorName, ActorID parentActor) {
		super(parentActor, actorName);
		Util.assertNotNull(mailbox, "mailbox");
		Util.assertNotNull(pollConverter, "pollConverter");
		this.mailbox = mailbox;
		this.pollConverter = pollConverter;
	}

	public GenericActor(AbstractMailbox<RequestType, PollType> mailbox,
			PollConverter<RequestType, PollType> pollConverter) {
		this(mailbox, pollConverter, null, null);
	}

	public GenericActor(AbstractMailbox<RequestType, PollType> mailbox) {
		this(mailbox, null, null, null);
	}

	// ----- internal stuff -----

	private class Job implements Runnable {
		public final PollType message;
		public final ActorID actorID;
		public Job(PollType message, ActorID actorID) {
			this.message = message;
			this.actorID = actorID;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorID);
			tvcKeeper.incrementBy(1);
			try {
				act(pollConverter.getMessage(message));
			} catch(Throwable error) {
				onFailure(error);
			}
		}
	}

	// ----- Actor methods -----

	public Runnable poll(ActorID actorID) {
		final PollType message = mailbox.poll();
		if (message == null) {
			return null;
		}
		return new Job(message, actorID);
	}

	public void send(RequestType message) throws MailboxException {
		mailbox.add(message);
	}

}
