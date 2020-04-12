package metmon.model.metric;

import metmon.store.StoreCell;

public class Metric extends StoreCell<String, Double> {
	
	public Metric(String key, Double value) {
		super(key, value);
	}
	
	public Metric() {
	}
}
