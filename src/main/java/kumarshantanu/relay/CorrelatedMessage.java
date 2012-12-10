package kumarshantanu.relay;

import java.io.Serializable;


public class CorrelatedMessage<RequestType> implements Serializable {

	/**
	 * Auto generated
	 */
	private static final long serialVersionUID = -8650909889652775500L;

	public RequestType message;
	public String correlationID;

	public RequestType getMessage() {
		return message;
	}

	public String getCorrelationID() {
		return correlationID;
	}

	public void setMessage(RequestType message) {
		this.message = message;
	}

	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}

	public CorrelatedMessage(RequestType message, String correlationID) {
		this.message = message;
		this.correlationID = correlationID;
	}

	public CorrelatedMessage() { /* do nothing, required for serialization */ }

	@Override
	public String toString() {
		return "CorrelatedMessage [message=" + message + ", correlationID="
				+ correlationID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((correlationID == null) ? 0 : correlationID.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		CorrelatedMessage<RequestType> other = (CorrelatedMessage<RequestType>) obj;
		if (correlationID == null) {
			if (other.correlationID != null)
				return false;
		} else if (!correlationID.equals(other.correlationID))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}
