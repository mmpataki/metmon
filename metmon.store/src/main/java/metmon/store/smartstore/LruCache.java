package metmon.store.smartstore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LruCache<K, V> {

	public class Entry {
		Entry prev, next;
		K k;
		V v;

		public Entry(Entry prev, Entry next, K k, V v) {
			super();
			this.prev = prev;
			this.next = next;
			this.k = k;
			this.v = v;
		}

		@Override
		public String toString() {
			return "[" + k + "] => " + next;
		}
		
		public K getKey() {
			return k;
		}
		
		public V getValue() {
			return v;
		}

		public Entry getPrev() {
			return prev;
		}

		public Entry getNext() {
			return next;
		}
	}
	

	int maxSize;
	protected Entry head, tail;
	protected AtomicInteger cnt = new AtomicInteger(0);
	protected Map<K, Entry> map = new HashMap<>();

	public LruCache(int maxSize) {
		this.maxSize = maxSize;
	}

	public void put(K k, V v) {
		evict();
		synchronized (this) {
			Entry e = new Entry(null, head, k, v);
			if (head != null) {
				head.prev = e;
			}
			head = e;
			if (tail == null) {
				tail = head;
			}
			map.put(k, e);
		}
		cnt.incrementAndGet();
	}

	public V get(K k) {
		Entry e = map.get(k);
		if (e == null)
			return null;
		if (e.v == null)
			map.remove(k);
		synchronized (this) {
			if (e.prev != null)
				e.prev.next = e.next;
			if (e.next != null)
				e.next.prev = e.prev;
			e.next = head;
			e.prev = null;
			head = e;
		}
		return e.v;
	}

	public void evict() {
		if (cnt.get() >= maxSize) {
			synchronized (this) {
				Entry p = tail.prev;
				p.next = null;
				tail.prev = null;
				tail.v = null;
				tail.k = null;
				tail = p;
			}
			cnt.decrementAndGet();
		}
	}
	
	public int size() {
		return cnt.get();
	}
}
