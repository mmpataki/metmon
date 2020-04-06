package metmon.rest.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.kv.KeyValuePair;
import metmon.model.kv.KeyValueRecord;
import metmon.model.kv.KeyValueRequest;
import metmon.model.meta.MetaConsts;
import metmon.model.meta.MetaRecord;
import metmon.model.meta.MetaRecordRequest;
import metmon.model.meta.MetaStoreEntry;
import metmon.model.meta.View;
import metmon.model.meta.Views;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;
import metmon.store.FromStoreCell;
import metmon.store.FromStoreRecord;
import metmon.store.Store;
import metmon.store.StoreCell;
import metmon.store.StoreRecord;

@Service
public class MetaService {

	@Autowired
	MetmonConfiguration conf;

	Stores<String, String> stores;

	Store<String, String> AS;

	@PostConstruct
	private void init() throws Exception {
		stores = new Stores<>(conf, new SerDes.StringSerde(), new SerDes.StringSerde());
		AS = stores.open(new ProcIdentifier("appConfig", "cf1"), true);
	}

	public MetaRecord getAvailableMetrics(String procGroup, String proc) throws Exception {
		FromStoreRecord<String, String, MetaRecord, MetaStoreEntry> f = (ts) -> new MetaRecord(ts);
		return produce(new ProcIdentifier(procGroup, proc), MetaConsts.META_KEYS_TS, MetaConsts.META_KEYS_TS_END, f,
				(k, v) -> new MetaStoreEntry(k, v)).get(0);
	}

	public List<Views> getAvailableViews(String procGroup, String proc) throws Exception {
		ProcIdentifier pId = new ProcIdentifier(procGroup, proc);
		return produce(pId, MetaConsts.META_VIEWS_TS, MetaConsts.META_VIEWS_TS_END,
				(FromStoreRecord<String, String, Views, View>) (ts) -> new Views(), (k, v) -> new View(k, v));
	}

	/* internal usage */
	private <R extends StoreRecord<String, String, C>, C extends StoreCell<String, String>> List<R> produce(
			ProcIdentifier pId, long from, long to, FromStoreRecord<String, String, R, C> rbldr,
			FromStoreCell<String, String, C> cbldr) throws Exception {
		Store<String, String> store = stores.open(pId, true);
		return store.get(new MetaRecordRequest(from, to), rbldr, cbldr);
	}

	public void sink(MetricRecord<String, Double> mr) throws Exception {
		addProcess(mr.getId().getProcessGrp(), mr.getId().getProcess());
		Store<String, String> store = stores.open(mr.getId(), true);
		MetaRecord mm = new MetaRecord();
		mm.setTs(MetaConsts.META_KEYS_TS);
		for (Metric<String, Double> m : mr.getRecords()) {
			mm.addRecord(new MetaStoreEntry(mr.getCtxt() + "\0" + m.getKey(), ""));
		}
		store.put(mm, r -> r, c -> c);
	}

	public void addProcess(String procGroup, String proc) throws Exception {

		/* add a process-group entry */
		KeyValueRecord pgrpe = new KeyValueRecord(MetaConsts.META_PROC_GRPS_TS);
		pgrpe.addRecord(new KeyValuePair(procGroup, ""));
		AS.put(pgrpe, r -> r, c -> c);

		/* add all process entry */
		KeyValueRecord pe = new KeyValueRecord(MetaConsts.META_PROC_TS);
		pe.addRecord(new KeyValuePair(procGroup + '\0' + proc, ""));
		AS.put(pe, r -> r, c -> c);

	}

	public List<String> getProcGroups() throws Exception {
		FromStoreRecord<String, String, KeyValueRecord, KeyValuePair> f = (ts) -> new KeyValueRecord(ts);
		return AS
				.get(new KeyValueRequest(MetaConsts.META_PROC_GRPS_TS, MetaConsts.META_PROC_GRPS_END,
						Collections.emptySet()), f, (k, v) -> new KeyValuePair(k, v))
				.get(0).getRecords().stream().map(kv -> kv.getKey()).collect(Collectors.toList());
	}

	public List<String> getProcesses(String procGroup) throws Exception {
		FromStoreRecord<String, String, KeyValueRecord, KeyValuePair> f = (ts) -> new KeyValueRecord(ts);
		return AS
				.get(new KeyValueRequest(MetaConsts.META_PROC_TS, MetaConsts.META_PROC_END, Collections.emptySet()), f,
						(k, v) -> new KeyValuePair(k, v))
				.get(0).getRecords().stream().map(kv -> kv.getKey())
				.filter(c -> c.substring(0, c.indexOf('\0')).equals(procGroup))
				.map(d -> d.substring(d.indexOf('\0') + 1, d.length())).collect(Collectors.toList());
	}

	public void createView(Views view) throws Exception {
		Store<String, String> store = stores.open(view.getpId(), true);
		view.setTs(MetaConsts.META_VIEWS_TS);
		store.put(view, r -> r, c -> {
			((View) c).setpId(view.getpId());
			return c;
		});
	}

}
