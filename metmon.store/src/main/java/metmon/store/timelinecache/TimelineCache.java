package metmon.store.timelinecache;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class TimelineCache<V> {

	SortedMap<Long, V> range = new TreeMap<>();
	CachePopulator<V> pop;

	public TimelineCache(long tSpan, CachePopulator<V> pop) {
		this.pop = pop;
	}

	List<V> get(long from, long to) {

		long cur = from;

		while (cur != to) {

			SortedSet<Long> S = new TreeSet<Long>();
			SortedSet<Long> foundSet = S.tailSet(cur);
			
			if(foundSet.isEmpty()) {
				/* the */
				
				
			} else {
				
			}
			
			long found = foundSet.last();

			if (found < cur) {

			} else if (found > cur) {

			} else {

			}

		}

		return null;
	}

}
