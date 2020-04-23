package metmon.rest.services;

import javax.annotation.PostConstruct;

import metmon.model.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;
import metmon.store.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MetricMetaService {

	Logger LOG = LoggerFactory.getLogger(MetricsService.class);
	@Autowired
	MetmonConfiguration conf;

	@Autowired
	ProcessService PS;

	Stores<String, Short, MetaRecord, MetaStoreEntry> stores;

	/* TODO: Would it hurt to have a cache for these set of keys? */
	Map<ProcIdentifier, Map<String, Short>> keyCache;

	@PostConstruct
	private void init() throws Exception {
		stores = new Stores<>("metricmetastore", conf, new SerDes.StringSerde(), new SerDes.ShortSerde());
		keyCache = new ConcurrentHashMap<>();
	}

	public MetaRecord 	getAvailableMetrics(String procGroup, String proc) throws Exception {
		Store<String, Short, MetaRecord, MetaStoreEntry> store = stores.open(new ProcIdentifier(procGroup, proc),
				true);
		return store.get(new MetaRecordRequest(MetaConsts.META_KEYS_TS, MetaConsts.META_KEYS_TS_END),
				MetaRecord::new, MetaStoreEntry::new).get(0);
	}

	public Map<String, Short> registerMetrics(KeyRegisterRequest krr) throws Exception {
		Store<String, Short, MetaRecord, MetaStoreEntry> store = stores.open(krr.getpId(), true);
		keyCache.compute(krr.getpId(), (pId, keys) -> {
			if(keys == null) {
				try {
					keys = getAvailableMetrics(pId.getProcessGrp(), pId.getProcess()).getKeys();
				} catch (Exception e) {
					LOG.error("failed to get the metric keys while registering keys", e);
					keys = new ConcurrentHashMap<>();
				}
			}
			MetaRecord mm = new MetaRecord();
			mm.setTs(MetaConsts.META_KEYS_TS);
			for (String key : krr.getKeys()) {
				Map<String, Short> finalKeys = keys;
				short val = keys.compute(key, (k, v) -> (v == null ? (short) finalKeys.size() : v));
				mm.addRecord(new MetaStoreEntry(key, val));
			}
			if(!mm.getRecords().isEmpty()) {
				try {
					store.put(mm, r -> r, c -> c);
				} catch (Exception e) {
					LOG.error("Couldn't update the backend store with nre keys", e);
				}
			}
			return keys;
		});
		return keyCache.get(krr.getpId());
	}

}
