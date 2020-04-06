package metmon.store;

public class StoreCell<K, V> {

	K key;
	V value;

	public StoreCell(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public StoreCell() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		StoreCell<K, V> o = (StoreCell<K, V>) obj;
		return (key.equals(o.getKey()) && value.equals(o.getValue()));
	}
	

	@Override
	public String toString() {
		return getKey() + "=>" + getValue();
	}
}
