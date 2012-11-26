package kumarshantanu.relay.lifecycle;

import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorId;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class LifecycleAwareActor<RequestType, ReturnType>
extends AbstractLifecycleAware implements Actor<RequestType, ReturnType> {
	
	public final Actor<RequestType, ReturnType> orig;
	
	public LifecycleAwareActor(Actor<RequestType, ReturnType> orig) {
		super(orig.getActorId().getActorName());
		this.orig = orig;
		this.setState(LifecycleStateEnum.RUNNING);
	}
	
	// ----- AbstractLifecycleAware methods -----
	
	@Override
	public void execute() {
		// do nothing, because actors are not Runnable
	}
	
	// ----- Worker methods -----

	public boolean isIdempotent() {
		return orig.isIdempotent();
	}

	public ReturnType execute(RequestType req) {
		return orig.execute(req);
	}

	// ----- Actor methods -----
	
	public ActorId getActorId() {
		return orig.getActorId();
	}
	
	public boolean isMailboxEmpty() {
		return orig.isMailboxEmpty();
	}
	
	public Runnable poll(ActorId actorId) {
		if (getState() == LifecycleStateEnum.RUNNING) {
			return orig.poll(actorId);
		}
		return null;
	}

	public void send(RequestType message) throws MailboxException {
		orig.send(message);
	}

	public Future<ReturnType> send(RequestType message, boolean returnFuture)
			throws MailboxException {
		return orig.send(message, returnFuture);
	}

	public void send(RequestType message, Callback<ReturnType> handler)
			throws MailboxException {
		orig.send(message, handler);
	}

	public Future<ReturnType> send(RequestType message,
			Callback<ReturnType> handler, boolean returnFuture)
			throws MailboxException {
		return orig.send(message, handler, returnFuture);
	}

}
