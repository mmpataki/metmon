package metmon.store;

import java.util.Set;

public interface StoreRequest<K> {

	long getFrom();
	
	long getTo();
	
	Set<K> getKeys();
	
}
