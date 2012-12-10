package kumarshantanu.relay.lifecycle;

import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class LifecycleAwareActor<RequestType, ReturnType>
extends AbstractLifecycleAware implements Actor<RequestType, ReturnType> {
	
	public final Actor<RequestType, ReturnType> orig;
	
	public LifecycleAwareActor(Actor<RequestType, ReturnType> orig) {
		super(orig.getActorID().getActorName());
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
	
	public ActorID getActorID() {
		return orig.getActorID();
	}
	
	public boolean isMailboxEmpty() {
		return orig.isMailboxEmpty();
	}
	
	public Runnable poll(ActorID actorID) {
		if (getState() == LifecycleStateEnum.RUNNING) {
			return orig.poll(actorID);
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

}
