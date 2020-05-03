package metmon.inmemstore;

import metmon.conf.MetmonConfiguration;
import metmon.store.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> extends BaseStore<K, V, R, C> {

    NavigableMap<Long, R> S = new TreeMap<>();

    public InMemStore(MetmonConfiguration conf) {
        super(conf);
    }

    @Override
    public void open(StoreInfo<K, V> si, boolean create) throws Exception {
        /* no-op */
    }

    @Override
    public List<R> get(StoreRequest<K> req, FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr) throws Exception {
        return S.subMap(req.getFrom(), true, req.getTo(), true).values()
                .stream()
                .map(rec -> {
                    R r = rbldr.apply(rec.getTs());
                    rec.getRecords().forEach(c -> r.addRecord(cbldr.apply(c.getKey(), c.getValue())));
                    return r;
                })
                .collect(Collectors.toList());
    }

    /** TODO: decide whether to call rbldr and cbldr here */
    @Override
    public void put(R rec, ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception {
        S.put(rec.getTs(), rec);
    }

    @Override
    public void delete(DeleteRequest<K> del) throws Exception {
        S.remove(del.getTs());
    }

    @Override
    public void close() throws Exception {
        S.clear();
    }
}