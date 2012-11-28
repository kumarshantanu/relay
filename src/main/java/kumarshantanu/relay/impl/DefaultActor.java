package kumarshantanu.relay.impl;

import java.util.concurrent.Future;

import kumarshantanu.relay.ActorId;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.Mailbox;
import kumarshantanu.relay.MailboxException;

/**
 * DefaultActor stores messages as-it-is in its mailbox, hence compared to
 * <tt>AmbientActor<tt> it can have a large number of messages in its mailbox
 * without memory overhead; on the flip side, it does not support the
 * interactive <tt>send(message, callback)</tt> method.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public abstract class DefaultActor<RequestType, ReturnType>
extends AbstractActor<RequestType, ReturnType> {

	public final Mailbox<RequestType> mailbox;
	public final Callback<ReturnType> callback;

	public DefaultActor(Agent agent, Callback<ReturnType> callback,
			Mailbox<RequestType> mailbox,
			String actorName, ActorId parentActor) {
		super(parentActor, actorName);
		Util.notNull(agent, "agent");
		if (callback != null) {
			this.callback = callback;
		} else {
			this.callback = new DummyCallback<ReturnType>();
		}
		if (mailbox != null) {
			this.mailbox = mailbox;
		} else {
			this.mailbox = new DefaultMailbox<RequestType>();
		}
		agent.register(this);
	}

	public DefaultActor(Agent agent) {
		this(agent, null, null, null, null);
	}

	// ----- internal stuff -----

	private class Job implements Runnable {
		public final RequestType message;
		public final ActorId actorId;
		public Job(RequestType message, ActorId actorId) {
			this.message = message;
			this.actorId = actorId;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorId);
			try {
				tvcKeeper.incrementBy(1);
				ReturnType val = execute(message);
				try {
					callback.onReturn(val);
				} catch (Exception e) {
					// ignore callback exceptions
				}
			} catch (Exception e) {
				try {
					callback.onException(e);
				} catch (Exception f) {
					// ignore callback exceptions
				}
			}
		}
	}

	// ----- Actor methods -----

	public boolean isMailboxEmpty() {
		return mailbox.isEmpty();
	}

	public Runnable poll(ActorId actorId) {
		final RequestType message = mailbox.poll();
		if (message == null) {
			return null;
		}
		return new Job(message, actorId);
	}

	public void send(RequestType message) throws MailboxException {
		send(message, false);
	}

	public Future<ReturnType> send(RequestType message, boolean returnFuture) throws MailboxException {
		if (returnFuture == false) {
			mailbox.add(message);
			return null;
		}
		// create adapter
		MsgAdapter<RequestType, ReturnType, RequestType> adapter =
				new MsgAdapter<RequestType, ReturnType, RequestType>() {
			public RequestType convert(RequestType request, Callback<ReturnType> callback) {
				return request;
			}
		};
		// create wrapper
		CallbackFuture<RequestType, ReturnType, RequestType> wrapper =
				new CallbackFuture<RequestType, ReturnType, RequestType>(
						callback, mailbox, message, adapter);
		// get the result message
		RequestType mailboxMessage = wrapper.message;
		// send message
		mailbox.add(mailboxMessage);
		// return the Future object
		return wrapper;
	};

	public void send(RequestType message,
			Callback<ReturnType> handler) throws MailboxException {
		throw new UnsupportedOperationException(
				"'send' with callback is not supported on this actor");
	}

	public Future<ReturnType> send(RequestType message,
			Callback<ReturnType> handler, boolean returnFuture) throws MailboxException {
		throw new UnsupportedOperationException(
				"'send' with callback is not supported on this actor");
	}
}
