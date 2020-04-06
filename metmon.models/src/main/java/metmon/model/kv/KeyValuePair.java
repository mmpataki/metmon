package metmon.model.kv;

import metmon.store.StoreCell;

public class KeyValuePair extends StoreCell<String, String> {

	public KeyValuePair(Object o1, Object o2) {
		super(o1.toString(), o2.toString());
	}
	
}
