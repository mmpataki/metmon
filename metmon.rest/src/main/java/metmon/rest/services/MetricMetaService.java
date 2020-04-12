package metmon.rest.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.meta.MetaConsts;
import metmon.model.meta.MetaRecord;
import metmon.model.meta.MetaRecordRequest;
import metmon.model.meta.MetaStoreEntry;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;
import metmon.store.Store;

@Service
public class MetricMetaService {

	@Autowired
	MetmonConfiguration conf;

	@Autowired
	ProcessService PS;

	Stores<String, String, MetaRecord, MetaStoreEntry> stores;
	
	@PostConstruct
	private void init() throws Exception {
		stores = new Stores<>(conf, new SerDes.StringSerde(), new SerDes.StringSerde());
	}

	public MetaRecord getAvailableMetrics(String procGroup, String proc) throws Exception {
		Store<String, String, MetaRecord, MetaStoreEntry> store = stores.open(new ProcIdentifier(procGroup, proc),
				true);
		return store.get(new MetaRecordRequest(MetaConsts.META_KEYS_TS, MetaConsts.META_KEYS_TS_END),
				(ts) -> new MetaRecord(ts), (k, v) -> new MetaStoreEntry(k, v)).get(0);
	}

	public void sink(MetricRecord mr) throws Exception {
		PS.addProcess(mr.getId().getProcessGrp(), mr.getId().getProcess());
		Store<String, String, MetaRecord, MetaStoreEntry> store = stores.open(mr.getId(), true);
		MetaRecord mm = new MetaRecord();
		mm.setTs(MetaConsts.META_KEYS_TS);
		for (Metric m : mr.getRecords()) {
			mm.addRecord(new MetaStoreEntry(mr.getCtxt() + "\0" + m.getKey(), ""));
		}
		store.put(mm, r -> r, c -> c);
	}

}
