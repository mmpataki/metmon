package metmon.store;

import metmon.conf.MetmonConfiguration;

public abstract class BaseStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>>
		implements Store<K, V, R, C> {

	MetmonConfiguration conf;

	public BaseStore(MetmonConfiguration conf) {
		this.conf = conf;
	}

}
