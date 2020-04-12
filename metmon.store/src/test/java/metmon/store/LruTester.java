package metmon.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import metmon.store.smartstore.LruCache;

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
		assertEquals(0, head.getKey());
		get(1);
		assertEquals(1, head.getKey());
	}

	@Test
	public void testEviction() throws Exception {
		int x = (int)System.currentTimeMillis();
		for (int i = 0; i < CSIZE + 1; i++) {
			put(x + i, "hello");
		}
		assertNotEquals(null, get(x+1));
		assertEquals(null, get(x));
	}
	
	@Test
	public void testPromoteAndEvict() throws Exception {
		int x = (int)System.currentTimeMillis();
		for (int i = 0; i < CSIZE; i++) {
			put(x + i, "hello");
		}
		put(-1, "hello");
		assertEquals(1 + x, tail.getKey());
		get(1+x);
		assertEquals(1+x, head.getKey());
		assertEquals(-1, head.getNext().getKey());
	}
	
}
