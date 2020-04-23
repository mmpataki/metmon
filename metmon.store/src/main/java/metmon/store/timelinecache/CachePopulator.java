package metmon.store.timelinecache;

import java.util.List;

public interface CachePopulator<V> {
	
	List<V> produce(long from, long to);
	
}
