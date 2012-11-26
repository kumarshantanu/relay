package kumarshantanu.relay;

import kumarshantanu.relay.impl.Util;

/**
 * Identification information about actor.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 */
public class ActorId {
	
	public final String actorName;
	
	public ActorId(String name) {
		this.actorName = name;
	}

	public String getJvmId() {
		return Util.JVM_ID;
	}
	
	public String getActorName() {
		return actorName;
	}
	
	@Override
	public String toString() {
		return "" + getJvmId() + "/" + getActorName();
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ActorId &&
				toString().equals(obj.toString());
	}

}
