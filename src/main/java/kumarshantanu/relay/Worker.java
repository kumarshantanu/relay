package kumarshantanu.relay;

/**
 * Worker that carries out a unit of work.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public interface Worker<RequestType, ReturnType> {

	/**
	 * Return true if the work is idempotent, false otherwise.
	 * @return
	 */
	public boolean isIdempotent();

	/**
	 * Execute the work.
	 * @param req
	 * @return
	 */
	public ReturnType execute(RequestType req);
	
}
