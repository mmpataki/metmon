package metmon.model.meta;

import java.beans.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import metmon.store.StoreRecord;

public class MetaRecord extends StoreRecord<String, String, MetaStoreEntry> {

	/* public view */
	Map<String, Set<String>> contexts;

	public Map<String, Set<String>> getContexts() {
		return contexts;
	}

	public void setContexts(Map<String, Set<String>> contexts) {
		this.contexts = contexts;
	}

	public MetaRecord(long ts) {
		super(ts);
		contexts = new HashMap<String, Set<String>>();
	}

	/* private stuff, compatible with store API */
	public MetaRecord() {
		this(-1);
	}

	@Override
	public void addRecord(MetaStoreEntry mse) {
		String key = mse.getKey().substring(0, mse.getKey().indexOf(0));
		String val = mse.getKey().substring(mse.getKey().indexOf(0)+1);
		Set<String> l = contexts.get(key);
		if (l == null) {
			contexts.put(key, l = new TreeSet<>());
		}
		l.add(val);
		super.addRecord(mse);
	}
	
	@Override
	@Transient
	public List<MetaStoreEntry> getRecords() {
		return super.getRecords();
	}
}
