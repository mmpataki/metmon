package metmon.model.meta;

import metmon.store.StoreCell;

public class MetaStoreEntry extends StoreCell<String, Short> {

	/* store only in mem */
	String ctxt;

	public MetaStoreEntry(String key, Short num) {
		super(key, num);
	}

	public MetaStoreEntry(StoreCell<String, Short> me) {
		this(me.getKey(), me.getValue());
	}

	public MetaStoreEntry() {
		
	}
}