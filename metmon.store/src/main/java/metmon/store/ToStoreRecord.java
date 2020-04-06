package metmon.store;

public interface ToStoreRecord<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> {
	
	StoreRecord<K, V, C> apply(R record);
	
}
