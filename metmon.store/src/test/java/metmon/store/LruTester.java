package metmon.store;

import metmon.store.kvcache.LruCache;
import static org.junit.Assert.*;
import org.junit.Test;

/* need access to inner fields */
public class LruTester extends LruCache<Integer, String> {

	static final int OPS = 1000000;
	static final int CSIZE = 500;
	
	public LruTester() {
		super(CSIZE);
	}
	
	@Test
	public void perfTest() {
		long i = 0;
		while (i++ < OPS) {
			put((int) i, "hello");
		}
	}

	@Test
	public void movesToHead() {
		for (int i = 0; i < CSIZE; i++) {
			put(i, "hello");
		}
		get(0);
		assertEquals(0l, (long)head.getKey());
		get(1);
		assertEquals(1l, (long)head.getKey());
	}

	@Test
	public void testEviction() throws Exception {
		int x = (int)System.currentTimeMillis();
		for (int i = 0; i < CSIZE + 1; i++) {
			put(x + i, "hello");
		}
		assertNotEquals(null, get(x+1));
		assertNull(get(x));
	}
	
	@Test
	public void testPromoteAndEvict() throws Exception {
		int x = (int)System.currentTimeMillis();
		for (int i = 0; i < CSIZE; i++) {
			put(x + i, "hello");
		}
		put(-1, "hello");
		assertEquals(1l + x, (long)tail.getKey());
		get(1+x);
		assertEquals(1l + x, (long)head.getKey());
		assertEquals(-1l, (long)head.getNext().getKey());
	}
	
}
