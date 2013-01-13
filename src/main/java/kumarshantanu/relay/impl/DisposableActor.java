package kumarshantanu.relay.impl;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;

public class DisposableActor<RequestType, ReturnType> extends AbstractActorDecorator<RequestType, ReturnType> {

	private final Agent agent;

	private volatile boolean disposable = false;

	public DisposableActor(Actor<RequestType, ReturnType> orig, Agent agent,
			boolean disposable) {
		super(orig);
		this.agent = agent;
		this.disposable = disposable;
	}

	public DisposableActor(Actor<RequestType, ReturnType> orig, Agent agent) {
		this(orig, agent, true);
	}

	public boolean isDisposable() {
		return disposable;
	}

	public void setDisposable(boolean disposable) {
		this.disposable = disposable;
	}

	public Runnable poll(ActorID actorID) {
		Runnable r = super.poll(actorID);
		if (r == null && disposable) {
			agent.unregister(this);
		}
		return r;
	}

}
