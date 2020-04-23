package metmon.store.timelinecache;

import metmon.conf.MetmonConfiguration;
import metmon.store.BaseStore;
import metmon.store.StoreCell;
import metmon.store.StoreDecorator;
import metmon.store.StoreRecord;

public class TimelineCacheStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> extends StoreDecorator<K, V, R, C> {

	public TimelineCacheStore(MetmonConfiguration conf, BaseStore<K, V, R, C> delegate) {
		super(conf, delegate);
	}

	
	
}
