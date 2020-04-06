package metmon.rest.services;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import metmon.conf.MetmonConfiguration;
import metmon.model.metric.ProcIdentifier;
import metmon.store.SerDe;
import metmon.store.Store;
import metmon.store.StoreInfo;
import metmon.utils.ReflectionUtils;

class Stores<K, V> {

	Map<String, Map<String, Store<K, V>>> stores;
	Constructor<Store<K, V>> ctor;
	MetmonConfiguration conf;
	private SerDe<K> kSerde;
	private SerDe<V> vSerde;

	@SuppressWarnings("unchecked")
	public Stores(MetmonConfiguration conf, SerDe<K> kSerde, SerDe<V> vSerde) throws Exception {
		stores = new HashMap<>();
		this.conf = conf;
		this.kSerde = kSerde;
		this.vSerde = vSerde;

		ctor = (Constructor<Store<K, V>>) ReflectionUtils.getCtor(
				conf.get(MetmonConfiguration.STORAGE_BACKEND_CLASS, MetmonConfiguration.STORAGE_BACKEND_CLASS_DEFAULT),
				MetmonConfiguration.class);

	}

	Store<K, V> open(ProcIdentifier u, boolean create) throws Exception {
		Map<String, Store<K, V>> groupMap = null;
		if (!stores.containsKey(u.getProcessGrp())) {
			synchronized (stores) {
				if (!stores.containsKey(u.getProcessGrp())) {
					stores.put(u.getProcessGrp(), new HashMap<>());
				}
			}
		}
		groupMap = stores.get(u.getProcessGrp());
		if (!groupMap.containsKey(u.getProcess())) {
			synchronized (groupMap) {
				if (!groupMap.containsKey(u.getProcess())) {
					Store<K, V> store = ctor.newInstance(conf);
					StoreInfo<K, V> si = new StoreInfo<>(u.getProcessGrp(), u.getProcess(), conf, kSerde, vSerde);
					store.open(si, create);
					groupMap.put(u.getProcess(), store);
				}
			}
		}
		/* if we are here, we should have that object */
		return stores.get(u.getProcessGrp()).get(u.getProcess());
	}

}
