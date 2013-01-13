package kumarshantanu.relay.impl;

import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class AbstractActorDecorator<RequestType, ReturnType> implements Actor<RequestType, ReturnType> {

	protected final Actor<RequestType, ReturnType> orig;

	public AbstractActorDecorator(Actor<RequestType, ReturnType> orig) {
		this.orig = orig;
	}

	public ActorID getActorID() { return orig.getActorID(); }
	public boolean isIdempotent() { return orig.isIdempotent(); }
	public ReturnType act(RequestType req) { return orig.act(req); }

	public boolean isMailboxEmpty() { return orig.isMailboxEmpty(); }
	public Runnable poll(ActorID actorID) { return orig.poll(actorID); }
	public void send(RequestType message) throws MailboxException { orig.send(message); }
	public Future<ReturnType> send(RequestType message, boolean returnFuture)
			throws MailboxException { return orig.send(message, returnFuture); }

	public LifecycleStateEnum getState() { return orig.getState(); }
	public void suspend() { orig.suspend(); }
	public void resume() { orig.resume(); }
	public void stop() { orig.stop(); }
	public void forceStop() { orig.forceStop(); }

}
