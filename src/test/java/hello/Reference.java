package hello;

public class Reference<V> {

	private V value;

	public final V get() {
		return value;
	}

	public final void set(V newValue) {
		value = newValue;
	}

}
