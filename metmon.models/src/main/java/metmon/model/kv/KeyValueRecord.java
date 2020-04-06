package metmon.model.kv;

import metmon.store.StoreRecord;

public class KeyValueRecord extends StoreRecord<String, String, KeyValuePair> {

	public KeyValueRecord(long ts) {
		super(ts);
	}

	public KeyValueRecord() {
		super(-1);
	}
}
