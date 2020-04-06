package metmon.store;

import metmon.conf.MetmonConfiguration;

public abstract class BaseStore<K, V> implements Store<K, V> {

	MetmonConfiguration conf;
	
	public BaseStore(MetmonConfiguration conf) {
		this.conf = conf;
	}
	
}
