package metmon.store;

public interface ToStoreCell<K, V, T extends StoreCell<K, V>> {

	StoreCell<K, V> apply(T cell);

}
