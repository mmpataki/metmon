package metmon.model.meta;

import metmon.store.StoreCell;

public class MetaStoreEntry extends StoreCell<String, String> {

	/* store only in mem */
	String ctxt;

	public MetaStoreEntry(String context, String key) {
		super(context, key);
	}

	public MetaStoreEntry(StoreCell<String, String> me) {
		this(me.getKey(), me.getValue());
	}

	public MetaStoreEntry() {
		
	}
}