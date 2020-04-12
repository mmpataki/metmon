package metmon.store.hbase;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import metmon.conf.MetmonConfiguration;
import metmon.store.BaseStore;
import metmon.store.DeleteRequest;
import metmon.store.FromStoreCell;
import metmon.store.FromStoreRecord;
import metmon.store.SerDe;
import metmon.store.StoreCell;
import metmon.store.ToStoreCell;
import metmon.store.StoreInfo;
import metmon.store.StoreRecord;
import metmon.store.ToStoreRecord;
import metmon.store.StoreRequest;

/* 
 * Store using HBase as storage backend.
 */
public class HBaseStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> extends BaseStore<K, V, R, C> {

	public HBaseStore(MetmonConfiguration conf) {
		super(conf);
	}

	/* per app info */
	static Configuration conf;
	static Connection cnx;
	static boolean initDone = false;

	/* per store info */
	Table table;
	TableName tn;
	byte[] cf;
	StoreInfo<K, V> si;
	SerDe<K> kSerde;
	SerDe<V> vSerde;

	void commonInit(StoreInfo<K, V> si) throws IOException {
		if (!initDone) {
			synchronized (HBaseStore.class) {
				if (!initDone) {
					conf = HBaseConfiguration.create();
					si.getConf().getAsMap().entrySet().stream().filter(e -> ((String) e.getKey()).startsWith("hbase."))
							.forEach(e -> conf.set((String) e.getKey(), (String) e.getValue()));
					cnx = ConnectionFactory.createConnection(conf);
				}
				initDone = true;
			}
		}
	}

	@Override
	public void open(StoreInfo<K, V> si, boolean create) throws Exception {
		this.si = si;
		this.kSerde = si.getkSerde();
		this.vSerde = si.getvSerde();
		commonInit(si);
		setupTable(si, create);
		setupColumnFamily(si);
	}

	private void setupColumnFamily(StoreInfo<K, V> si) throws Exception {
		if (table == null) {
			throw new IllegalStateException("The table is not initialized");
		}
		if (!table.getTableDescriptor().hasFamily(si.getProc().getBytes())) {
			cnx.getAdmin().addColumn(tn, new HColumnDescriptor(si.getProc()));
		}
		cf = si.getProc().getBytes();
	}

	private void setupTable(StoreInfo<K, V> si, boolean create) throws IOException {
		tn = TableName.valueOf(si.getProcGroup());
		if (create && !cnx.getAdmin().tableExists(tn)) {
			HTableDescriptor tdesc = new HTableDescriptor(tn);
			tdesc.addFamily(new HColumnDescriptor(si.getProc()));
			cnx.getAdmin().createTable(tdesc);
		}
		table = cnx.getTable(tn);
	}

	public void close() throws Exception {
		synchronized (HBaseStore.class) {
			table.close();
		}
	}

	@Override
	public List<R> get(StoreRequest<K> req, FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr)
			throws Exception {
		Scan s = new Scan();
		s.setStartRow(Bytes.toBytes(req.getFrom()));
		s.setStopRow(Bytes.toBytes(req.getTo()));
		s.setMaxVersions(1);

		s.addFamily(cf);

		int i = 0;
		byte[][] keys = new byte[req.getKeys().size()][];
		for (K key : req.getKeys()) {
			keys[i] = kSerde.serialize(key);
			s.addColumn(cf, keys[i]);
			i++;
		}

		List<R> list = new LinkedList<>();

		Iterator<Result> it = table.getScanner(s).iterator();
		while (it.hasNext()) {
			Result res = it.next();
			R rec = rbldr.apply(Bytes.toLong(res.getRow()));

			CellScanner cs = res.cellScanner();

			while (cs.advance()) {
				Cell c = cs.current();
				K key = kSerde.deserialize(CellUtil.cloneQualifier(c));
				if (keys.length != 0 && !req.getKeys().contains(key))
					continue;
				C cell = cbldr.apply(key, vSerde.deserialize(CellUtil.cloneValue(c)));
				rec.addRecord(cell);
			}

			list.add(rec);
		}
		return list;
	}

	@Override
	public void put(R rec, ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception {
		StoreRecord<K, V, C> r = rbldr.apply(rec);
		Put p = new Put(Bytes.toBytes(r.getTs()));
		for (C c : r.getRecords()) {
			StoreCell<K, V> sc = cbldr.apply(c);
			p.addColumn(cf, kSerde.serialize(sc.getKey()), vSerde.serialize(sc.getValue()));
		}
		table.put(p);
	}

	@Override
	public void delete(DeleteRequest<K> del) throws Exception {
		Delete d = new Delete(Bytes.toBytes(del.getTs()));
		for (K k : del.getKeys()) {
			d.addColumn(cf, kSerde.serialize(k));
		}
		table.delete(d);
	}

}
