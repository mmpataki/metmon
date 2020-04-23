package metmon.model.metric;

import metmon.store.StoreCell;

public class Metric extends StoreCell<Short, Double> {
	
	public Metric(Short key, Double value) {
		super(key, value);
	}
	
	public Metric() {
	}
}
