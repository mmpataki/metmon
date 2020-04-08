package metmon.store;

import java.util.List;

/**
 * Store is the persistence layer of the application. These store key value
 * pairs in the underlying storage system. They are initialized per process.
 * 
 * Store developers should never implement this directly, they should subclass
 * {@link metmon.store.BaseStore}
 *
 * @param <K> : Key type
 * @param <V> : Value type
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

	/**
	 * Get record(cells) from store
	 * @param <R> : user record type
	 * @param <C> : user cell type
	 * @param req : get request
	 * @param rbldr : function to convert a store record to user record. 
	 * @param cbldr : function to convert a store cell to user cell.
	 * @return : list of user level record.
	 * @throws Exception
	 */
	<R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> List<R> get(StoreRequest<K> req,
			FromStoreRecord<K, V, R, C> rbldr, FromStoreCell<K, V, C> cbldr) throws Exception;

	/**
	 * Put record(cells) in to store.
	 * @param <R> : user record type
	 * @param <C> : user cell type
	 * @param rec : user record
	 * @param rbldr : function to convert from user record to store record
	 * @param cbldr : function to convert from user cell to store cell
	 * @throws Exception
	 */
	<R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> void put(R rec, 
			ToStoreRecord<K, V, R, C> rbldr, ToStoreCell<K, V, C> cbldr) throws Exception;

	/**
	 * Delete cells from store.
	 * @param del : deletion request.
	 */
	void delete(DeleteRequest<K> del) throws Exception;
	
	void close() throws Exception;

}
