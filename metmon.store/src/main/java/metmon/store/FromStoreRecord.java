package metmon.store;

public interface FromStoreRecord<K, V, R extends StoreRecord<K, V, C>, C extends StoreCell<K, V>> {
	
	R apply(long ts);
	
}
