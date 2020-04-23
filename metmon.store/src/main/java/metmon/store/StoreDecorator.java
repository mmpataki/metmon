package metmon.store;

import java.util.List;

import metmon.conf.MetmonConfiguration;

public class StoreDecorator<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>>
		extends BaseStore<K, V, R, C> {

	protected BaseStore<K, V, R, C> delegate;

	public StoreDecorator(MetmonConfiguration conf, BaseStore<K, V, R, C> delegate) {
		super(conf);
		this.delegate = delegate;
	}

	@Override
	public void open(StoreInfo<K, V> si, boolean create) throws Exception {
		delegate.open(si, create);
	}

	@Override
	public List<R> get(StoreRequest<K> req, FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr)
			throws Exception {
		return delegate.get(req, rbldr, cbldr);
	}

	@Override
	public void put(R rec, ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception {
		delegate.put(rec, rbldr, cbldr);
	}

	@Override
	public void delete(DeleteRequest<K> del) throws Exception {
		delegate.delete(del);
	}

	@Override
	public void close() throws Exception {
		delegate.close();
	}

}
