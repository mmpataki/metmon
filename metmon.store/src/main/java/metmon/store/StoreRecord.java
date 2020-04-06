package metmon.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StoreRecord<K, V, C extends StoreCell<K, V>> {
	
	long ts;
	
	List<C> records;
	
	public StoreRecord() {
		this(-1);
	}
	
	public StoreRecord(long ts) {
		super();
		this.ts = ts;
		this.records = new LinkedList<C>();
	}

	public long getTs() {
		return ts;
	}
	
	public void addRecord(C c) {
		records.add(c);
	}

	public List<C> getRecords() {
		return records;
	}

	public void setRecords(List<C> records) {
		this.records = records;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		StoreRecord<K, V, C> o = (StoreRecord<K, V, C>) obj;
		if (getTs() != o.getTs())
			return false;
		Map<K, V> om = o.getRecordMap();

		for (C m1 : o.getRecords()) {
			if (!om.containsKey(m1.getKey()))
				return false;
			if (!om.get(m1.getKey()).equals(m1.getValue()))
				return false;
		}
		return true;
	}
	
	private Map<K, V> getRecordMap() {
		Map<K, V> m = new HashMap<K, V>();
		for (C r : getRecords()) {
			m.put(r.getKey(), r.getValue());
		}
		return m;
	}
	
}
