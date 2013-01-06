package kumarshantanu.relay.impl;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicCounter {

	public static final BigInteger ONE = new BigInteger("1");

	private final AtomicReference<BigInteger> VALUE =
			new AtomicReference<BigInteger>(new BigInteger("0"));

	public BigInteger get() {
		return VALUE.get();
	}

	public void set(BigInteger newValue) {
		VALUE.set(newValue);
	}

	public BigInteger incrementAndGet() {
		BigInteger old;
		BigInteger inc;
		do {
			old = VALUE.get();
			inc = old.add(ONE);
		} while(!VALUE.compareAndSet(old, inc));
		return inc;
	}

}
