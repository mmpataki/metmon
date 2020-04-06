package metmon.store;

import java.util.List;

/**
 * Store is the persistence layer of the application. These store key value
 * pairs in the underlying storage system. They are initialized per context.
 * 
 * Store developers should never implement this directly, they should subclass
 * {@link metmon.store.BaseStore}
 */
public interface Store<K, V> {

	/**
	 * Initialize/Open the store, create it if specified.
	 * 
	 * @param si     : Store Info
	 * @param create : Should we create the store if not present
	 * @throws Exception
	 */
	void open(StoreInfo<K, V> si, boolean create) throws Exception;

	<R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> List<R> get(StoreRequest<K> req,
			FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr) throws Exception;

	<R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> void put(R rec, 
			ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception;

	void close() throws Exception;

}
