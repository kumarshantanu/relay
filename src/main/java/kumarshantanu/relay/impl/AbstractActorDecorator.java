package kumarshantanu.relay.impl;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class AbstractActorDecorator<RequestType> implements Actor<RequestType> {

	protected final Actor<RequestType> orig;

	public AbstractActorDecorator(Actor<RequestType> orig) {
		this.orig = orig;
	}

	public ActorID getActorID() { return orig.getActorID(); }
	public boolean isIdempotent() { return orig.isIdempotent(); }
	public void act(RequestType req) { orig.act(req); }

	public Runnable poll(ActorID actorID) { return orig.poll(actorID); }
	public void send(RequestType message) throws MailboxException { orig.send(message); }

	public LifecycleStateEnum getState() { return orig.getState(); }
	public void suspend() { orig.suspend(); }
	public void resume() { orig.resume(); }
	public void stop() { orig.stop(); }
	public void forceStop() { orig.forceStop(); }

}
