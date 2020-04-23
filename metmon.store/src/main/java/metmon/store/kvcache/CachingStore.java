package metmon.store.kvcache;

import java.util.List;

import metmon.conf.MetmonConfiguration;
import metmon.store.BaseStore;
import metmon.store.DeleteRequest;
import metmon.store.FromStoreCell;
import metmon.store.FromStoreRecord;
import metmon.store.StoreCell;
import metmon.store.StoreDecorator;
import metmon.store.StoreInfo;
import metmon.store.StoreRecord;
import metmon.store.StoreRequest;
import metmon.store.ToStoreCell;
import metmon.store.ToStoreRecord;

public class CachingStore<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>>
		extends StoreDecorator<K, V, R, C> {

	static class Pair<T1, T2> {
		T1 t1;
		T2 t2;

		public Pair(T1 t1, T2 t2) {
			super();
			this.t1 = t1;
			this.t2 = t2;
		}

		@Override
		public String toString() {
			return "Pair [t1=" + t1 + ", t2=" + t2 + "]";
		}

		public T1 getFirst() {
			return t1;
		}

		public T2 getSecond() {
			return t2;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			Pair<T1, T2> p = (Pair<T1, T2>) obj;
			return getFirst().equals(p.getFirst()) && getSecond().equals(p.getSecond());
		}

		@Override
		public int hashCode() {
			return (getFirst().hashCode() << 16) | getSecond().hashCode();
		}
	}

	LruCache<Pair<Long, Long>, List<R>> C = new LruCache<>(1024);

	public CachingStore(MetmonConfiguration conf, BaseStore<K, V, R, C> delegate) {
		super(conf, delegate);
	}

	@Override
	public void open(StoreInfo<K, V> si, boolean create) throws Exception {
		delegate.open(si, create);
	}

	@Override
	public List<R> get(StoreRequest<K> req, FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr)
			throws Exception {
		Pair<Long, Long> p = new Pair<Long, Long>(req.getFrom(), req.getTo());
		List<R> rec = C.get(p);
		if (rec == null) {
			rec = delegate.get(req, rbldr, cbldr);
			C.put(p, rec);
		}
		return rec;
	}

	@Override
	public void put(R rec, ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception {
		Pair<Long, Long> p = new Pair<>(rec.getTs(), rec.getTs() + 1);
		List<R> r = C.get(p);
		if (r == null || r.isEmpty() || !r.get(0).equals(rec))
			delegate.put(rec, rbldr, cbldr);
	}

	@Override
	public void delete(DeleteRequest<K> del) throws Exception {
		delegate.delete(del);
		C.delete(new Pair<>(del.getTs(), del.getTs() + 1));
	}

	@Override
	public void close() throws Exception {
		C.clear();
	}

}
