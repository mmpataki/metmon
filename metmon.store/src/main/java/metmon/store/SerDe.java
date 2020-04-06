package metmon.store;

public interface SerDe<T> {

	T deserialize(byte[] buf);
	
	byte[] serialize(T obj);
	
}
