package kumarshantanu.relay.impl;

import java.util.concurrent.Future;

import kumarshantanu.relay.ActorId;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.Mailbox;
import kumarshantanu.relay.MailboxException;

/**
 * AmbientActor is meant for passing a relatively small number of coarse-grained
 * messages, potentially with interactive behaviour via the methods
 * <tt>send(message, callback)</tt> and <tt>send(message, callback, true)</tt>.
 * Note that mailbox incurs memory overhead due to storing the callback object.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public abstract class AmbientActor<RequestType, ReturnType>
extends AbstractActor<RequestType, ReturnType> {

	public final MsgAdapter<RequestType, ReturnType,
							MsgCallback<RequestType, ReturnType>> adapter =
			new MsgCallbackAdapter<RequestType, ReturnType>();

	public final Mailbox<MsgCallback<RequestType, ReturnType>> mailbox;
	public final Callback<ReturnType> callback;

	public AmbientActor(Agent agent, Callback<ReturnType> callback,
			Mailbox<MsgCallback<RequestType, ReturnType>> mailbox,
			String actorName, ActorId parentActor) {
		super(parentActor, actorName);
		Util.notNull(agent, "agent");
		this.callback = callback;
		if (mailbox != null) {
			this.mailbox = mailbox;
		} else {
			this.mailbox = new DefaultMailbox<MsgCallback<RequestType,ReturnType>>();
		}
		agent.register(this);
	}

	public AmbientActor(Agent ag) {
		this(ag, null, null, null, null);
	}

	public boolean isMailboxEmpty() {
		return mailbox.isEmpty();
	}
	
	private class Job implements Runnable {
		public final MsgCallback<RequestType, ReturnType> msgHandler;
		public final ActorId actorId;
		public Job(MsgCallback<RequestType, ReturnType> msgHandler,
				ActorId actorId) {
			this.msgHandler = msgHandler;
			this.actorId = actorId;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorId);
			try {
				tvcKeeper.incrementBy(1);
				ReturnType val = execute(msgHandler.message);
				if (msgHandler.handler != null) {
					try {
						msgHandler.handler.onReturn(val);
					} catch (Exception e) {
						// ignore callback exceptions
					}
				}
			} catch (Exception e) {
				if (msgHandler.handler != null) {
					try {
						msgHandler.handler.onException(e);
					} catch (Exception f) {
						// ignore callback exceptions
					}
				}
			}
		}
	}

	// ----- Actor methods -----

	public Runnable poll(ActorId actorId) {
		final MsgCallback<RequestType, ReturnType> msgAndHandler =
				mailbox.poll();
		if (msgAndHandler == null) {
			return null;
		}
		return new Job(msgAndHandler, actorId);
	}

	public void send(RequestType message) throws MailboxException {
		send(message, callback, false);
	};

	public Future<ReturnType> send(RequestType message, boolean returnFuture)
			throws MailboxException {
		if (!returnFuture) {
			send(message, callback);
			return null;
		}
		return send(message, callback, true);
	}

	public void send(final RequestType message,
			final Callback<ReturnType> handler) throws MailboxException {
		send(message, handler, false);
	}

	public Future<ReturnType> send(final RequestType message,
			final Callback<ReturnType> handler, boolean returnFuture) throws MailboxException {
		// short-circuit, if returnFuture is false
		if (!returnFuture) {
			mailbox.add(adapter.convert(message, handler));
			return null;
		}
		// create wrapper (this increases memory footprint of the message)
		CallbackFuture<RequestType, ReturnType,
							MsgCallback<RequestType, ReturnType>> wrapper =
				new CallbackFuture<RequestType, ReturnType, MsgCallback<RequestType, ReturnType>>(
									handler, mailbox, message, adapter);
		return send(wrapper);
	}

	public Future<ReturnType> send(CallbackFuture<RequestType, ReturnType,
				MsgCallback<RequestType, ReturnType>> handler) throws MailboxException {
		// get the result message
		MsgCallback<RequestType, ReturnType> mailboxMessage = handler.message;
		// send message
		mailbox.add(mailboxMessage);
		// return the Future object
		return handler;
	}

}
