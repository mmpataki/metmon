package metmon.model.meta;

import java.beans.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import metmon.store.StoreRecord;

public class MetaRecord extends StoreRecord<String, Short, MetaStoreEntry> {

	/* public view */
	Map<String, Short> keys = new HashMap<>();

	public MetaRecord(long ts) {
		super(ts);
	}

	public Map<String, Short> getKeys() {
		return keys;
	}

	public void setKeys(Map<String, Short> keys) {
		keys.forEach((k,  v) -> addRecord(new MetaStoreEntry(k, v)));
	}

	/* private stuff, compatible with store API */
	public MetaRecord() {
		this(-1);
	}

	@Override
	public void addRecord(MetaStoreEntry mse) {
		keys.put(mse.getKey(), mse.getValue());
		super.addRecord(mse);
	}

	@Override
	@Transient
	public List<MetaStoreEntry> getRecords() {
		return super.getRecords();
	}
}
