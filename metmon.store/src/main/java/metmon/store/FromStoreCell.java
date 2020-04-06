package metmon.store;

public interface FromStoreCell<K, V, T extends StoreCell<K, V>> {

	T apply(K k, V v);

}
