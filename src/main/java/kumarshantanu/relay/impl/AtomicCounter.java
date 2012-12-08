package kumarshantanu.relay.impl;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicCounter {

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
			inc = old.add(new BigInteger("1"));
		} while(!VALUE.compareAndSet(old, inc));
		return inc;
	}

}
