package kumarshantanu.relay;

import kumarshantanu.relay.impl.Util;

/**
 * Identification information about actor.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 */
public class ActorID {
	
	public final String actorName;
	
	public ActorID(String name) {
		this.actorName = name;
	}

	public String getJvmID() {
		return Util.JVM_ID;
	}
	
	public String getActorName() {
		return actorName;
	}
	
	@Override
	public String toString() {
		return "" + getJvmID() + "/" + getActorName();
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ActorID &&
				toString().equals(obj.toString());
	}

}
