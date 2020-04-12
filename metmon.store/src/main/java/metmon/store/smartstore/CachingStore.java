package metmon.store.smartstore;

import java.util.List;

import metmon.store.DeleteRequest;
import metmon.store.FromStoreCell;
import metmon.store.FromStoreRecord;
import metmon.store.Store;
import metmon.store.StoreCell;
import metmon.store.StoreInfo;
import metmon.store.StoreRecord;
import metmon.store.StoreRequest;
import metmon.store.ToStoreCell;
import metmon.store.ToStoreRecord;

public class CachingStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>>
		implements Store<K, V, R, C> {

	Store<K, V, R, C> delegate;
	LruCache<Long, R> C = new LruCache<>(1024);

	public CachingStore(Store<K, V, R, C> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void open(StoreInfo<K, V> si, boolean create) throws Exception {
		delegate.open(si, create);
	}

	@Override
	public List<R> get(StoreRequest<K> req, FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr)
			throws Exception {
		R rec = C.get(req.getFrom());
		if (rec == null) {
			rec = delegate.get(req, rbldr, cbldr).get(0);
			C.put(req.getFrom(), rec);
		}
		return rec;
	}

	@Override
	public void put(R rec, ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(DeleteRequest<K> del) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

}
