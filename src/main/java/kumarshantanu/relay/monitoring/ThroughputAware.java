package kumarshantanu.relay.monitoring;

public interface ThroughputAware {

	/**
	 * Return array of throughput count for a window of duration slots
	 * @return
	 */
	public long[] getThroughput();

	/**
	 * Return string representation of throughput, typically for diagnostic
	 * purpose
	 * @return
	 */
	public String getThroughputString();

	/**
	 * Return duration (milli-seconds) of each slot in the window
	 * @return
	 */
	public long getUnitDurationMillis();

	/**
	 * Return duration name of each slot in the window
	 * @return
	 */
	public String getUnitDurationName();

}
