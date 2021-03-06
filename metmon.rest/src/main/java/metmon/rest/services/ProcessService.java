package metmon.rest.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import metmon.store.StoreCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.kv.KeyValuePair;
import metmon.model.kv.KeyValueRecord;
import metmon.model.kv.KeyValueRequest;
import metmon.model.meta.MetaConsts;
import metmon.model.metric.ProcIdentifier;
import metmon.store.FromStoreRecord;
import metmon.store.Store;

@Service
public class ProcessService {

	@Autowired
	MetmonConfiguration conf;

	Stores<String, String, KeyValueRecord, KeyValuePair> stores;

	Store<String, String, KeyValueRecord, KeyValuePair> AS;

	@PostConstruct
	private void init() throws Exception {
		stores = new Stores<>("processtore", conf, new SerDes.StringSerde(), new SerDes.StringSerde());
		AS = stores.open(new ProcIdentifier("appConfig", "cf1"), true);
	}

	public void addProcess(ProcIdentifier pId) throws Exception {

		/* add a process-group entry */
		KeyValueRecord pgrpe = new KeyValueRecord(MetaConsts.META_PROC_GRPS_TS);
		pgrpe.addRecord(new KeyValuePair(pId.getProcessGrp(), ""));
		AS.put(pgrpe, r -> r, c -> c);

		/* add all process entry */
		KeyValueRecord pe = new KeyValueRecord(MetaConsts.META_PROC_TS);
		pe.addRecord(new KeyValuePair(pId.getProcessGrp() + '\0' + pId.getProcess(), ""));
		AS.put(pe, r -> r, c -> c);

	}

	public List<String> getProcGroups() throws Exception {
		FromStoreRecord<String, String, KeyValueRecord, KeyValuePair> f = KeyValueRecord::new;
		return AS
				.get(new KeyValueRequest(MetaConsts.META_PROC_GRPS_TS, MetaConsts.META_PROC_GRPS_END,
						Collections.emptySet()), f, KeyValuePair::new)
				.get(0).getRecords().stream().map(StoreCell::getKey).collect(Collectors.toList());
	}

	public List<String> getProcesses(String procGroup) throws Exception {
		FromStoreRecord<String, String, KeyValueRecord, KeyValuePair> f = KeyValueRecord::new;
		return AS
				.get(new KeyValueRequest(MetaConsts.META_PROC_TS, MetaConsts.META_PROC_END, Collections.emptySet()), f,
						KeyValuePair::new)
				.get(0).getRecords().stream().map(StoreCell::getKey)
				.filter(c -> c.substring(0, c.indexOf('\0')).equals(procGroup))
				.map(d -> d.substring(d.indexOf('\0') + 1, d.length())).collect(Collectors.toList());
	}

}
