package metmon.model.metric;

import metmon.store.StoreCell;

public class Metric<K, V> extends StoreCell<K, V> {
	
	public Metric(K key, V value) {
		super(key, value);
	}
	
	public Metric() {
	}
}
