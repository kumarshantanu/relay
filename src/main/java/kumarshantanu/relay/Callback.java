package kumarshantanu.relay;

/**
 * Callback handler interface. It is used to accept the processing result.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <ReturnType>
 */
public interface Callback<ReturnType> {

	/**
	 * Handle the return value.
	 * @param value
	 */
	public void onReturn(ReturnType value);

	/**
	 * Handle the thrown Exception.
	 * @param ex
	 */
	public void onException(Exception ex);

}
